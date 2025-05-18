import AuctionCard from './AuctionCard'
import AuctionMap from './AuctionMap'
import EmptyState from './EmptyState'
import type { AuctionDto } from '@/api/generated'
import PaginationControls from '@/components/PaginationControls'
import VirtualizedSelect from '@/components/VirtualizedSelect'
import AuctionDetails from '@/components/auctions/AuctionMarket/AuctionDetails'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
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
import { useMediaQuery } from '@/hooks/use-mobile'
import { formatDate, getCities, getRegions } from '@/lib/utils'
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
import { useEffect, useMemo, useState } from 'react'

export type ViewMode = 'cards' | 'table' | 'map'
export type UserRole = 'buyer' | 'seller'

interface MarketplaceProps {
  auctions: AuctionDto[]
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

export const AUCTION_STATUS_OPEN_LABEL = 'Ouvert'

export const qualityOptions = [
  'All',
  'Premium',
  'Industrial',
  'Standard',
] as const
export type QualityOption = (typeof qualityOptions)[number]

export const productTypeOptions = ['All', 'harvest', 'transformed'] as const
export type ProductTypeOption = (typeof productTypeOptions)[number]

export const sortOptions = [
  { value: 'endDate-asc', label: 'Expiration ⬆' },
  { value: 'endDate-desc', label: 'Expiration ⬇' },
  { value: 'price-asc', label: 'Prix ⬆' },
  { value: 'price-desc', label: 'Prix ⬇' },
] as const
export type SortOptionValue = (typeof sortOptions)[number]['value']

export const perPage = 12

const AuctionMarketplace: React.FC<MarketplaceProps> = ({
  auctions,
  userRole,
  onMakeBid,
  onBuyNow,
  onCreateAuction,
  onBidAction,
}) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')

  // ------------------------------------- UI state
  const [viewMode, setViewMode] = useState<ViewMode>('cards')
  const [sheetOpen, setSheetOpen] = useState(false)
  const [dialogAuction, setDialogAuction] = useState<AuctionDto | null>(null)
  const [inlineAuction, setInlineAuction] = useState<AuctionDto | null>(null)

