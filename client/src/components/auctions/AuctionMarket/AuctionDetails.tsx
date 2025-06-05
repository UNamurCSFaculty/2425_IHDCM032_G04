import type { ApiErrorResponse, AuctionDto, BidDto } from '@/api/generated'
import {
  acceptAuctionMutation,
  acceptBidMutation,
  createBidMutation,
  getContractOfferByCriteriaOptions,
  listAuctionsQueryKey,
  listBidsOptions,
  listBidsQueryKey,
  rejectBidMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import { ContractModal } from '@/components/ContractModal'
import { CountdownTimer } from '@/components/CountDownTimer'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { Separator } from '@/components/ui/separator'
import { TradeStatus, getPricePerKg, wktToLatLon } from '@/lib/utils'
import { useAuthUser } from '@/store/userStore'
import dayjs from '@/utils/dayjs-config'
import { formatPrice, formatWeight } from '@/utils/formatter'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import 'leaflet/dist/leaflet.css'
import {
  CheckCircle,
  Clock,
  DollarSign,
  Package,
  PlusCircle,
  ShoppingCart,
  TrendingUp,
  UserCircle2,
  XCircle,
  Loader2,
  Star,
  User,
  ScrollText,
} from 'lucide-react'
import React, { useEffect, useMemo, useState, useRef } from 'react'
import { client } from '@/api/generated/client.gen.ts'
import { useTranslation } from 'react-i18next'
import { MapContainer, Marker, Popup, TileLayer } from 'react-leaflet'
import { toast } from 'sonner'

export type UserRole = 'buyer' | 'seller'

interface Props {
  auction: AuctionDto
  role: UserRole
  showDetails?: boolean
}

const contractQueryOptions = (
  qualityId: number,
  sellerId: number,
  buyerId: number
) => ({
  ...getContractOfferByCriteriaOptions({
    query: { qualityId, sellerId, buyerId },
  }),
  staleTime: 10_000,
  retry: false,
})

const AuctionDetailsPanel: React.FC<Props> = ({
  auction,
  showDetails = false,
  role,
}) => {
  const user = useAuthUser()
  const queryClient = useQueryClient()
  const [amount, setAmount] = useState('')
  const [bidPricePerKg, setBidPricePerKg] = useState(0)
  const [contractPrice, setContractPrice] = useState(0)
  const [buyNowPopover, setBuyNowPopover] = useState(false)
  const [makeBidPopover, setMakeBidPopover] = useState(false)
  const [acceptBidPopoverIndex, setAcceptBidPopoverIndex] = useState(-1)
  const [rejectBidPopoverIndex, setRejectBidPopoverIndex] = useState(-1)
  const [isOpen, setIsOpen] = useState(false)

  const sseRef = useRef<EventSource | null>(null) // Visitor SSE

  const acceptedBid =
    auction.bids?.find(bid => bid.status!.name === TradeStatus.ACCEPTED) || null

  const { t } = useTranslation()

  const { data: bids = [], isLoading: bidsLoading } = useQuery(
    listBidsOptions({ query: { auctionId: auction.id } })
  )

  const sortedBids = useMemo<BidDto[]>(
    () => [...bids].sort((a, b) => b.amount - a.amount),
    [bids]
  )
  const canBid = role === 'buyer' && auction.status.name === TradeStatus.OPEN
  const bestBid = auction.bids.reduce(
    (max, b) => (b.amount > max ? b.amount : max),
    0
  )

  const { data: contract } = useQuery(
    contractQueryOptions(
      auction.product.qualityControl.quality.id,
      auction.trader.id,
      user.id
    )
  )

  useEffect(() => {
    if (contract) {
      const pricePerKg = getPricePerKg(auction.price, auction.productQuantity)
      if (contract.pricePerKg >= pricePerKg) {
        setContractPrice(0)
      } else {
        setContractPrice(contract.pricePerKg * auction.productQuantity)
      }
    }
  }, [contract, auction.price, auction.productQuantity])

  useEffect(() => {
    const handler = (event: Event) => {
      const customEvent = event as CustomEvent<{ auctionId: number }>
      if (customEvent.detail && customEvent.detail.auctionId === auction.id) {
        queryClient.invalidateQueries({ queryKey: listBidsQueryKey() })
        queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
      }
    }
    window.addEventListener('auction:newBid', handler)
    return () => {
      window.removeEventListener("auction:newBid", handler)
      if (sseRef.current) {
        sseRef.current.close()
        sseRef.current = null
      }
    }
  }, [auction.id, queryClient])

  useEffect(() => { // SSE visitors
    const baseUrl = client.getConfig().baseUrl?.replace(/\/$/, "") ?? ""
    const sseUrl = `${baseUrl}/api/auctions/${auction.id}/sse?visitor=true`
    const es = new window.EventSource(sseUrl, { withCredentials: true })
    sseRef.current = es

    es.addEventListener("refreshBids", () => {
      queryClient.invalidateQueries({ queryKey: listBidsQueryKey() })
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
      window.dispatchEvent(
        new CustomEvent("auction:newBid", {
          detail: { auctionId: auction.id },
        })
      )
    })
    es.addEventListener("auctionClosed", () => {
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
    })
    es.onerror = () => {
      console.error("[SSE] Erreur EventSource enchère")
    }
    return () => {
      es.close()
      sseRef.current = null
    }
  }, [auction.id, queryClient])

  useEffect(() => {
    setBidPricePerKg(
      getPricePerKg(amount ? Number(amount) : 0, auction.productQuantity)
    )
  }, [amount, auction.productQuantity])

  const createBidRequest = useMutation({
    ...createBidMutation(),
    onSuccess() {
      queryClient.invalidateQueries({ queryKey: listBidsQueryKey() })
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })

      toast.success(t('auction.bid_created_ok'), {
        duration: 3000,
      })
    },
    onError(error: ApiErrorResponse) {
      const detail =
        error && error.errors.length > 0 ? ' ' + error.errors[0].message : ''
      toast.error(
        t('auction.bid_created_fail') + ' ' + error.code + ':' + detail,
        {
          duration: 3000,
        }
      )
    },
  })

  const acceptBidRequest = useMutation({
    ...acceptBidMutation(),
    onSuccess() {
      queryClient.invalidateQueries({ queryKey: listBidsQueryKey() })
      toast.success(t('auction.form.accept_bid_ok'), {
        duration: 3000,
      })
    },
    onError(error: ApiErrorResponse) {
      toast.error(t('auction.form.accept_bid_fail') + ' (' + error.code + ')', {
        duration: 3000,
      })
    },
  })

  const rejectBidRequest = useMutation({
    ...rejectBidMutation(),
    onSuccess() {
      queryClient.invalidateQueries({ queryKey: listBidsQueryKey() })
      toast.success(t('auction.form.reject_bid_ok'), {
        duration: 3000,
      })
    },
    onError(error: ApiErrorResponse) {
      toast.error(t('auction.form.reject_bid_fail') + ' (' + error.code + ')', {
        duration: 3000,
      })
    },
  })

  const acceptAuctionRequest = useMutation({
    ...acceptAuctionMutation(),
    onSuccess() {
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
    },
    onError(error: ApiErrorResponse) {
      toast.error(
        t('auction.form.accept_auction_fail') + ' (' + error.code + ')',
        {
          duration: 3000,
        }
      )
    },
  })

  const handleSubmitBid = () => {
    const value = Number(amount)
    if (!value || value <= 0) return

    createBidRequest.mutate({
      body: { amount: value, auctionId: auction.id, traderId: user.id },
    })

    setAmount('')
    setMakeBidPopover(false)
  }

  const handleSubmitBuyNow = async (buyNowPrice: number | undefined) => {
    if (!buyNowPrice) return

    const newBid = await createBidRequest.mutateAsync({
      body: { amount: buyNowPrice, auctionId: auction.id, traderId: user.id },
    })

    acceptBidRequest.mutate({ path: { bidId: newBid.id } })
    acceptAuctionRequest.mutate({ path: { id: auction.id } })
    setBuyNowPopover(false)
  }

  const handleBidAction = (
    auctionId: number,
    bidId: number,
    action: 'accept' | 'reject'
  ) => {
    if (action === 'accept') {
      acceptBidRequest.mutate({ path: { bidId } })
      acceptAuctionRequest.mutate({ path: { id: auctionId } })
      setAcceptBidPopoverIndex(-1)
    } else {
      rejectBidRequest.mutate({ path: { bidId } })
      setRejectBidPopoverIndex(-1)
    }
  }

  const coords = wktToLatLon(auction.product.store.address.location)
  const mapCenter = (coords ?? [0, 0]) as [number, number]
  const endsIn = new Date(auction.expirationDate)
  const ended = endsIn < new Date()

  return (
    <div className="mx-auto flex w-full flex-col gap-6 p-4">
      <div className="flex flex-wrap space-y-1">
        <h2 className="flex items-center gap-2 text-2xl font-semibold">
          {t('auction.details_title_full', {
            productType: t('database.' + auction.product.type),
            productId: auction.product.id,
            auctionId: auction.id,
          })}
        </h2>
        <div className="text-muted-foreground ml-4 flex flex-wrap gap-4 text-sm">
          <span className="flex items-center gap-1">
            <UserCircle2 className="size-4" />
            {auction.trader.firstName} {auction.trader.lastName}
          </span>
        </div>
      </div>

      {/* Map */}
      {coords && (
        <div className="flex w-full flex-col gap-4">
          {showDetails && (
            <Card className="gap-0 rounded-lg bg-white py-3 shadow">
              <CardHeader>
                <CardTitle className="text-lg">
                  {t('auction.auction_details_title')}
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex flex-wrap gap-2">
                  <Badge variant="outline" className="flex items-center gap-1">
                    <Clock />
                    {t('auction.expires_in')}&nbsp;
                    <span className="font-semibold">
                      <CountdownTimer
                        status={auction.status.name}
                        endDate={endsIn}
                      />
                    </span>
                  </Badge>

                  <Badge variant="default" className="flex items-center gap-1">
                    <Package className="size-4" />
                    {t('product.quantity_label')}&nbsp;
                    <span className="font-semibold">
                      {formatWeight(auction.productQuantity)}
                    </span>
                  </Badge>

                  <Badge variant="default" className="flex items-center gap-1">
                    <DollarSign className="size-4" />
                    {t('auction.asking_price')}&nbsp;
                    <span className="font-semibold">
                      {formatPrice.format(auction.price)}
                    </span>
                  </Badge>

                  {bestBid > 0 && (
                    <Badge
                      variant="default"
                      className="flex items-center gap-1"
                    >
                      <TrendingUp className="size-4" />
                      {t('auction.best_bid')}&nbsp;
                      <span className="font-semibold">
                        {formatPrice.format(bestBid)}
                      </span>
                    </Badge>
                  )}
                </div>
              </CardContent>
            </Card>
          )}

          {/* 2. La map en dessous */}
          <div className="h-48 w-full overflow-hidden rounded-md">
            <MapContainer
              key="details-map"
              center={mapCenter}
              zoom={12}
              scrollWheelZoom={false}
              className="h-full w-full"
            >
              <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution="© OpenStreetMap"
              />
              <Marker position={mapCenter}>
                <Popup>
                  <div className="text-sm font-medium">
                    {auction.product.store.name}
                  </div>
                </Popup>
              </Marker>
            </MapContainer>
          </div>
        </div>
      )}
      <div className="flex flex-col gap-4 lg:flex-row">
        {/* Left column: actions */}
        {role === 'buyer' && !ended && (
          <div className="flex flex-1 flex-col gap-6">
            <Card className="rounded-lg bg-neutral-100 p-4 shadow">
              {/* Achat immédiat */}
              <div className="flex flex-col items-center text-center">
                <span className="mb-2 text-base font-medium text-gray-700">
                  {t('auction.buy_now_label')}
                </span>
                <Popover open={buyNowPopover} onOpenChange={setBuyNowPopover}>
                  <PopoverTrigger asChild>
                    <Button
                      className="bg-amber-600 px-6 text-white hover:bg-amber-700"
                      onClick={() => setBuyNowPopover(true)}
                    >
                      <ShoppingCart className="mr-2 size-4" />
                      {contractPrice > 0 ? (
                        <>
                          <span className="text-sm line-through opacity-70">
                            {formatPrice.format(auction.price)}
                          </span>
                          <span className="text-base font-semibold">
                            {formatPrice.format(contractPrice)}
                          </span>
                        </>
                      ) : (
                        <span className="text-base font-semibold">
                          {formatPrice.format(auction.price)}
                        </span>
                      )}
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-64 p-4">
                    <p className="mb-4 text-center text-sm">
                      {t('auction.confirm_buy_now_prompt', {
                        price: formatPrice.format(
                          contractPrice > 0 ? contractPrice : auction.price
                        ),
                      })}
                    </p>
                    <div className="flex justify-end gap-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => setBuyNowPopover(false)}
                      >
                        {t('buttons.cancel')}
                      </Button>
                      <Button
                        size="sm"
                        onClick={() =>
                          handleSubmitBuyNow(
                            contractPrice > 0 ? contractPrice : auction.price
                          )
                        }
                      >
                        {t('buttons.confirm')}
                      </Button>
                    </div>
                  </PopoverContent>
                </Popover>
                {contractPrice > 0 && (
                  <span className="mt-1 text-sm font-medium text-amber-600">
                    {t('auction.contract_price_label')}
                  </span>
                )}
              </div>

              <Separator />

              {canBid && (
                <div className="flex flex-col items-center text-center">
                  <span className="mb-2 text-base font-medium text-gray-700">
                    {t('auction.add_bid_label')}
                  </span>
                  <Input
                    type="number"
                    min="1"
                    placeholder={t('form.placeholder.bid_amount_cfa')}
                    value={amount}
                    onChange={e => setAmount(e.target.value)}
                    className="mb-3 w-full bg-white"
                  />
                  <div className="mb-2 text-xs font-semibold text-gray-500">
                    {bidPricePerKg} CFA/kg
                  </div>
                  <Popover
                    open={makeBidPopover}
                    onOpenChange={setMakeBidPopover}
                  >
                    <PopoverTrigger asChild>
                      <Button
                        disabled={!amount}
                        className="w-full px-6"
                        onClick={() => setMakeBidPopover(true)}
                      >
                        <PlusCircle className="mr-2 size-4" />
                        {t('buttons.place_bid')}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-64 p-4">
                      <p className="mb-4 text-center text-sm">
                        {t('auction.confirm_bid_prompt', {
                          amount: formatPrice.format(Number(amount) || 0),
                        })}
                      </p>
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => {
                            setAmount('')
                            setMakeBidPopover(false)
                          }}
                        >
                          {t('buttons.cancel')}
                        </Button>
                        <Button size="sm" onClick={handleSubmitBid}>
                          {t('buttons.confirm')}
                        </Button>
                      </div>
                    </PopoverContent>
                  </Popover>
                </div>
              )}
            </Card>
          </div>
        )}

        {role === 'buyer' && ended && (
          <div className="flex w-2/5 flex-col">
            <Card className="rounded-lg bg-neutral-100 p-4 shadow">
              <div className="flex flex-col">
                <span className="mb-2 text-center text-base font-medium text-gray-700">
                  {t('auction.status.ended_title')}
                </span>
                <div className="text-sm text-gray-500">
                  <p>
                    {t('auction.status.ended_message')}{' '}
                    {t('auction.status.offer_contract')}
                  </p>
                </div>

                {acceptedBid && user.id === acceptedBid.trader.id && (
                  <div className="mt-2 flex justify-center">
                    <Button onClick={() => setIsOpen(true)}>
                      <ScrollText />
                      {t('auction.table.propose_contract_button')}
                    </Button>

                    <ContractModal
                      acceptedBid={acceptedBid}
                      auction={auction}
                      isOpen={isOpen}
                      onClose={() => setIsOpen(false)}
                      onSubmit={() => setIsOpen(false)}
                    />
                  </div>
                )}
              </div>
            </Card>
          </div>
        )}

        <div className="flex-2">
          <Card className="overflow-hidden">
            <CardHeader>
              <CardTitle className="text-center text-lg">
                {t('auction.bid_count_for_lot', { count: sortedBids.length })}
              </CardTitle>
            </CardHeader>

            {bidsLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 className="text-muted-foreground size-8 animate-spin" />
              </div>
            ) : (
              <CardContent className="max-h-80 divide-y overflow-y-auto bg-neutral-100 p-2">
                {sortedBids.length === 0 ? (
                  <div className="py-8 text-center text-gray-500">
                    {t('auction.no_bids_yet')}
                  </div>
                ) : (
                  sortedBids.map(bid => (
                    <div
                      key={bid.id}
                      className="flex items-center justify-between py-2"
                    >
                      <div>
                        <div className="font-medium">
                          {bid.trader.id === user.id ? (
                            <div className="flex items-center space-x-2">
                              <b className="text-green-600">
                                {bid.trader.firstName} {bid.trader.lastName}
                              </b>
                              <User size={18} className="text-green-600" />
                            </div>
                          ) : (
                            <>
                              {bid.trader.firstName} {bid.trader.lastName}
                            </>
                          )}
                        </div>
                        <div className="text-xs text-gray-600">
                          {dayjs(bid.creationDate).fromNow()}
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <span className="font-semibold">
                          {formatPrice.format(bid.amount)}
                        </span>
                        {role === 'seller' &&
                          auction.status.name === TradeStatus.OPEN &&
                          bid.status.name === TradeStatus.OPEN && (
                            <div className="flex gap-1">
                              <Popover open={acceptBidPopoverIndex === bid.id}>
                                <PopoverTrigger asChild>
                                  <Button
                                    size="sm"
                                    variant="outline"
                                    className="flex items-center border-green-200 bg-green-700 px-2 py-1 text-white"
                                    onClick={() =>
                                      setAcceptBidPopoverIndex(bid.id)
                                    }
                                  >
                                    <CheckCircle className="mr-1 h-3 w-3" />
                                    {t('buttons.accept')}
                                  </Button>
                                </PopoverTrigger>
                                <PopoverContent className="w-48 p-2">
                                  <p className="mb-2 text-center text-sm">
                                    {t('auction.accept_bid_prompt')}
                                  </p>
                                  <div className="flex justify-end gap-2">
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      onClick={() =>
                                        setAcceptBidPopoverIndex(-1)
                                      }
                                    >
                                      {t('buttons.cancel')}
                                    </Button>
                                    <Button
                                      size="sm"
                                      onClick={() =>
                                        handleBidAction(
                                          auction.id,
                                          bid.id,
                                          'accept'
                                        )
                                      }
                                    >
                                      {t('common.yes')}
                                    </Button>
                                  </div>
                                </PopoverContent>
                              </Popover>

                              <Popover open={rejectBidPopoverIndex === bid.id}>
                                <PopoverTrigger asChild>
                                  <Button
                                    size="sm"
                                    variant="outline"
                                    className="flex items-center border-red-200 bg-red-600 px-2 py-1 text-white"
                                    onClick={() =>
                                      setRejectBidPopoverIndex(bid.id)
                                    }
                                  >
                                    <XCircle className="mr-1 h-3 w-3" />
                                    {t('buttons.reject')}
                                  </Button>
                                </PopoverTrigger>
                                <PopoverContent className="w-48 p-2">
                                  <p className="mb-2 text-center text-sm">
                                    {t('auction.reject_bid_prompt')}
                                  </p>
                                  <div className="flex justify-end gap-2">
                                    <Button
                                      variant="ghost"
                                      size="sm"
                                      onClick={() =>
                                        setRejectBidPopoverIndex(-1)
                                      }
                                    >
                                      {t('buttons.cancel')}
                                    </Button>
                                    <Button
                                      size="sm"
                                      variant="destructive"
                                      onClick={() =>
                                        handleBidAction(
                                          auction.id,
                                          bid.id,
                                          'reject'
                                        )
                                      }
                                    >
                                      {t('common.yes')}
                                    </Button>
                                  </div>
                                </PopoverContent>
                              </Popover>
                            </div>
                          )}
                        {bid.status.name !== TradeStatus.OPEN && (
                          <Badge
                            variant="outline"
                            className={`text-xs ${bid.status.name === TradeStatus.ACCEPTED ? 'bg-yellow-400' : 'bg-gray-200'}`}
                          >
                            {bid.status.name === TradeStatus.ACCEPTED && (
                              <Star />
                            )}
                            {bid.status.name}
                          </Badge>
                        )}
                      </div>
                    </div>
                  ))
                )}
              </CardContent>
            )}
          </Card>
        </div>
      </div>
    </div>
  )
}

export default AuctionDetailsPanel
