import AuctionCard from './AuctionCard'
import AuctionMap from './AuctionMap'
import EmptyState from './EmptyState'
import type { AuctionDto, QualityDto } from '@/api/generated'
import PaginationControls from '@/components/PaginationControls'
import VirtualizedSelect from '@/components/VirtualizedSelect'
import AuctionDetails from '@/components/auctions/AuctionMarket/AuctionDetails'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogTitle,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet'
import { Slider } from '@/components/ui/slider'
import {
  Table,
  TableBody,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { ToggleGroup, ToggleGroupItem } from '@/components/ui/toggle-group'
import cities from '@/data/cities.json'
import regions from '@/data/regions.json'
import { useMediaQuery } from '@/hooks/use-mobile'
import { ProductType, formatDate } from '@/lib/utils'
import dayjs from '@/utils/dayjs-config'
import { formatPrice } from '@/utils/formatter'
import {
  ArrowLeft,
  ChevronDown,
  LayoutGrid,
  List as ListIcon,
  Map as MapIcon,
  Plus,
  Search,
  SlidersHorizontal,
  X,
} from 'lucide-react'
import React, { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'

export type ViewMode = 'cards' | 'table' | 'map'
export type UserRole = 'buyer' | 'seller'

export const AUCTION_STATUS_OPEN_LABEL = 'Ouvert'

export const productTypeOptions = [
  'All',
  ProductType.HARVEST,
  ProductType.TRANSFORMED,
] as const
export type ProductTypeOption = (typeof productTypeOptions)[number]

export const sortOptions = [
  { value: 'endDate-asc', label: 'Expiration ⬆' },
  { value: 'endDate-desc', label: 'Expiration ⬇' },
  { value: 'price-asc', label: 'Prix ⬆' },
  { value: 'price-desc', label: 'Prix ⬇' },
] as const
export type SortOptionValue = (typeof sortOptions)[number]['value']

export const perPage = 12

const cityOptions = cities.map((n, i) => ({
  id: i + 1,
  label: n,
}))

const regionOptions = regions.map((n, i) => ({
  id: i + 1,
  label: n,
}))

// ------------------------------------- Filters Panel -------------------------------------
interface FiltersPanelProps {
  search: string
  onSearch: (value: string) => void
  priceRange: [number, number]
  onPriceChange: (range: [number, number]) => void
  selectedDate: Date | null
  onDateSelect: (date: Date | null) => void
  qualities: QualityDto[]
  qualityId: number | null
  onQualityChange: (q: number | null) => void
  productType: ProductTypeOption
  onTypeChange: (t: ProductTypeOption) => void
  regionId: number | null
  onRegionChange: (id: number | null) => void
  cityId: number | null
  onCityChange: (id: number | null) => void
  resetFilters: () => void
}

const FiltersPanel: React.FC<FiltersPanelProps> = ({
  search,
  onSearch,
  priceRange,
  onPriceChange,
  selectedDate,
  onDateSelect,
  qualities,
  qualityId,
  onQualityChange,
  productType,
  onTypeChange,
  regionId,
  onRegionChange,
  cityId,
  onCityChange,
  resetFilters,
}) => {
  const { t } = useTranslation()

  const qualityOptions = qualities.map(q => ({
    id: q.id,
    label: q.name,
  }))

  return (
    <div>
      <div className="flex justify-between items-center p-4 border-b">
        <h3 className="font-semibold text-lg">Filtres</h3>
        <Button variant="ghost" size="sm" onClick={resetFilters}>
          Reset
        </Button>
      </div>
      <div className="p-4">
        <div className="space-y-6 p-4 lg:p-0">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher…"
              className="pl-10"
              value={search}
              onChange={e => onSearch(e.target.value)}
            />
            {search && (
              <Button
                variant="ghost"
                size="icon"
                className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7"
                onClick={() => onSearch('')}
              >
                <X className="size-4" />
              </Button>
            )}
          </div>

          {/* Price */}
          <div className="space-y-2">
            <div className="flex justify-between text-sm font-medium">
              <span>Prix</span>
              <span>
                {formatPrice.format(priceRange[0])} –{' '}
                {formatPrice.format(priceRange[1])}
              </span>
            </div>
            <Slider
              value={priceRange}
              onValueChange={v => onPriceChange(v as [number, number])}
              min={0}
              max={5_000_000}
              step={500}
            />
          </div>

          {/* Quality / Type */}
          <div>
            <label
              htmlFor="product-type-select"
              className="text-sm font-medium"
            >
              Marchandise
            </label>
            <Select
              value={productType}
              onValueChange={v => onTypeChange(v as ProductTypeOption)}
            >
              <SelectTrigger id="product-type-select">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {productTypeOptions.map(type => (
                  <SelectItem key={type} value={type}>
                    {type === 'All' ? 'Tous' : t('database.' + type)}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <VirtualizedSelect
            id="quality-select"
            label="Qualité"
            placeholder="Toutes les qualités"
            options={qualityOptions}
            value={qualityId}
            onChange={onQualityChange}
          />

          {/* Region / City */}
          <VirtualizedSelect
            id="region-select"
            label="Région"
            placeholder="Toutes les régions"
            options={regionOptions}
            value={regionId}
            onChange={onRegionChange}
          />
          <VirtualizedSelect
            id="city-select"
            label="Ville"
            placeholder="Toutes les villes"
            options={cityOptions}
            value={cityId}
            onChange={onCityChange}
          />

          {/* Date picker */}
          <div className="space-y-2">
            <label
              htmlFor="expiration-date-picker"
              className="text-sm font-medium"
            >
              Expire avant
            </label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  id="expiration-date-picker"
                  variant="outline"
                  className="w-full justify-between"
                >
                  {selectedDate
                    ? formatDate(selectedDate.toISOString())
                    : 'Choisir…'}
                  <ChevronDown className="size-4 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-auto p-0" align="start">
                <Calendar
                  mode="single"
                  selected={selectedDate ?? undefined}
                  onSelect={d => onDateSelect(d ?? null)}
                  disabled={d => d < dayjs().startOf('day').toDate()}
                />
              </PopoverContent>
            </Popover>
          </div>
        </div>
      </div>
    </div>
  )
}

// ------------------------------------- Main Component -------------------------------------
interface MarketplaceProps {
  auctions: AuctionDto[]
  qualities: QualityDto[]
  userRole: UserRole
  onMakeBid?: (id: number) => void
  onBuyNow?: (id: number) => void
  onCreateAuction?: () => void
  onBidAction?: (
    auctionId: number,
    bidId: number,
    action: 'accept' | 'reject'
  ) => void
}

const AuctionMarketplace: React.FC<MarketplaceProps> = ({
  auctions,
  qualities,
  userRole,
  onMakeBid,
  onBuyNow,
  onCreateAuction,
  onBidAction,
}) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')

  // UI state
  const [viewMode, setViewMode] = useState<ViewMode>('cards')
  const [sheetOpen, setSheetOpen] = useState(false)
  const [dialogAuction, setDialogAuction] = useState<AuctionDto | null>(null)
  const [inlineAuction, setInlineAuction] = useState<AuctionDto | null>(null)

  // Filters state
  const [search, setSearch] = useState('')
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 5_000_000])
  const [selectedDate, setSelectedDate] = useState<Date | null>(null)
  const [qualityId, setQualityId] = useState<number | null>(null)
  const [productType, setProductType] = useState<ProductTypeOption>('All')
  const [regionId, setRegionId] = useState<number | null>(null)
  const [cityId, setCityId] = useState<number | null>(null)
  const [sort, setSort] = useState<SortOptionValue>('endDate-asc')

  useEffect(() => setCityId(null), [regionId])

  // Filtering & Sorting
  const filtered = useMemo(
    () =>
      auctions.filter(a => {
        if (
          search &&
          !`${a.product.type} ${a.product.store.name} ${a.id}`
            .toLowerCase()
            .includes(search.toLowerCase())
        )
          return false
        if (a.price < priceRange[0] || a.price > priceRange[1]) return false
        if (
          selectedDate &&
          dayjs(a.expirationDate).isAfter(dayjs(selectedDate).endOf('day'))
        )
          return false
        if (qualityId && a.product.qualityControl?.quality.id !== qualityId)
          return false
        if (productType !== 'All' && a.product.type !== productType)
          return false
        if (regionId && a.product.store.address.regionId !== regionId)
          return false
        if (cityId && a.product.store.address.cityId !== cityId) return false
        return a.status.name === AUCTION_STATUS_OPEN_LABEL
      }),
    [
      auctions,
      search,
      priceRange,
      selectedDate,
      qualityId,
      productType,
      regionId,
      cityId,
    ]
  )

  const sorted = useMemo(() => {
    const list = [...filtered]
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
  }, [filtered, sort])

  // Pagination
  const [currentPage, setCurrentPage] = useState(1)
  const totalPages = Math.max(1, Math.ceil(sorted.length / perPage))
  const paginated = useMemo(
    () => sorted.slice((currentPage - 1) * perPage, currentPage * perPage),
    [sorted, currentPage]
  )

  useEffect(
    () => setCurrentPage(1),
    [search, priceRange, selectedDate, qualityId, productType, regionId, cityId]
  )

  const resetFilters = () => {
    setSearch('')
    setPriceRange([0, 5_000_000])
    setSelectedDate(null)
    setQualityId(null)
    setProductType('All')
    setRegionId(null)
    setCityId(null)
  }

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
            Résultat(s): {filtered.length} enchère{filtered.length !== 1 && 's'}
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
                  aria-label="Cartes"
                  className="flex items-center justify-center py-2 hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground"
                >
                  <LayoutGrid className="size-4 mr-1" />
                  Cartes
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
                      search={search}
                      onSearch={setSearch}
                      priceRange={priceRange}
                      onPriceChange={setPriceRange}
                      selectedDate={selectedDate}
                      onDateSelect={setSelectedDate}
                      qualities={qualities}
                      qualityId={qualityId}
                      onQualityChange={setQualityId}
                      productType={productType}
                      onTypeChange={setProductType}
                      regionId={regionId}
                      onRegionChange={setRegionId}
                      cityId={cityId}
                      onCityChange={setCityId}
                      resetFilters={resetFilters}
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
              search={search}
              onSearch={setSearch}
              priceRange={priceRange}
              onPriceChange={setPriceRange}
              selectedDate={selectedDate}
              onDateSelect={setSelectedDate}
              qualities={qualities}
              qualityId={qualityId}
              onQualityChange={setQualityId}
              productType={productType}
              onTypeChange={setProductType}
              regionId={regionId}
              onRegionChange={setRegionId}
              cityId={cityId}
              onCityChange={setCityId}
              resetFilters={resetFilters}
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
                    onMakeBid={onMakeBid}
                    onBuyNow={onBuyNow}
                  />
                  <div className="col-span-full lg:col-span-2">
                    <AuctionDetails
                      auction={inlineAuction}
                      role={userRole}
                      onBidAction={onBidAction}
                      onMakeBid={onMakeBid}
                      onBuyNow={onBuyNow}
                    />
                  </div>
                </>
              ) : filtered.length === 0 ? (
                <EmptyState onReset={resetFilters} className="col-span-full" />
              ) : (
                <>
                  {paginated.map(a => (
                    <AuctionCard
                      key={a.id}
                      auction={a}
                      layout="grid"
                      role={userRole}
                      onDetails={() => setInlineAuction(a)}
                      onMakeBid={onMakeBid}
                      onBuyNow={onBuyNow}
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
                      <TableHead>Type</TableHead>
                      <TableHead>Expire le</TableHead>
                      <TableHead>Région</TableHead>
                      <TableHead>Ville</TableHead>
                      <TableHead>Quantité</TableHead>
                      <TableHead>Qualité</TableHead>
                      <TableHead className="text-right">Prix</TableHead>
                      <TableHead className="text-right">Offre max</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
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
                        onMakeBid={onMakeBid}
                        onBuyNow={onBuyNow}
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
                <EmptyState onReset={resetFilters} className="col-span-3" />
              )}
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
              onBidAction={onBidAction}
              onMakeBid={onMakeBid}
              onBuyNow={onBuyNow}
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