  // ------------------------------------- filters
  const [search, setSearch] = useState('')
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 5_000_000])
  const [selectedDate, setSelectedDate] = useState<Date | null>(null)
  const [quality, setQuality] = useState<QualityOption>('All')
  const [productType, setProductType] = useState<ProductTypeOption>('All')
  const [regionId, setRegionId] = useState<number | null>(null)
  const [cityId, setCityId] = useState<number | null>(null)
  const [sort, setSort] = useState<SortOptionValue>('endDate-asc')

  const [cities, setCities] = useState<string[]>([])
  const [regions, setRegions] = useState<string[]>([])
  useEffect(() => {
    // charge une seule fois
    void getCities().then(setCities)
    void getRegions().then(setRegions)
  }, [])
  useEffect(() => setCityId(null), [regionId])

  // ------------------------------------- filtering & sorting
  const filtered = useMemo(() => {
    return auctions.filter(a => {
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
      if (
        quality !== 'All' &&
        a.product.qualityControl?.quality.name !== quality
      )
        return false
      if (productType !== 'All' && a.product.type !== productType) return false
      const adr = a.product.store.address
      if (regionId && adr.regionId !== regionId) return false
      if (cityId && adr.cityId !== cityId) return false
      if (a.status.name !== AUCTION_STATUS_OPEN_LABEL) return false
      return true
    })
  }, [
    auctions,
    search,
    priceRange,
    selectedDate,
    quality,
    productType,
    regionId,
    cityId,
  ])

  const sorted = useMemo(() => {
    const list = [...filtered]
    switch (sort) {
      case 'endDate-desc':
        list.sort(
          (a, b) =>
            dayjs(b.expirationDate).valueOf() -
            dayjs(a.expirationDate).valueOf()
        )
        break
      case 'price-asc':
        list.sort((a, b) => a.price - b.price)
        break
      case 'price-desc':
        list.sort((a, b) => b.price - a.price)
        break
      default:
        list.sort(
          (a, b) =>
            dayjs(a.expirationDate).valueOf() -
            dayjs(b.expirationDate).valueOf()
        )
    }
    return list
  }, [filtered, sort])

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
    window.scrollTo({ top: 200, behavior: 'smooth' })
  }

  // ------------------------------------- pagination
  const [currentPage, setCurrentPage] = useState(1)
  const totalPages = Math.max(1, Math.ceil(sorted.length / perPage))
  const paginated = useMemo(
    () => sorted.slice((currentPage - 1) * perPage, currentPage * perPage),
    [sorted, currentPage]
  )
  useEffect(
    () => setCurrentPage(1),
    [search, priceRange, selectedDate, quality, productType, regionId, cityId]
  )

  const resetFilters = () => {
    setSearch('')
    setPriceRange([0, 5_000_000])
    setSelectedDate(null)
    setQuality('All')
    setProductType('All')
    setRegionId(null)
    setCityId(null)
  }

  // -------------------------------------------------------------------------
  const isInCardDetail = viewMode === 'cards' && inlineAuction
  const cssCard = isInCardDetail ? 'lg:justify-start' : 'lg:justify-end'
  return (
    <>
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6 w-full">
        <div className="text-sm text-muted-foreground lg:w-[260px]">
          {filtered.length} enchère{filtered.length !== 1 && 's'}
        </div>
        <div className={`flex items-center ${cssCard} w-full lg:pl-11`}>
          {isInCardDetail && (
            <div className="space-y-4">
              <Button
                variant="outline"
                className="flex items-center gap-1"
                onClick={() => setInlineAuction(null)}
              >
                <ArrowLeft className="size-4" /> Retour
              </Button>
            </div>
          )}
          {!isInCardDetail && (
            <div>
              <div className="flex flex-wrap gap-2 items-center">
                {/* sorting */}
                <Select
                  value={sort}
                  onValueChange={v => setSort(v as SortOptionValue)}
                >
                  <SelectTrigger className="w-40">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {sortOptions.map(o => (
                      <SelectItem key={o.value} value={o.value}>
                        {o.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>

                {/* view */}
                <Select
                  value={viewMode}
                  onValueChange={v => {
                    setViewMode(v as ViewMode)
                    setInlineAuction(null)
                    setDialogAuction(null)
                  }}
                >
                  <SelectTrigger className="w-32">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="cards">
                      <LayoutGrid className="size-4 inline mr-2" /> Cartes
                    </SelectItem>
                    <SelectItem value="table">
                      <ListIcon className="size-4 inline mr-2" /> Liste
                    </SelectItem>
                    <SelectItem value="map">
                      <MapIcon className="size-4 inline mr-2" /> Carte
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>
              {/* create auction */}
              {userRole === 'seller' && onCreateAuction && (
                <Button
                  className="bg-emerald-600 text-white"
                  onClick={onCreateAuction}
                >
                  <Plus className="size-4 mr-2" /> Nouvelle enchère
                </Button>
              )}
            </div>
          )}
          {/* mobile filters */}
          {!isInCardDetail && !isDesktop && (
            <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
              <SheetTrigger asChild>
                <Button variant="outline">
                  <SlidersHorizontal className="size-4 mr-2" /> Filtres
                </Button>
              </SheetTrigger>
              <SheetContent
                side="left"
                className="w-[300px] sm:w-[380px] p-0 overflow-y-auto"
              >
                <div className="flex justify-between items-center p-4 border-b">
                  <h3 className="font-semibold text-lg">Filtres</h3>
                  <Button variant="ghost" size="sm" onClick={resetFilters}>
                    Reset
                  </Button>
                </div>
                {renderFilters()}
              </SheetContent>
            </Sheet>
          )}
        </div>
      </div>

      {/* Grid layout */}
      <div className="grid lg:grid-cols-[260px_1fr] gap-6 items-start">
        {isDesktop && (
          <div className="sticky top-6 border rounded-lg shadow-sm bg-background self-start">
            <div className="flex justify-between items-center p-4 border-b">
              <h3 className="font-semibold text-lg">Filtres</h3>
              <Button variant="ghost" size="sm" onClick={resetFilters}>
                Reset
              </Button>
            </div>
            <div className="p-4">{renderFilters()}</div>
          </div>
        )}

        <div className="space-y-6 relative">
          {/* Cards mode */}
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
            {viewMode === 'cards' &&
              (inlineAuction ? (
                <>
                  <div className="col-span-3 lg:col-span-1">
                    <AuctionCard
                      key={inlineAuction.id}
                      auction={inlineAuction}
                      layout="grid"
                      isDetail={true}
                      role={userRole}
                      onDetails={() => {}}
                      onMakeBid={onMakeBid}
                      onBuyNow={onBuyNow}
                    />
                  </div>
                  <div className="col-span-3 lg:col-span-2">
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
                <EmptyState onReset={resetFilters} />
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
                    <PaginationControls
                      current={currentPage}
                      total={totalPages}
                      onChange={handlePageChange}
                    />
                  )}
                </>
              ))}
          </div>
          {/* Table mode */}
          {viewMode === 'table' && (
            <>
              <div className="border rounded-lg overflow-hidden bg-background">
                <Table className="text-sm">
                  <TableHeader className="sticky top-0 backdrop-blur supports-[backdrop-filter]:bg-muted/60 z-10">
                    <TableRow className="h-9  bg-neutral-100 text-white[& > *] ">
                      <TableHead className="py-1 px-2 w-[120px]">
                        Type
                      </TableHead>
                      <TableHead className="py-1 px-2 w-[120px]">
                        Expire le
                      </TableHead>
                      <TableHead className="py-1 px-2">Région</TableHead>
                      <TableHead className="py-1 px-2">Ville</TableHead>
                      <TableHead className="py-1 px-2">Quantité</TableHead>
                      <TableHead className="py-1 px-2">Qualité</TableHead>
                      <TableHead className="py-1 px-2 text-right">
                        Prix
                      </TableHead>
                      <TableHead className="py-1 px-2 text-right">
                        Offre max
                      </TableHead>
                      <TableHead className="py-1 px-2 text-right">
                        Actions
                      </TableHead>
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
            </>
          )}

          {/* Map mode */}
          {viewMode === 'map' && (
            <AuctionMap auctions={sorted} onSelect={a => setDialogAuction(a)} />
          )}
        </div>
      </div>

      {/* Dialog */}
      {dialogAuction && viewMode !== 'cards' && (
        <Dialog open onOpenChange={o => !o && setDialogAuction(null)}>
          <DialogContent className="w-full max-w-[80vw]! max-h-[90vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>Enchère #{dialogAuction.id}</DialogTitle>
            </DialogHeader>
            <AuctionDetails
              auction={dialogAuction}
              role={userRole}
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

  // ---------------------- local helper ----------------------
  function renderFilters() {
    return (
      <div className="space-y-6 p-4 lg:p-0">
        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
          <Input
            placeholder="Rechercher…"
            className="pl-10"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          {search && (
            <Button
              variant="ghost"
              size="icon"
              className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7"
              onClick={() => setSearch('')}
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
            onValueChange={v => setPriceRange(v as [number, number])}
            min={0}
            max={5_000_000}
            step={500}
          />
        </div>

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
                onSelect={d => setSelectedDate(d ?? null)}
                disabled={d => d < dayjs().startOf('day').toDate()}
              />
            </PopoverContent>
          </Popover>
        </div>

        {/* Quality / Type */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label htmlFor="quality-select" className="text-sm font-medium">
              Qualité
            </label>
            <Select
              value={quality}
              onValueChange={v => setQuality(v as QualityOption)}
            >
              <SelectTrigger id="quality-select">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {qualityOptions.map(q => (
                  <SelectItem key={q} value={q}>
                    {q === 'All' ? 'Toutes' : q}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div>
            <label
              htmlFor="product-type-select"
              className="text-sm font-medium"
            >
              Type
            </label>
            <Select
              value={productType}
              onValueChange={v => setProductType(v as ProductTypeOption)}
            >
              <SelectTrigger id="product-type-select">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {productTypeOptions.map(t => (
                  <SelectItem key={t} value={t}>
                    {t === 'All' ? 'Tous' : t}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* Region / City */}
        <VirtualizedSelect
          id="region-select"
          label="Région"
          placeholder="Toutes les régions"
          options={regions}
          value={regionId}
          onChange={setRegionId}
        />
        <VirtualizedSelect
          id="city-select"
          label="Ville"
          placeholder={regionId ? 'Toutes les villes' : 'Choisir région…'}
          options={cities}
          value={cityId}
          onChange={setCityId}
        />
      </div>
    )
  }
}

export default AuctionMarketplace
