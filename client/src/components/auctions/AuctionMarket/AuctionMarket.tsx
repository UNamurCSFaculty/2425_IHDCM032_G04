import EmptyState from '../../EmptyState'
import AuctionCard from './AuctionCard'
import AuctionMap from './AuctionMap'
import type { AuctionDto, QualityDto } from '@/api/generated'
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
import { TradeStatus, productTypes } from '@/lib/utils'
import dayjs from '@/utils/dayjs-config'
import {
  ArrowLeft,
  LayoutGrid,
  List as ListIcon,
  Map as MapIcon,
  Plus,
  SlidersHorizontal,
} from 'lucide-react'
import React, { useEffect, useMemo, useState } from 'react'

export type ViewMode = 'cards' | 'table' | 'map'
export type UserRole = 'buyer' | 'seller'

export const sortOptions = [
  { value: 'endDate-asc', label: 'Expiration ⬆' },
  { value: 'endDate-desc', label: 'Expiration ⬇' },
  { value: 'price-asc', label: 'Prix ⬆' },
  { value: 'price-desc', label: 'Prix ⬇' },
] as const
export type SortOptionValue = (typeof sortOptions)[number]['value']

export const perPage = 12

interface MarketplaceProps {
  auctions: AuctionDto[]
  qualities: QualityDto[]
  userRole: UserRole
  filterByAuctionStatus?: boolean
  onCreateAuction?: () => void
}

