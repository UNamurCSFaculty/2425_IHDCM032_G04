import EmptyState from '../EmptyState'
import FiltersPanel from '../FiltersPanel'
import ProductCard from './ProductCard'
import type { ProductDto, QualityDto } from '@/api/generated'
import PaginationControls from '@/components/PaginationControls'
import { Button } from '@/components/ui/button'
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
  SlidersHorizontal,
} from 'lucide-react'
import React, { useEffect, useMemo, useState } from 'react'

export type ViewMode = 'cards' | 'table' | 'map'

export const sortOptions = [
  { value: 'deliveryDate-asc', label: 'date ⬆' },
  { value: 'deliveryDate-desc', label: 'date ⬇' },
  { value: 'weight-asc', label: 'poids ⬆' },
  { value: 'weight-desc', label: 'poids ⬇' },
] as const
export type SortOptionValue = (typeof sortOptions)[number]['value']

export const perPage = 12

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
  const [sort, setSort] = useState<SortOptionValue>('deliveryDate-desc')

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

  const filtered = useMemo(
    () =>
      products.filter(p => {
        if (
          filters.search &&
          !`${p.type} ${p.store.name} ${p.id}`
            .toLowerCase()
            .includes(filters.search.toLowerCase())
        )
          return false

        if (
          p.weightKg < filters.priceRange[0] ||
          p.weightKg > filters.priceRange[1]
        )
          return false

        if (
          filters.qualityId &&
          p.qualityControl?.quality.id !== filters.qualityId
        )
          return false

        if (
          filters.productTypeId &&
          p.type !== productTypes[filters.productTypeId - 1]
        )
          return false

        if (filters.regionId && p.store.address.regionId !== filters.regionId)
          return false

        if (filters.cityId && p.store.address.cityId !== filters.cityId)
          return false

        return true
      }),
    [
      products,
      filters.search,
      filters.priceRange,
      filters.qualityId,
      filters.productTypeId,
      filters.regionId,
      filters.cityId,
    ]
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
                      onFiltersChange={setFilters}
                      qualities={qualities}
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
            <FiltersPanel onFiltersChange={setFilters} qualities={qualities} />
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
                <EmptyState className="col-span-full" />
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
              {paginated.length === 0 && <EmptyState className="col-span-3" />}
            </>
          )}

          {/* Map */}
        </div>
      </div>
    </>
  )
}

export default ProductList
