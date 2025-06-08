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
import { useTranslation } from 'react-i18next'

export type ViewMode = 'cards' | 'table' | 'map'

export const sortOptions = [
  { value: 'deliveryDate-asc', label: 'sort.date_asc' },
  { value: 'deliveryDate-desc', label: 'sort.date_desc' },
  { value: 'weight-asc', label: 'sort.weight_asc' },
  { value: 'weight-desc', label: 'sort.weight_desc' },
] as const
export type SortOptionValue = (typeof sortOptions)[number]['value']

export const perPage = 12

interface ProductListProps {
  products: ProductDto[]
}

/**
 * Composant pour afficher une liste de produits
 */
const ProductList: React.FC<ProductListProps> = ({ products }) => {
  const isDesktop = useMediaQuery('(min-width: 1024px)')
  const { t } = useTranslation()

  const [viewMode, setViewMode] = useState<ViewMode>('cards')
  const [sheetOpen, setSheetOpen] = useState(false)
  const [inlineProduct, setInlineProduct] = useState<ProductDto | null>(null)
  const [sort, setSort] = useState<SortOptionValue>('deliveryDate-asc')

  const [filteredProducts, setFilteredProducts] = useState<ProductDto[]>([])

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

  const isInCardDetail = viewMode === 'cards' && inlineProduct
  const cssCard = isInCardDetail ? 'lg:justify-start' : 'lg:justify-end'

  return (
    <>
      <div className="mb-6 flex w-full flex-col flex-wrap items-center justify-center gap-4 sm:flex-row lg:justify-between">
        <div className="text-md text-muted-foreground w-full lg:w-[260px]">
          <div className="text-center lg:pl-4 lg:text-left">
            {t('product.results_count', { count: filteredProducts.length })}
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
                <ArrowLeft className="size-4" /> {t('buttons.back')}
              </Button>
            </div>
          ) : (
            <div className="flex w-full flex-col flex-wrap items-center justify-center gap-2 lg:flex-row lg:justify-end">
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
                  aria-label={t('view_mode.grid_label')}
                  className="hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground flex items-center justify-center py-2"
                >
                  <LayoutGrid className="mr-1 size-4" />
                  {t('view_mode.grid_label')}
                </ToggleGroupItem>
                <ToggleGroupItem
                  value="table"
                  aria-label={t('view_mode.list_label')}
                  className="hover:bg-muted data-[state=on]:bg-primary data-[state=on]:text-primary-foreground flex items-center justify-center py-2"
                >
                  <ListIcon className="mr-1 size-4" />
                  {t('view_mode.list_label')}
                </ToggleGroupItem>
              </ToggleGroup>
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

      <div className="grid items-start gap-6 px-6 lg:grid-cols-[260px_1fr]">
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
          {viewMode === 'cards' && (
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3">
              {inlineProduct ? (
                <>
                  <ProductCard product={inlineProduct} layout="grid" isDetail />
                  <div className="col-span-full lg:col-span-2"></div>
                </>
              ) : filteredProducts.length === 0 ? (
                <EmptyState className="col-span-full" />
              ) : (
                <>
                  {paginated.map(p => (
                    <ProductCard key={p.id} product={p} layout="grid" />
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

          {viewMode === 'table' && (
            <>
              <div className="bg-background overflow-x-auto rounded-lg border">
                <Table className="table-auto text-sm">
                  <TableHeader className="supports-[backdrop-filter]:bg-muted/60 sticky top-0 z-10 backdrop-blur">
                    <TableRow className="h-9 bg-neutral-100">
                      <TableHead>{t('product.merchandise_label')}</TableHead>
                      <TableHead>{t('product.owner_label')}</TableHead>
                      <TableHead>{t('product.quantity_label')}</TableHead>
                      <TableHead>{t('product.quality_label')}</TableHead>
                      <TableHead>{t('product.origin_label')}</TableHead>
                      <TableHead>{t('product.deposit_date_label')}</TableHead>
                      <TableHead>{t('form.city')}</TableHead>
                      <TableHead>{t('address.region_label')}</TableHead>
                      <TableHead>{t('product.warehouse_label')}</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {paginated.map(p => (
                      <ProductCard key={p.id} product={p} layout="row" />
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
        </div>
      </div>
    </>
  )
}

export default ProductList
