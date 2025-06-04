import { listAuctionsOptions } from '@/api/generated/@tanstack/react-query.gen'
import EmptyState from '../../EmptyState'
import AuctionCard from './AuctionCard'
import AuctionMap from './AuctionMap'
import { ProductType, type AuctionDto } from '@/api/generated'
import FiltersPanel from '@/components/FiltersPanel'
import PaginationControls from '@/components/PaginationControls'
import AuctionDetails from '@/components/auctions/AuctionMarket/AuctionDetails'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet'
import {
  Table,
  TableBody,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { ToggleGroup, ToggleGroupItem } from '@/components/ui/toggle-group'
import { useMediaQuery } from '@/hooks/use-mobile'
import dayjs from '@/utils/dayjs-config'
import { useQuery } from '@tanstack/react-query'
import {
  Apple,
  ArrowLeft,
  LayoutGrid,
  List as ListIcon,
  Map as MapIcon,
  Nut,
  SlidersHorizontal,
} from 'lucide-react'
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import LoadingState from '@/components/LoadingState'
import { useAuthUser } from '@/store/userStore'
import { TradeStatus, getPricePerKg } from '@/lib/utils'
import AuctionTrend from './AuctionTrend'

export type ViewMode = 'cards' | 'table' | 'map'
export type UserRole = 'buyer' | 'seller'
export type MarketMode = 'marketplace' | 'my-purchases' | 'my-sales'

export const sortOptions = [
  { value: 'endDate-asc', label: 'sort.expiration_asc' },
  { value: 'endDate-desc', label: 'sort.expiration_desc' },
  { value: 'price-asc', label: 'sort.price_asc' },
  { value: 'price-desc', label: 'sort.price_desc' },
] as const
export type SortOptionValue = (typeof sortOptions)[number]['value']

export const perPage = 12

interface MarketplaceProps {
  marketMode?: MarketMode
  userRole: UserRole
  filterByAuctionStatus?: boolean
}

const AuctionMarketplace: React.FC<MarketplaceProps> = ({
  marketMode = 'marketplace',
  userRole,
  filterByAuctionStatus,
}) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')
  const { t } = useTranslation()
  const user = useAuthUser()

  // Queries
  let buyerId = undefined
  let traderId = undefined
  let auctionStatus = undefined

  if (marketMode === 'my-purchases') {
    buyerId = user.id // query all auctions where user placed bids
  } else if (marketMode === 'my-sales') {
    traderId = user.id // query all auctions where user is the seller
  } else if (marketMode === 'marketplace') {
    auctionStatus = TradeStatus.OPEN // default to open auctions
  }

  const listAuctionsQueryOptions = () => ({
    ...listAuctionsOptions({
      query: { buyerId: buyerId, traderId: traderId, status: auctionStatus },
    }),
    staleTime: 10_000,
  })

  const { data: auctions, isLoading: isAuctionsLoading } = useQuery({
    ...listAuctionsQueryOptions(),
  })

  // UI state
  const [viewMode, setViewMode] = useState<ViewMode>('cards')
  const [sheetOpen, setSheetOpen] = useState(false)
  const [dialogAuction, setDialogAuction] = useState<AuctionDto | null>(null)
  const [inlineAuction, setInlineAuction] = useState<AuctionDto | null>(null)
  const [sort, setSort] = useState<SortOptionValue>('endDate-asc')

  // Filtering
  const [filteredAuctions, setFilteredAuctions] = useState<AuctionDto[]>([])

  // Sorting
  const sorted = useMemo(() => {
    const list = [...filteredAuctions]
    switch (sort) {
      case 'price-asc':
        return list.sort(
          (a, b) =>
            getPricePerKg(a.price, a.productQuantity) -
            getPricePerKg(b.price, b.productQuantity)
        )
      case 'price-desc':
        return list.sort(
          (a, b) =>
            getPricePerKg(b.price, b.productQuantity) -
            getPricePerKg(a.price, a.productQuantity)
        )
      case 'endDate-desc':
        return list.sort(
          (a, b) =>
            dayjs(b.expirationDate).valueOf() -
            dayjs(a.expirationDate).valueOf()
        )
      default:
        return list.sort(
          (a, b) =>
            dayjs(a.expirationDate).valueOf() -
            dayjs(b.expirationDate).valueOf()
        )
    }
  }, [filteredAuctions, sort])

  // Pagination
  const [currentPage, setCurrentPage] = useState(1)
  const totalPages = Math.max(1, Math.ceil(sorted.length / perPage))
  const paginated = useMemo(
    () => sorted.slice((currentPage - 1) * perPage, currentPage * perPage),
    [sorted, currentPage]
  )

  useEffect(() => setCurrentPage(1), [filteredAuctions])

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
    window.scrollTo({ top: 200, behavior: 'smooth' })
  }

  const handleFilteredDataChange = useCallback(
    (newFilteredData: AuctionDto[], selectedAuctionStatus: TradeStatus) => {
      if (
        marketMode === 'my-purchases' &&
        selectedAuctionStatus !== TradeStatus.OPEN
      ) {
        // show only auctions won by user
        newFilteredData = newFilteredData.filter(auction =>
          auction.bids.some(
            bid =>
              bid.trader.id === user.id &&
              bid.status.name === TradeStatus.ACCEPTED
          )
        )
      }
      setFilteredAuctions(newFilteredData)

      // reset view to list when user selects a filter in FilterPanel
      setInlineAuction(null)
    },
    [user.id, marketMode]
  )

  // Display inline auction on custom event (SSE notif's toast action)
  useEffect(() => {
    const handler = (event: Event) => {
      const customEvent = event as CustomEvent<{ auctionId: number }>
      if (customEvent.detail && customEvent.detail.auctionId) {
        const found = (auctions as AuctionDto[]).find(
          a => a.id === customEvent.detail.auctionId
        )
        if (found) setInlineAuction(found)
      }
    }
    window.addEventListener('auction:showInlineAuction', handler)
    return () => {
      window.removeEventListener('auction:showInlineAuction', handler)
    }
  }, [auctions])

  // Compute auctions trends
  const trends = useMemo(() => {
    const stats = {
      harvest: { volume: 0, totalWeight: 0, totalPrice: 0, avgPricePerKg: 0 },
      transformed: {
        volume: 0,
        totalWeight: 0,
        totalPrice: 0,
        avgPricePerKg: 0,
      },
    }

    if (!auctions) return stats

    for (const auction of auctions as AuctionDto[]) {
      if (
        auction.product.type !== ProductType.HARVEST &&
        auction.product.type !== ProductType.TRANSFORMED
      )
        continue

      stats[auction.product.type].volume += 1
      stats[auction.product.type].totalWeight += auction.productQuantity
      stats[auction.product.type].totalPrice += auction.price
    }

    stats.harvest.avgPricePerKg = getPricePerKg(
      stats.harvest.totalPrice,
      stats.harvest.totalWeight
    )
    stats.transformed.avgPricePerKg = getPricePerKg(
      stats.transformed.totalPrice,
      stats.transformed.totalWeight
    )

    return stats
  }, [auctions])

  // Render
  const isInCardDetail = viewMode === 'cards' && inlineAuction
  const cssCard = isInCardDetail ? 'lg:justify-start' : 'lg:justify-end'

  return (
    <>
      {/* Header */}
      <div className="mb-6 flex w-full flex-col flex-wrap items-center justify-center gap-4 sm:flex-row lg:justify-between">
        <Select value={sort} onValueChange={v => setSort(v as SortOptionValue)}>
          <SelectTrigger className="w-40">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {sortOptions.map(o => (
              <SelectItem
                key={o.value}
                value={o.value}
              >{`${t('sort.label_prefix')}${t(o.label)}`}</SelectItem>
            ))}
          </SelectContent>
        </Select>

        {marketMode !== 'marketplace' && (
          <div className="text-md text-muted-foreground w-full lg:w-[260px]">
            <div className="text-center lg:pl-4 lg:text-left">
              {t('marketplace.results_count', {
                count: filteredAuctions.length,
              })}
            </div>
          </div>
        )}

        <div className={`flex items-center ${cssCard} lg:pl-11`}>
          {isInCardDetail ? (
            <div className="pl-4">
              <Button
                variant="default"
                className="flex w-40 items-center gap-1"
                onClick={() => setInlineAuction(null)}
              >
                <ArrowLeft className="size-4" /> {t('buttons.back')}
              </Button>
            </div>
          ) : (
            <div className="flex w-full flex-col flex-wrap items-center justify-center gap-2 lg:flex-row lg:justify-end">
              {!isAuctionsLoading && auctions && (
                <>
                  <AuctionTrend
                    tooltip={t('database.harvest')}
                    icon={<Apple />}
                    volume={trends.harvest.volume}
                    price={trends.harvest.avgPricePerKg}
                    weightKg={trends.harvest.totalWeight}
                  />
                  <AuctionTrend
                    tooltip={t('database.transformed')}
                    icon={<Nut />}
                    volume={trends.transformed.volume}
                    price={trends.transformed.avgPricePerKg}
                    weightKg={trends.transformed.totalWeight}
                  />
                </>
              )}
              {/* Sorting */}
              {viewMode !== 'map' && <></>}

              {/* View Mode */}
              <ToggleGroup
                size="sm"
                type="single"
                value={viewMode}
                onValueChange={v => {
                  setViewMode(v as ViewMode)
                  setInlineAuction(null)
                  setDialogAuction(null)
                }}
                className="bg-background grid grid-cols-3 overflow-hidden rounded-lg border"
              >
                <ToggleGroupItem
                  value="cards"
                  aria-label="Grille"
                  className="hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground flex items-center justify-center py-2"
                >
                  <LayoutGrid className="mr-1 size-4" />
                  {t('view_mode.grid_label')}
                </ToggleGroupItem>
                <ToggleGroupItem
                  value="table"
                  aria-label="Liste"
                  className="hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground flex items-center justify-center py-2"
                >
                  <ListIcon className="mr-1 size-4" />
                  {t('view_mode.list_label')}
                </ToggleGroupItem>
                <ToggleGroupItem
                  value="map"
                  aria-label="Carte"
                  className="hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground flex items-center justify-center py-2"
                >
                  <MapIcon className="mr-1 size-4" />
                  {t('view_mode.map_label')}
                </ToggleGroupItem>
              </ToggleGroup>

              {/* Mobile Filters */}
              {!isDesktop && (
                <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
                  <SheetTrigger asChild>
                    <Button variant="outline">
                      <SlidersHorizontal className="mr-2 size-4" />
                      {t('filters.panel_title')}
                    </Button>
                  </SheetTrigger>
                  <SheetContent
                    side="left"
                    className="w-[300px] overflow-y-auto py-7 sm:w-[380px]"
                  >
                    <FiltersPanel
                      filterData={auctions as AuctionDto[]}
                      filterDataType="auction"
                      onFilteredDataChange={handleFilteredDataChange}
                      filterByAuctionStatus={filterByAuctionStatus}
                      filterByPrice={true}
                    />
                  </SheetContent>
                </Sheet>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="grid items-start gap-6 lg:grid-cols-[260px_1fr]">
        {isDesktop && !isAuctionsLoading && (
          <div className="bg-background sticky top-20 self-start rounded-lg border shadow-sm">
            <FiltersPanel
              filterData={auctions as AuctionDto[]}
              filterDataType="auction"
              onFilteredDataChange={handleFilteredDataChange}
              filterByAuctionStatus={filterByAuctionStatus}
              filterByPrice={true}
            />
          </div>
        )}

        {isAuctionsLoading ? (
          <LoadingState className="col-span-full" />
        ) : (
          <>
            <div className="relative w-full min-w-0">
              {/* Cards */}
              {viewMode === 'cards' && (
                <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3">
                  {inlineAuction ? (
                    <>
                      <AuctionCard
                        auction={inlineAuction}
                        layout="grid"
                        isDetail
                        role={userRole}
                        onDetails={() => {}}
                      />
                      <div className="col-span-full lg:col-span-2">
                        <AuctionDetails
                          auction={inlineAuction}
                          role={userRole}
                        />
                      </div>
                    </>
                  ) : filteredAuctions.length === 0 ? (
                    <EmptyState className="col-span-full" />
                  ) : (
                    <>
                      {paginated.map(a => (
                        <AuctionCard
                          key={a.id}
                          auction={a}
                          layout="grid"
                          role={userRole}
                          onDetails={() => setInlineAuction(a)}
                        />
                      ))}
                      {totalPages > 1 && (
                        <div className="col-span-full flex justify-center">
                          <PaginationControls
                            current={currentPage}
                            total={totalPages}
                            onChange={handlePageChange}
                          />
                        </div>
                      )}
                    </>
                  )}
                </div>
              )}

              {/* Table */}
              {viewMode === 'table' && (
                <>
                  <div className="bg-background overflow-x-auto rounded-lg border">
                    <Table className="table-auto text-sm">
                      <TableHeader className="supports-[backdrop-filter]:bg-muted/60 sticky top-0 z-10 backdrop-blur">
                        <TableRow className="h-9 bg-neutral-100">
                          <TableHead>
                            {t('product.merchandise_label')}
                          </TableHead>
                          <TableHead>{t('auction.expiration_label')}</TableHead>
                          <TableHead>{t('address.region_label')}</TableHead>
                          <TableHead>{t('form.city')}</TableHead>
                          <TableHead>{t('product.quantity_label')}</TableHead>
                          <TableHead>{t('product.quality_label')}</TableHead>
                          <TableHead className="text-right">
                            {t('product.price_label')}
                          </TableHead>
                          <TableHead className="text-right">
                            {t('auction.best_bid')}
                          </TableHead>
                          <TableHead></TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {paginated.map(a => (
                          <AuctionCard
                            key={a.id}
                            auction={a}
                            layout="row"
                            role={userRole}
                            onDetails={() => setDialogAuction(a)}
                          />
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                  {totalPages > 1 && (
                    <PaginationControls
                      current={currentPage}
                      total={totalPages}
                      onChange={setCurrentPage}
                    />
                  )}
                  {paginated.length === 0 && (
                    <EmptyState className="col-span-3" />
                  )}
                </>
              )}

              {/* Map */}
              {viewMode === 'map' && (
                <AuctionMap
                  auctions={sorted}
                  onSelect={a => setDialogAuction(a)}
                />
              )}
            </div>
          </>
        )}
      </div>

      {/* Dialog */}
      {dialogAuction && viewMode !== 'cards' && (
        <Dialog open onOpenChange={open => !open && setDialogAuction(null)}>
          <DialogTitle />
          <DialogContent className="max-h-[90vh] w-full max-w-[80vw]! overflow-y-auto">
            <AuctionDetails
              auction={dialogAuction}
              role={userRole}
              showDetails={true}
            />
            <DialogFooter>
              <Button variant="outline" onClick={() => setDialogAuction(null)}>
                {t('common.close')}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}
    </>
  )
}

export default AuctionMarketplace