const AuctionMarketplace: React.FC<MarketplaceProps> = ({
  auctions,
  qualities,
  userRole,
  filterByAuctionStatus,
  onCreateAuction,
}) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')

  // UI state
  const [viewMode, setViewMode] = useState<ViewMode>('cards')
  const [sheetOpen, setSheetOpen] = useState(false)
  const [dialogAuction, setDialogAuction] = useState<AuctionDto | null>(null)
  const [inlineAuction, setInlineAuction] = useState<AuctionDto | null>(null)
  const [sort, setSort] = useState<SortOptionValue>('endDate-asc')

  // Filtering & Sorting
  const [filters, setFilters] = useState({
    search: '',
    auctionStatus: TradeStatus.OPEN,
    priceRange: [0, 5_000_000] as [number, number],
    selectedDate: null as Date | null,
    qualityId: null as number | null,
    productTypeId: null as number | null,
    regionId: null as number | null,
    cityId: null as number | null,
  })

  const filteredAuctions = useMemo(
    () =>
      auctions.filter(a => {
        if (
          filters.search &&
          !`${a.product.type} ${a.product.store.name} ${a.id} ${a.trader.firstName} ${a.trader.lastName}`
            .toLowerCase()
            .includes(filters.search.toLowerCase())
        )
          return false

        if (
          filters.auctionStatus === TradeStatus.OPEN &&
          a.status.name !== TradeStatus.OPEN
        )
          return false
        if (
          filters.auctionStatus !== TradeStatus.OPEN &&
          a.status.name === TradeStatus.OPEN
        )
          return false

        if (a.price < filters.priceRange[0] || a.price > filters.priceRange[1])
          return false

        if (
          filters.selectedDate &&
          dayjs(a.expirationDate).isAfter(
            dayjs(filters.selectedDate).endOf('day')
          )
        )
          return false

        if (
          filters.qualityId &&
          a.product.qualityControl?.quality.id !== filters.qualityId
        )
          return false

        if (
          filters.productTypeId &&
          a.product.type !== productTypes[filters.productTypeId - 1]
        )
          return false

        if (
          filters.regionId &&
          a.product.store.address.regionId !== filters.regionId
        )
          return false

        if (filters.cityId && a.product.store.address.cityId !== filters.cityId)
          return false

        return true
      }),
    [
      auctions,
      filters.search,
      filters.auctionStatus,
      filters.priceRange,
      filters.selectedDate,
      filters.qualityId,
      filters.productTypeId,
      filters.regionId,
      filters.cityId,
    ]
  )

  const sorted = useMemo(() => {
    const list = [...filteredAuctions]
    switch (sort) {
      case 'endDate-desc':
        return list.sort(
          (a, b) =>
            dayjs(b.expirationDate).valueOf() -
            dayjs(a.expirationDate).valueOf()
        )
      case 'price-asc':
        return list.sort((a, b) => a.price - b.price)
      case 'price-desc':
        return list.sort((a, b) => b.price - a.price)
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

  useEffect(
    () => setCurrentPage(1),
    [
      filters.search,
      filters.priceRange,
      filters.selectedDate,
      filters.qualityId,
      filters.productTypeId,
      filters.regionId,
      filters.cityId,
    ]
  )
  const handlePageChange = (page: number) => {
    setCurrentPage(page)
    window.scrollTo({ top: 200, behavior: 'smooth' })
  }

  // Render
  const isInCardDetail = viewMode === 'cards' && inlineAuction
  const cssCard = isInCardDetail ? 'lg:justify-start' : 'lg:justify-end'

  return (
    <>
      {/* Header */}
      <div className="flex flex-wrap flex-col sm:flex-row items-center justify-center lg:justify-between gap-4 mb-6 w-full">
        <div className="text-md  text-muted-foreground w-full lg:w-[260px] ">
          <div className="text-center lg:text-left lg:pl-4">
            Résultat(s) : {filteredAuctions.length} enchère
            {filteredAuctions.length !== 1 && 's'}
          </div>
        </div>
        <div className={`flex items-center ${cssCard} lg:pl-11`}>
          {isInCardDetail ? (
            <div className="pl-4">
              <Button
                variant="outline"
                className="flex items-center gap-1 w-40"
                onClick={() => setInlineAuction(null)}
              >
                <ArrowLeft className="size-4" /> Retour
              </Button>
            </div>
          ) : (
            <div className="flex flex-col lg:flex-row flex-wrap gap-2 items-center justify-center lg:justify-end w-full">
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
                      >{`Tri par ${o.label}`}</SelectItem>
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
                className="grid grid-cols-3 rounded-lg border bg-background overflow-hidden"
              >
                <ToggleGroupItem
                  value="cards"
                  aria-label="Grille"
                  className="flex items-center justify-center py-2 hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground"
                >
                  <LayoutGrid className="size-4 mr-1" />
                  Grille
                </ToggleGroupItem>
                <ToggleGroupItem
                  value="table"
                  aria-label="Liste"
                  className="flex items-center justify-center py-2 hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground"
                >
                  <ListIcon className="size-4 mr-1" />
                  Liste
                </ToggleGroupItem>
                <ToggleGroupItem
                  value="map"
                  aria-label="Carte"
                  className="flex items-center justify-center py-2 hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground"
                >
                  <MapIcon className="size-4 mr-1" />
                  Carte
                </ToggleGroupItem>
              </ToggleGroup>
              {/* Create Auction */}
              {userRole === 'seller' && onCreateAuction && (
                <Button
                  className="bg-emerald-600 text-white"
                  onClick={onCreateAuction}
                >
                  <Plus className="size-4 mr-2" />
                  Nouvelle enchère
                </Button>
              )}
              {/* Mobile Filters */}
              {!isDesktop && (
                <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
                  <SheetTrigger asChild>
                    <Button variant="outline">
                      <SlidersHorizontal className="size-4 mr-2" />
                      Filtres
                    </Button>
                  </SheetTrigger>
                  <SheetContent
                    side="left"
                    className="w-[300px] sm:w-[380px] p-0 overflow-y-auto"
                  >
                    <FiltersPanel
                      onFiltersChange={setFilters}
                      qualities={qualities}
                      filterByAuctionStatus={filterByAuctionStatus}
                    />
                  </SheetContent>
                </Sheet>
              )}
            </div>
          )}
        </div>
      </div>

      <div className="grid lg:grid-cols-[260px_1fr] gap-6 items-start">
        {isDesktop && (
          <div className="sticky top-20 border rounded-lg shadow-sm bg-background self-start">
            <FiltersPanel
              onFiltersChange={setFilters}
              qualities={qualities}
              filterByAuctionStatus={filterByAuctionStatus}
            />
          </div>
        )}

        <div className="relative w-full min-w-0">
          {/* Cards */}
          {viewMode === 'cards' && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-4">
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
              <div className="border rounded-lg bg-background overflow-x-auto">
                <Table className="text-sm table-auto">
                  <TableHeader className="sticky top-0 backdrop-blur supports-[backdrop-filter]:bg-muted/60 z-10">
                    <TableRow className="h-9 bg-neutral-100">
                      <TableHead>Marchandise</TableHead>
                      <TableHead>Expiration</TableHead>
                      <TableHead>Région</TableHead>
                      <TableHead>Ville</TableHead>
                      <TableHead>Quantité</TableHead>
                      <TableHead>Qualité</TableHead>
                      <TableHead className="text-right">Prix</TableHead>
                      <TableHead className="text-right">
                        Meilleure offre
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
          <DialogContent className="w-full max-w-[80vw]! max-h-[90vh] overflow-y-auto">
            <AuctionDetails
              auction={dialogAuction}
              role={userRole}
              showDetails={true}
            />
            <DialogFooter>
              <Button variant="outline" onClick={() => setDialogAuction(null)}>
                Fermer
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}
    </>
  )
}

export default AuctionMarketplace
