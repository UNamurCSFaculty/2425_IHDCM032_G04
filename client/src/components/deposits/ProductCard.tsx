import { InfoTile } from '../InfoTile'
import type {
  HarvestProductDto,
  ProductDto,
  TransformedProductDto,
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
// Simple JSON import (no async fetch) -----------------------------------------
import cities from '@/data/cities.json'
import regions from '@/data/regions.json'
import { ProductType, cn } from '@/lib/utils'
import { formatDate, formatWeight } from '@/utils/formatter'
import {
  Clock,
  Earth,
  MapPin,
  Package,
  ShieldCheck,
  ShoppingCart,
  TrendingUp,
  UserCircle2,
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

  /* -------------------- Table row layout -------------------- */
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
        <TableCell>{product.qualityControl?.quality.name ?? 'N/A'}</TableCell>
        <TableCell>N/A</TableCell>
        <TableCell>{formatDate(product.deliveryDate)}</TableCell>
        <TableCell className="truncate">{cityLabel}</TableCell>
        <TableCell className="truncate">{regionLabel}</TableCell>
        <TableCell>{product.store.name}</TableCell>
      </TableRow>
    )
  }

  /* -------------------- Card layout -------------------- */
  return (
    <Card
      className={cn(
        'flex flex-col gap-2 overflow-hidden bg-gradient-to-b from-green-50/50 to-yellow-50/50 shadow-sm transition-all hover:scale-102 hover:shadow-lg'
      )}
    >
      <CardHeader className="pb-2">
        <CardTitle className="flex items-center gap-2 truncate text-base font-semibold">
          Lot n°{product.id}
          <Badge variant={'default'} className="ml-auto shrink-0">
            Produit {t('database.' + product.type)}
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
              {product.type === ProductType.HARVEST
                ? (product as HarvestProductDto).producer.firstName +
                  ' ' +
                  (product as HarvestProductDto).producer.lastName
                : (product as TransformedProductDto).transformer.firstName +
                  ' ' +
                  (product as TransformedProductDto).transformer.lastName}
            </Badge>
          </div>
        </CardDescription>
      </CardHeader>
      <CardContent className="flex-grow space-y-3 pt-0">
        <div className="flex items-center justify-between">
          <InfoTile
            icon={<Clock className="size-4" />}
            label="Date de dépôt"
            size="lg"
          >
            {formatDate(product.deliveryDate)}
          </InfoTile>
        </div>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <InfoTile icon={<ShieldCheck className="size-4" />} label="Qualité">
            {product.qualityControl?.quality.name ?? 'N/A'}
          </InfoTile>
          <InfoTile icon={<Package className="size-4" />} label="Quantité">
            {formatWeight(product.weightKg)}
          </InfoTile>
          <InfoTile icon={<Earth className="size-4" />} label="Origine">
            {product.type === ProductType.HARVEST
              ? 'N/A' // (product as HarvestProductDto).field.id
              : 'N/A'}
          </InfoTile>
          <InfoTile
            icon={<TrendingUp className="size-4" />}
            label="Qualiticien"
          >
            {!product.qualityControl.qualityInspector
              ? 'N/A'
              : product.qualityControl.qualityInspector.firstName +
                ' ' +
                product.qualityControl.qualityInspector.lastName}
          </InfoTile>
        </div>
      </CardContent>
      {/* <CardFooter className="pt-1 pb-1  flex flex-col gap-2"></CardFooter> */}
    </Card>
  )
}

export default ProductCard
