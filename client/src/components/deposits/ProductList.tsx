import EmptyState from '../EmptyState'
import FiltersPanel from '../FiltersPanel'
import ProductCard from './ProductCard'
import type { ProductDto } from '@/api/generated'
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
import dayjs from '@/utils/dayjs-config'
import {
  ArrowLeft,
  LayoutGrid,
  List as ListIcon,
  SlidersHorizontal,
} from 'lucide-react'
import React, { useCallback, useEffect, useMemo, useState } from 'react'

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
}

const ProductList: React.FC<ProductListProps> = ({ products }) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')

  // UI state
  const [viewMode, setViewMode] = useState<ViewMode>('cards')
  const [sheetOpen, setSheetOpen] = useState(false)
  const [inlineProduct, setInlineProduct] = useState<ProductDto | null>(null)
  const [sort, setSort] = useState<SortOptionValue>('deliveryDate-asc')

  // Filtering
  const [filteredProducts, setFilteredProducts] = useState<ProductDto[]>([])

  // Sorting
  const sorted = useMemo(() => {
    const list = [...filteredProducts]
    switch (sort) {
      case 'weight-asc':
        return list.sort((a, b) => a.weightKg - b.weightKg)
      case 'weight-desc':
        return list.sort((a, b) => b.weightKg - a.weightKg)
      case 'deliveryDate-desc':
        return list.sort(
          (a, b) =>
            dayjs(b.deliveryDate).valueOf() - dayjs(a.deliveryDate).valueOf()
        )
      default:
        return list.sort(
          (a, b) =>
            dayjs(a.deliveryDate).valueOf() - dayjs(b.deliveryDate).valueOf()
        )
    }
  }, [filteredProducts, sort])

  // Pagination
  const [currentPage, setCurrentPage] = useState(1)
  const totalPages = Math.max(1, Math.ceil(sorted.length / perPage))
  const paginated = useMemo(
    () => sorted.slice((currentPage - 1) * perPage, currentPage * perPage),
    [sorted, currentPage]
  )

  useEffect(() => setCurrentPage(1), [filteredProducts])

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
    window.scrollTo({ top: 200, behavior: 'smooth' })
  }

  const handleFilteredDataChange = useCallback(
    (newFilteredData: ProductDto[]) => {
      setFilteredProducts(newFilteredData)
    },
    []
  )

  // Render
  const isInCardDetail = viewMode === 'cards' && inlineProduct
  const cssCard = isInCardDetail ? 'lg:justify-start' : 'lg:justify-end'

  return (
    <>
      {/* Header */}
      <div className="mb-6 flex w-full flex-col flex-wrap items-center justify-center gap-4 sm:flex-row lg:justify-between">
        <div className="text-md text-muted-foreground w-full lg:w-[260px]">
          <div className="text-center lg:pl-4 lg:text-left">
            Résultat(s) : {filteredProducts.length} produit
            {filteredProducts.length !== 1 && 's'}
          </div>
        </div>
        <div className={`flex items-center ${cssCard} lg:pl-11`}>
          {isInCardDetail ? (
            <div className="pl-4">
              <Button
                variant="outline"
                className="flex w-40 items-center gap-1"
                onClick={() => setInlineProduct(null)}
              >
                <ArrowLeft className="size-4" /> Retour
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
                className="bg-background grid grid-cols-2 overflow-hidden rounded-lg border"
              >
                <ToggleGroupItem
                  value="cards"
                  aria-label="Grille"
                  className="hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground flex items-center justify-center py-2"
                >
                  <LayoutGrid className="mr-1 size-4" />
                  Grille
                </ToggleGroupItem>
                <ToggleGroupItem
                  value="table"
                  aria-label="Liste"
                  className="hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground flex items-center justify-center py-2"
                >
                  <ListIcon className="mr-1 size-4" />
                  Liste
                </ToggleGroupItem>
              </ToggleGroup>
              {/* Mobile Filters */}
              {!isDesktop && (
                <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
                  <SheetTrigger asChild>
                    <Button variant="outline">
                      <SlidersHorizontal className="mr-2 size-4" />
                      Filtres
                    </Button>
                  </SheetTrigger>
                  <SheetContent
                    side="left"
                    className="w-[300px] overflow-y-auto p-0 sm:w-[380px]"
                  >
                    <FiltersPanel
                      filterData={products}
                      filterDataType="product"
                      onFilteredDataChange={handleFilteredDataChange}
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
              filterData={products}
              filterDataType="product"
              onFilteredDataChange={handleFilteredDataChange}
            />
          </div>
        )}

        <div className="relative w-full min-w-0">
          {/* Cards */}
          {viewMode === 'cards' && (
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3">
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
              ) : filteredProducts.length === 0 ? (
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
              <div className="bg-background overflow-x-auto rounded-lg border">
                <Table className="table-auto text-sm">
                  <TableHeader className="supports-[backdrop-filter]:bg-muted/60 sticky top-0 z-10 backdrop-blur">
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
