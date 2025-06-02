import EmptyState from '../../EmptyState'
import AuctionCard from './AuctionCard'
import AuctionMap from './AuctionMap'
import type { AuctionDto } from '@/api/generated'
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
import { TradeStatus } from '@/lib/utils'
import { useAuthUser } from '@/store/userStore'
import dayjs from '@/utils/dayjs-config'
import {
  ArrowLeft,
  LayoutGrid,
  List as ListIcon,
  Map as MapIcon,
  SlidersHorizontal,
} from 'lucide-react'
import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'

export type ViewMode = 'cards' | 'table' | 'map'
export type UserRole = 'buyer' | 'seller'

export const sortOptions = [
  { value: 'endDate-asc', label: 'sort.expiration_asc' },
  { value: 'endDate-desc', label: 'sort.expiration_desc' },
  { value: 'price-asc', label: 'sort.price_asc' },
  { value: 'price-desc', label: 'sort.price_desc' },
] as const
export type SortOptionValue = (typeof sortOptions)[number]['value']

export const perPage = 12

interface MarketplaceProps {
  auctions: AuctionDto[]
  userRole: UserRole
  filterByAuctionStatus?: boolean
}

const AuctionMarketplace: React.FC<MarketplaceProps> = ({
  auctions,
  userRole,
  filterByAuctionStatus,
}) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')
  const { t } = useTranslation()
  const user = useAuthUser()

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
        return list.sort((a, b) => a.price - b.price)
      case 'price-desc':
        return list.sort((a, b) => b.price - a.price)
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
    (newFilteredData: AuctionDto[]) => {
      if (userRole === 'buyer') {
        // filter only auctions that the user won
        newFilteredData = newFilteredData.filter(auction =>
          auction.bids.some(
            bid =>
              bid.status.name === TradeStatus.ACCEPTED &&
              bid.trader.id === user.id
          )
        )
      }
      setFilteredAuctions(newFilteredData)
    },
    []
  )

  // Display inline auction on custom event (SSE notif's toast action)
  useEffect(() => {
    const handler = (event: Event) => {
      const customEvent = event as CustomEvent<{ auctionId: number }>
      if (customEvent.detail && customEvent.detail.auctionId) {
        const found = auctions.find(a => a.id === customEvent.detail.auctionId)
        if (found) setInlineAuction(found)
      }
    }
    window.addEventListener('auction:showInlineAuction', handler)
    return () => {
      window.removeEventListener('auction:showInlineAuction', handler)
    }
  }, [auctions])

  // Render
  const isInCardDetail = viewMode === 'cards' && inlineAuction
  const cssCard = isInCardDetail ? 'lg:justify-start' : 'lg:justify-end'

  return (
    <>
      {/* Header */}
      <div className="mb-6 flex w-full flex-col flex-wrap items-center justify-center gap-4 sm:flex-row lg:justify-between">
        <div className="text-md text-muted-foreground w-full lg:w-[260px]">
          <div className="text-center lg:pl-4 lg:text-left">
            {t('marketplace.results_count', {
              count: filteredAuctions.length,
            })}
          </div>
        </div>
        <div className={`flex items-center ${cssCard} lg:pl-11`}>
          {isInCardDetail ? (
            <div className="pl-4">
              <Button
                variant="outline"
                className="flex w-40 items-center gap-1"
                onClick={() => setInlineAuction(null)}
              >
                <ArrowLeft className="size-4" /> {t('buttons.back')}
              </Button>
            </div>
          ) : (
            <div className="flex w-full flex-col flex-wrap items-center justify-center gap-2 lg:flex-row lg:justify-end">
              {/* Sorting */}
              {viewMode !== 'map' && (
                <Select
                  value={sort}
                  onValueChange={v => setSort(v as SortOptionValue)}
                >
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
              )}

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
                      filterData={auctions}
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
        {isDesktop && (
          <div className="bg-background sticky top-20 self-start rounded-lg border shadow-sm">
            <FiltersPanel
              filterData={auctions}
              filterDataType="auction"
              onFilteredDataChange={handleFilteredDataChange}
              filterByAuctionStatus={filterByAuctionStatus}
              filterByPrice={true}
            />
          </div>
        )}

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
                    <AuctionDetails auction={inlineAuction} role={userRole} />
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
                      <TableHead>{t('product.merchandise_label')}</TableHead>
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
              {paginated.length === 0 && <EmptyState className="col-span-3" />}
            </>
          )}

          {/* Map */}
          {viewMode === 'map' && (
            <AuctionMap auctions={sorted} onSelect={a => setDialogAuction(a)} />
          )}
        </div>
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
