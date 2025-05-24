import EmptyState from '../EmptyState'
import ProductCard from './ProductCard'
import type { ProductDto, QualityDto } from '@/api/generated'
import PaginationControls from '@/components/PaginationControls'
import VirtualizedSelect from '@/components/VirtualizedSelect'
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
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
import { TradeStatus, productTypes } from '@/lib/utils'
import dayjs from '@/utils/dayjs-config'
import { formatDate, formatPrice } from '@/utils/formatter'
import {
  ArrowLeft,
  ChevronDown,
  LayoutGrid,
  List as ListIcon,
  Search,
  SlidersHorizontal,
  X,
} from 'lucide-react'
import React, { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'

export type ViewMode = 'cards' | 'table' | 'map'

export const sortOptions = [
  { value: 'deliveryDate-asc', label: 'date ⬆' },
  { value: 'deliveryDate-desc', label: 'date ⬇' },
  { value: 'weight-asc', label: 'poids ⬆' },
  { value: 'weight-desc', label: 'poids ⬇' },
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
  auctionStatus: TradeStatus
  onAuctionStatusChange: (status: TradeStatus) => void
  showAuctionStatusFilter?: boolean
  priceRange: [number, number]
  onPriceChange: (range: [number, number]) => void
  selectedDate: Date | null
  onDateSelect: (date: Date | null) => void
  qualities: QualityDto[]
  qualityId: number | null
  onQualityChange: (q: number | null) => void
  productTypeId: number | null
  onTypeChange: (t: number | null) => void
  regionId: number | null
  onRegionChange: (id: number | null) => void
  cityId: number | null
  onCityChange: (id: number | null) => void
  resetFilters: () => void
}

const FiltersPanel: React.FC<FiltersPanelProps> = ({
  search,
  onSearch,
  auctionStatus,
  onAuctionStatusChange,
  showAuctionStatusFilter,
  priceRange,
  onPriceChange,
  selectedDate,
  onDateSelect,
  qualities,
  qualityId,
  onQualityChange,
  productTypeId,
  onTypeChange,
  regionId,
  onRegionChange,
  cityId,
  onCityChange,
  resetFilters,
}) => {
  const { t } = useTranslation()

  const productTypeOptions = productTypes.map((n, i) => ({
    id: i + 1,
    label: t('database.' + n),
  }))

  const qualityOptions = qualities
    .filter(quality => {
      return (
        !productTypeId ||
        quality.qualityType.name.toLowerCase() ==
          productTypes[productTypeId - 1].toLowerCase()
      )
    })
    .map(q => ({
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

          {/* Status */}
          {showAuctionStatusFilter && (
            <RadioGroup
              value={auctionStatus}
              defaultValue={TradeStatus.OPEN}
              onValueChange={onAuctionStatusChange}
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value={TradeStatus.OPEN} id="r1" />
                <Label htmlFor="r1">Enchères en cours</Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value={TradeStatus.EXPIRED} id="r2" />
                <Label htmlFor="r2">Enchères terminées</Label>
              </div>
            </RadioGroup>
          )}

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
          {/* Product Type */}
          <VirtualizedSelect
            id="product-type-select"
            label="Marchandise"
            placeholder="Tous les types"
            options={productTypeOptions}
            value={productTypeId}
            onChange={onTypeChange}
          />
          {/* Quality */}
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
interface ProductListProps {
  products: ProductDto[]
  qualities: QualityDto[]
}

const ProductList: React.FC<ProductListProps> = ({ products, qualities }) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')

  // UI state
  const [viewMode, setViewMode] = useState<ViewMode>('cards')
  const [sheetOpen, setSheetOpen] = useState(false)
  const [inlineProduct, setInlineProduct] = useState<ProductDto | null>(null)

  // Filters state
  const [search, setSearch] = useState('')
  const [auctionStatus, setAuctionStatus] = useState<TradeStatus>(
    TradeStatus.OPEN
  )
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 5_000_000])
  const [weightKg] = useState<[number, number]>([0, 5_000_000])
  const [selectedDate, setSelectedDate] = useState<Date | null>(null)
  const [qualityId, setQualityId] = useState<number | null>(null)
  const [productTypeId, setProductTypeId] = useState<number | null>(null)
  const [regionId, setRegionId] = useState<number | null>(null)
  const [cityId, setCityId] = useState<number | null>(null)
  const [sort, setSort] = useState<SortOptionValue>('deliveryDate-desc')

  useEffect(() => setCityId(null), [regionId])

  // Filtering & Sorting
  const filtered = useMemo(
    () =>
      products.filter(p => {
        if (
          search &&
          !`${p.type} ${p.store.name} ${p.id}`
            .toLowerCase()
            .includes(search.toLowerCase())
        )
          return false

        if (p.weightKg < priceRange[0] || p.weightKg > priceRange[1])
          return false

        if (qualityId && p.qualityControl?.quality.id !== qualityId)
          return false

        if (productTypeId && p.type !== productTypes[productTypeId - 1])
          return false

        if (regionId && p.store.address.regionId !== regionId) return false

        if (cityId && p.store.address.cityId !== cityId) return false

        return true
      }),
    [products, search, priceRange, qualityId, productTypeId, regionId, cityId]
  )

  const sorted = useMemo(() => {
    const list = [...filtered]
    switch (sort) {
      case 'deliveryDate-desc':
        return list.sort(
          (a, b) =>
            dayjs(b.deliveryDate).valueOf() - dayjs(a.deliveryDate).valueOf()
        )
      case 'weight-asc':
        return list.sort((a, b) => a.weightKg - b.weightKg)
      case 'weight-desc':
        return list.sort((a, b) => b.weightKg - a.weightKg)
      default:
        return list.sort(
          (a, b) =>
            dayjs(a.deliveryDate).valueOf() - dayjs(b.deliveryDate).valueOf()
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
    [search, weightKg, selectedDate, qualityId, productTypeId, regionId, cityId]
  )

  const resetFilters = () => {
    setSearch('')
    setAuctionStatus(TradeStatus.OPEN)
    setPriceRange([0, 5_000_000])
    setSelectedDate(null)
    setQualityId(null)
    setProductTypeId(null)
    setRegionId(null)
    setCityId(null)
  }

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
    window.scrollTo({ top: 200, behavior: 'smooth' })
  }

  // Render
  const isInCardDetail = viewMode === 'cards' && inlineProduct
  const cssCard = isInCardDetail ? 'lg:justify-start' : 'lg:justify-end'

  return (
    <>
      {/* Header */}
      <div className="flex flex-wrap flex-col sm:flex-row items-center justify-center lg:justify-between gap-4 mb-6 w-full">
        <div className="text-md  text-muted-foreground w-full lg:w-[260px] ">
          <div className="text-center lg:text-left lg:pl-4">
            Résultat(s) : {filtered.length} produit
            {filtered.length !== 1 && 's'}
          </div>
        </div>
        <div className={`flex items-center ${cssCard} lg:pl-11`}>
          {isInCardDetail ? (
            <div className="pl-4">
              <Button
                variant="outline"
                className="flex items-center gap-1 w-40"
                onClick={() => setInlineProduct(null)}
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
                  setInlineProduct(null)
                }}
                className="grid grid-cols-2 rounded-lg border bg-background overflow-hidden"
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
              </ToggleGroup>
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
                      auctionStatus={auctionStatus}
                      onAuctionStatusChange={setAuctionStatus}
                      showAuctionStatusFilter={false}
                      priceRange={priceRange}
                      onPriceChange={setPriceRange}
                      selectedDate={selectedDate}
                      onDateSelect={setSelectedDate}
                      qualities={qualities}
                      qualityId={qualityId}
                      onQualityChange={setQualityId}
                      productTypeId={productTypeId}
                      onTypeChange={setProductTypeId}
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
              auctionStatus={auctionStatus}
              onAuctionStatusChange={setAuctionStatus}
              showAuctionStatusFilter={false}
              priceRange={priceRange}
              onPriceChange={setPriceRange}
              selectedDate={selectedDate}
              onDateSelect={setSelectedDate}
              qualities={qualities}
              qualityId={qualityId}
              onQualityChange={setQualityId}
              productTypeId={productTypeId}
              onTypeChange={setProductTypeId}
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
              {inlineProduct ? (
                <>
                  <ProductCard
                    product={inlineProduct}
                    layout="grid"
                    isDetail
                    onDetails={() => {}}
                  />
                  <div className="col-span-full lg:col-span-2">
                    {/* <AuctionDetails auction={inlineProduct} /> */}
                  </div>
                </>
              ) : filtered.length === 0 ? (
                <EmptyState onReset={resetFilters} className="col-span-full" />
              ) : (
                <>
                  {paginated.map(p => (
                    <ProductCard
                      key={p.id}
                      product={p}
                      layout="grid"
                      onDetails={
                        () => console.log('TODO') /*setInlineProduct(a)*/
                      }
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
                      <TableHead>Propriétaire</TableHead>
                      <TableHead>Quantité</TableHead>
                      <TableHead>Qualité</TableHead>
                      <TableHead>Origine</TableHead>
                      <TableHead>Date de dépôt</TableHead>
                      <TableHead>Ville</TableHead>
                      <TableHead>Région</TableHead>
                      <TableHead>Entrepôt</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {paginated.map(p => (
                      <ProductCard
                        key={p.id}
                        product={p}
                        layout="row"
                        onDetails={() => console.log('DETAILS')}
                        // onDetails={() => setDialogAuction(p)}
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
        </div>
      </div>
    </>
  )
}

export default ProductList
