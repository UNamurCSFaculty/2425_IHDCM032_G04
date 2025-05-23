import { InfoTile } from '../InfoTile'
import type {
  HarvestProductDto,
  ProductDto,
  TransformedProductDto,
} from '@/api/generated'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
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
import { ProductType, cn, formatDate } from '@/lib/utils'
import { formatWeight } from '@/utils/formatter'
import {
  Clock,
  MapPin,
  NotebookText,
  Package,
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

const ProductCard: React.FC<ProductCardProps> = ({
  product,
  layout,
  onDetails,
}) => {
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
      <TableRow className="h-10 hover:bg-muted/50" key={product.id}>
        <TableCell className="font-medium truncate">
          {t('database.' + product.type)}
        </TableCell>
        <TableCell>Brol</TableCell>
        <TableCell className="truncate">{regionLabel}</TableCell>
        <TableCell className="truncate">{cityLabel}</TableCell>
        <TableCell>{formatWeight(product.weightKg)}</TableCell>
        <TableCell>{product.qualityControl?.quality.name ?? 'N/A'}</TableCell>
        <TableCell className="text-right">brol</TableCell>
        <TableCell className="text-right">brol</TableCell>
        <TableCell className="text-right space-x-1">
          <Button size="sm" variant="outline" onClick={onDetails}>
            Détails
          </Button>
        </TableCell>
      </TableRow>
    )
  }

  /* -------------------- Card layout -------------------- */
  return (
    <Card
      className={cn(
        'overflow-hidden shadow-sm gap-2 transition-all flex flex-col hover:shadow-lg bg-gradient-to-b to-yellow-50/50 from-green-50/50 hover:scale-102'
      )}
    >
      <CardHeader className="pb-2">
        <CardTitle className="text-base font-semibold flex items-center gap-2 truncate">
          Lot n°{product.id}
          <Badge variant={'default'} className="ml-auto shrink-0">
            Produit {t('database.' + product.type)}
          </Badge>
        </CardTitle>
        <CardDescription className="flex flex-wrap flex-col items-center gap-1.5 text-sm text-neutral-700 justify-center">
          <div className="flex items-center gap-1 mt-2">
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
      <CardContent className="space-y-3 pt-0 flex-grow">
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
          <InfoTile icon={<NotebookText className="size-4" />} label="Qualité">
            {product.qualityControl?.quality.name ?? 'N/A'}
          </InfoTile>
          <InfoTile icon={<Package className="size-4" />} label="Quantité">
            {formatWeight(product.weightKg)}
          </InfoTile>
          <InfoTile icon={<ShoppingCart className="size-4" />} label="Origine">
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
