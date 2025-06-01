import { InfoTile } from '../InfoTile'
import {
  type HarvestProductDto,
  type ProductDto,
  ProductType,
  type TransformedProductDto,
} from '@/api/generated'
import { Badge } from '@/components/ui/badge'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { TableCell, TableRow } from '@/components/ui/table'
import cities from '@/data/cities.json'
import regions from '@/data/regions.json'
import { cn } from '@/lib/utils'
import { formatDate, formatWeight } from '@/utils/formatter'
import {
  Clock,
  Earth,
  MapPin,
  Package,
  ShieldCheck,
  ShoppingCart,
  UserCircle2,
  UserSearch,
  Wheat,
} from 'lucide-react'
import React from 'react'
import { useTranslation } from 'react-i18next'

export type CardLayout = 'grid' | 'row'

interface ProductCardProps {
  product: ProductDto
  layout: CardLayout
  isDetail?: boolean
  onDetails: () => void
}

const ProductCard: React.FC<ProductCardProps> = ({ product, layout }) => {
  const { t } = useTranslation()

  const regionLabel = product.store.address.regionId
    ? (regions[product.store.address.regionId - 1] ?? '—')
    : '—'
  const cityLabel = product.store.address.cityId
    ? (cities[product.store.address.cityId - 1] ?? '—')
    : '—'

  if (layout === 'row') {
    return (
      <TableRow className="hover:bg-muted/50 h-10" key={product.id}>
        <TableCell className="truncate font-medium">
          {t('database.' + product.type)}
        </TableCell>
        <TableCell>
          {(() => {
            const { firstName, lastName } =
              product.type === ProductType.HARVEST
                ? (product as HarvestProductDto).producer
                : (product as TransformedProductDto).transformer
            return `${firstName} ${lastName}`
          })()}
        </TableCell>
        <TableCell>{formatWeight(product.weightKg)}</TableCell>
        <TableCell>
          {product.qualityControl?.quality.name ??
            t('common.not_applicable_short')}
        </TableCell>
        <TableCell>{t('common.not_applicable_short')}</TableCell>
        <TableCell>{formatDate(product.deliveryDate)}</TableCell>
        <TableCell className="truncate">{cityLabel}</TableCell>
        <TableCell className="truncate">{regionLabel}</TableCell>
        <TableCell>{product.store.name}</TableCell>
      </TableRow>
    )
  }

  return (
    <Card
      className={cn(
        'flex flex-col gap-2 overflow-hidden bg-gradient-to-b from-green-50/50 to-yellow-50/50 shadow-sm transition-all hover:scale-102 hover:shadow-lg'
      )}
    >
      <CardHeader className="pb-2">
        <CardTitle className="flex items-center gap-2 truncate text-base font-semibold">
          {t('product.lot_label', { id: product.id })}
          <Badge variant={'default'} className="ml-auto shrink-0">
            {t('product.product_type_label', {
              type: t('database.' + product.type),
            })}
          </Badge>
        </CardTitle>
        <CardDescription className="flex flex-col flex-wrap items-center justify-center gap-1.5 text-sm text-neutral-700">
          <div className="mt-2 flex items-center gap-1">
            <Badge variant="outline">
              <MapPin className="size-3.5" /> {cityLabel}, {regionLabel}
            </Badge>
          </div>
          <div className="flex items-center gap-1">
            <Badge variant="outline">
              <ShoppingCart className="size-4" />
              {product.store.name}
            </Badge>
          </div>
          <div className="flex items-center gap-1">
            <Badge variant="outline">
              <UserCircle2 className="size-3.5" />
              {(() => {
                if (product.type === ProductType.HARVEST) {
                  const hp = product as HarvestProductDto
                  return `${hp.producer.firstName} ${hp.producer.lastName}`
                } else {
                  const tp = product as TransformedProductDto
                  return `${tp.transformer.firstName} ${tp.transformer.lastName}`
                }
              })()}
            </Badge>
          </div>
        </CardDescription>
      </CardHeader>
      <CardContent className="flex-grow space-y-3 pt-0">
        <div className="flex items-center justify-between">
          <InfoTile
            icon={<Clock className="size-4" />}
            label={t('product.deposit_date_label')}
            size="sm"
          >
            {formatDate(product.deliveryDate)}
          </InfoTile>
        </div>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <InfoTile
            icon={<ShieldCheck className="size-4" />}
            label={t('product.quality_label')}
          >
            {product.qualityControl?.quality.name ??
              t('common.not_applicable_short')}
          </InfoTile>
          <InfoTile
            icon={<Package className="size-4" />}
            label={t('product.quantity_label')}
          >
            {formatWeight(product.weightKg)}
          </InfoTile>

          <InfoTile
            icon={<UserSearch className="size-4" />}
            label={t('product.quality_inspector_label')}
          >
            {!product.qualityControl.qualityInspector
              ? t('common.not_applicable_short')
              : product.qualityControl.qualityInspector.firstName +
                ' ' +
                product.qualityControl.qualityInspector.lastName}
          </InfoTile>
          <InfoTile
            icon={<Earth className="size-4" />}
            label={t('product.origin_label')}
          >
            {(() => {
              if (product.type === ProductType.HARVEST) {
                const hp = product as HarvestProductDto
                return hp.field.identifier
              } else if (product.type === ProductType.TRANSFORMED) {
                return t('product.origin_transformed_default')
              } else {
                return t('common.not_applicable_short')
              }
            })()}
          </InfoTile>
        </div>
        {product.type === ProductType.TRANSFORMED &&
          (() => {
            const tp = product as TransformedProductDto
            return (
              <InfoTile
                icon={<Wheat className="size-4" />}
                label={t('product.raw_materials_label')}
                size="sm"
              >
                {tp.harvestProducts && tp.harvestProducts.length > 0 ? (
                  <ul className="lg">
                    {tp.harvestProducts.map((hp, index) => (
                      <li key={index}>
                        t('product.raw_materials_label') (
                        {hp.qualityControl.quality.name})
                      </li>
                    ))}
                  </ul>
                ) : (
                  t('common.not_applicable_short')
                )}
              </InfoTile>
            )
          })()}
      </CardContent>
    </Card>
  )
}

export default ProductCard
