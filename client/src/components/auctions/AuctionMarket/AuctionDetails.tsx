import type { AuctionDto, BidDto } from '@/api/generated'
import {
  acceptAuctionMutation,
  acceptBidMutation,
  createBidMutation,
  listAuctionsQueryKey,
  rejectBidMutation,
} from '@/api/generated/@tanstack/react-query.gen'
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
import { TradeStatus, wktToLatLon } from '@/lib/utils'
import { useAuthUser } from '@/store/userStore'
import dayjs from '@/utils/dayjs-config'
import { formatPrice, formatWeight } from '@/utils/formatter'
import { useMutation, useQueryClient } from '@tanstack/react-query'
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
} from 'lucide-react'
import React, { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { MapContainer, Marker, Popup, TileLayer } from 'react-leaflet'

export type UserRole = 'buyer' | 'seller'

interface Props {
  auction: AuctionDto
  role: UserRole
  showDetails?: boolean
}

const AuctionDetailsPanel: React.FC<Props> = ({
  auction,
  showDetails = false,
  role,
}) => {
  const [amount, setAmount] = useState('')
  const [buyOpen, setBuyOpen] = useState(false)
  const [bidOpen, setBidOpen] = useState(false)

  const { t } = useTranslation()

  const sortedBids = useMemo<BidDto[]>(
    () => [...auction.bids].sort((a, b) => b.amount - a.amount),
    [auction.bids]
  )
  const isOpen = auction.status.name === TradeStatus.OPEN
  const canBid = role === 'buyer' && isOpen
  const bestBid = auction.bids.reduce(
    (max, b) => (b.amount > max ? b.amount : max),
    0
  )

  const createBidRequest = useMutation({
    ...createBidMutation(),
    onSuccess() {
      console.log('Create Bid - Success')
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
    },
    onError(error) {
      console.error('Create Bid - Invalid request ', error)
    },
  })

  const acceptBidRequest = useMutation({
    ...acceptBidMutation(),
    onSuccess() {
      console.log('Accept Bid - Success')
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
    },
    onError(error) {
      console.error('Accept Bid - Invalid request ', error)
    },
  })

  const rejectBidRequest = useMutation({
    ...rejectBidMutation(),
    onSuccess() {
      console.log('Reject Bid - Success')
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
    },
    onError(error) {
      console.error('Reject Bid - Invalid request ', error)
    },
  })

  const acceptAuctionRequest = useMutation({
    ...acceptAuctionMutation(),
    onSuccess() {
      console.log('Accept Auction - Success')
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
    },
    onError(error) {
      console.error('Accept Auction - Invalid request ', error)
    },
  })

  const user = useAuthUser()

  const queryClient = useQueryClient()

  const handleSubmitBid = () => {
    const value = Number(amount)
    if (!value || value <= 0) return

    createBidRequest.mutate({
      body: {
        amount: value,
        auctionId: auction.id,
        traderId: user.id,
      },
    })

    setAmount('')
    setBidOpen(false)
  }

  const handleSubmitBuyNow = async (buyNowPrice: number | undefined) => {
    if (!buyNowPrice) return

    // The "buy now" action consists of adding a new bid and
    // accepting it

    const newBid = await createBidRequest.mutateAsync({
      body: {
        amount: buyNowPrice,
        auctionId: auction.id,
        traderId: user.id,
      },
    })

    acceptBidRequest.mutate({ path: { bidId: newBid.id } })
    acceptAuctionRequest.mutate({ path: { id: auction.id } })

    setBuyOpen(false)
  }

  const handleBidAction = (
    auctionId: number,
    bidId: number,
    action: 'accept' | 'reject'
  ) => {
    if (action == 'accept') {
      acceptBidRequest.mutate({ path: { bidId: bidId } })
      acceptAuctionRequest.mutate({ path: { id: auctionId } })
    } else {
      rejectBidRequest.mutate({ path: { bidId: bidId } })
    }
  }

  // Position map
  const coords = wktToLatLon(auction.product.store.address.location)
  const mapCenter = (coords ?? [0, 0]) as [number, number]
  const endsIn = new Date(auction.expirationDate)
  const ended = endsIn < new Date()
  return (
    <div className="flex flex-col gap-6 w-full mx-auto p-4">
      {/* Header */}
      <div className="space-y-1 flex flex-wrap">
        <h2 className="text-2xl font-semibold flex items-center gap-2">
          Produit {t('database.' + auction.product.type)} · lot n°
          {auction.product.id} · enchère n°{auction.id}
        </h2>
        <div className="flex flex-wrap gap-4 text-sm text-muted-foreground ml-4">
          <span className="flex items-center gap-1">
            <UserCircle2 className="size-4" />
            {auction.trader.firstName} {auction.trader.lastName}
          </span>
        </div>
      </div>

      {/* Map */}
      {coords && (
        <div className="flex flex-col gap-4 w-full">
          {showDetails && (
            <Card className="bg-white shadow rounded-lg gap-0 py-3">
              <CardHeader>
                <CardTitle className="text-lg">Détails de l’enchère</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex flex-wrap gap-2">
                  <Badge variant="outline" className="flex items-center gap-1">
                    <Clock />
                    Expire dans&nbsp;
                    <span className="font-semibold">
                      <CountdownTimer endDate={endsIn} />
                    </span>
                  </Badge>

                  <Badge variant="default" className="flex items-center gap-1">
                    <Package className="size-4" />
                    Quantité&nbsp;
                    <span className="font-semibold">
                      {formatWeight(auction.productQuantity)}
                    </span>
                  </Badge>

                  <Badge variant="default" className="flex items-center gap-1">
                    <DollarSign className="size-4" />
                    Prix demandé&nbsp;
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
                      Meilleure offre&nbsp;
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
          <div className="h-48 w-full rounded-md overflow-hidden">
            <MapContainer
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
      <div className="flex flex-col lg:flex-row gap-4">
        {/* Left column: actions */}
        {role === 'buyer' && !ended && (
          <div className="flex-1 flex flex-col gap-6">
            <Card className="p-4 bg-neutral-100 rounded-lg shadow">
              {/* Achat immédiat */}
              {auction.options?.buyNowPrice && (
                <div className="flex flex-col items-center text-center">
                  <span className="text-base font-medium text-gray-700 mb-2">
                    Achat immédiat
                  </span>
                  <Popover open={buyOpen} onOpenChange={setBuyOpen}>
                    <PopoverTrigger asChild>
                      <Button
                        className="bg-amber-600 hover:bg-amber-700 text-white px-6"
                        onClick={() => setBuyOpen(true)}
                      >
                        <ShoppingCart className="size-4 mr-2" />
                        {formatPrice.format(auction.options.buyNowPrice)}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-64 p-4">
                      <p className="text-center text-sm mb-4">
                        Confirmer l’achat immédiat&nbsp;
                        <span className="font-semibold">
                          {formatPrice.format(auction.options.buyNowPrice)}
                        </span>
                        &nbsp;?
                      </p>
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => setBuyOpen(false)}
                        >
                          Annuler
                        </Button>
                        <Button
                          size="sm"
                          onClick={() => {
                            handleSubmitBuyNow(auction.options?.buyNowPrice)
                          }}
                        >
                          Confirmer
                        </Button>
                      </div>
                    </PopoverContent>
                  </Popover>
                </div>
              )}

              <Separator />

              {/* Ajouter une enchère */}
              {canBid && (
                <div className="flex flex-col items-center text-center">
                  <span className="text-base font-medium text-gray-700 mb-2">
                    Ajouter une enchère
                  </span>
                  <Input
                    type="number"
                    min="1"
                    placeholder="Montant CFA"
                    value={amount}
                    onChange={e => setAmount(e.target.value)}
                    className="mb-3 w-full bg-white"
                  />
                  <Popover open={bidOpen} onOpenChange={setBidOpen}>
                    <PopoverTrigger asChild>
                      <Button
                        disabled={!amount}
                        className="w-full px-6"
                        onClick={() => setBidOpen(true)}
                      >
                        <PlusCircle className="size-4 mr-2" />
                        Placer l’offre
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-64 p-4">
                      <p className="text-center text-sm mb-4">
                        Confirmer votre offre de&nbsp;
                        <span className="font-semibold">
                          {formatPrice.format(Number(amount) || 0)}
                        </span>
                        &nbsp;?
                      </p>
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => {
                            setAmount('')
                            setBidOpen(false)
                          }}
                        >
                          Annuler
                        </Button>
                        <Button size="sm" onClick={handleSubmitBid}>
                          Confirmer
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
          <div>
            <Card className="p-4 bg-neutral-100 rounded-lg shadow">
              <div className="flex flex-col items-center text-center">
                <span className="text-base font-medium text-gray-700 mb-2">
                  Enchère terminée
                </span>
                <div className="text-sm text-gray-500">
                  Cette enchère est maintenant terminée.
                </div>
              </div>
            </Card>
          </div>
        )}
        {/* Separator */}
        {/* Right column: liste des bids */}
        <div className="flex-2">
          <Card className="overflow-hidden">
            <CardHeader>
              <CardTitle className="text-lg text-center">
                {sortedBids.length}{' '}
                {sortedBids.length <= 1 ? 'offre reçue' : 'offres reçues'}
              </CardTitle>
            </CardHeader>
            <CardContent className="max-h-80 overflow-y-auto divide-y bg-neutral-100 p-2">
              {sortedBids.length === 0 ? (
                <div className="text-center py-8 text-gray-500">
                  Aucune offre pour le moment.
                </div>
              ) : (
                sortedBids.map(bid => (
                  <div
                    key={bid.id}
                    className="flex items-center justify-between py-2"
                  >
                    <div>
                      <div className="font-medium">
                        {bid.trader.firstName} {bid.trader.lastName}
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
                        isOpen &&
                        bid.status.name === TradeStatus.OPEN && (
                          <div className="flex gap-1">
                            {/* Accept */}
                            <Popover>
                              <PopoverTrigger asChild>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  className="flex items-center px-2 py-1 bg-green-700 text-white border-green-200"
                                >
                                  <CheckCircle className="h-3 w-3 mr-1" />{' '}
                                  Accepter
                                </Button>
                              </PopoverTrigger>
                              <PopoverContent className="w-48 p-2">
                                <p className="text-sm text-center mb-2">
                                  Accepter cette offre ?
                                </p>
                                <div className="flex justify-end gap-2">
                                  <Button variant="ghost" size="sm">
                                    Annuler
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
                                    Oui
                                  </Button>
                                </div>
                              </PopoverContent>
                            </Popover>
                            {/* Reject */}
                            <Popover>
                              <PopoverTrigger asChild>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  className="flex items-center px-2 py-1 bg-red-600 text-white border-red-200"
                                >
                                  <XCircle className="h-3 w-3 mr-1" /> Refuser
                                </Button>
                              </PopoverTrigger>
                              <PopoverContent className="w-48 p-2">
                                <p className="text-sm text-center mb-2">
                                  Refuser cette offre ?
                                </p>
                                <div className="flex justify-end gap-2">
                                  <Button variant="ghost" size="sm">
                                    Annuler
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
                                    Oui
                                  </Button>
                                </div>
                              </PopoverContent>
                            </Popover>
                          </div>
                        )}
                      {bid.status.name !== TradeStatus.OPEN && (
                        <Badge variant="outline" className="text-xs">
                          {bid.status.name}
                        </Badge>
                      )}
                    </div>
                  </div>
                ))
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}

export default AuctionDetailsPanel
