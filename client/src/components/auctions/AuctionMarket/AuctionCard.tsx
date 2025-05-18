import { InfoTile } from './InfoTile'
import type { AuctionDto } from '@/api/generated'
import { CountdownTimer } from '@/components/CountDownTimer'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { TableCell, TableRow } from '@/components/ui/table'
import { cn, getCities, getRegions } from '@/lib/utils'
import dayjs from '@/utils/dayjs-config'
import { formatPrice, formatWeight } from '@/utils/formatter'
import {
  Clock,
  DollarSign,
  MapPin,
  NotebookText,
  Package,
  ShoppingCart,
  TrendingUp,
  UserCircle2,
} from 'lucide-react'
import React, { useEffect, useState } from 'react'

export type CardLayout = 'grid' | 'row'
export type UserRole = 'buyer' | 'seller'
interface AuctionCardProps {
  auction: AuctionDto
  layout: CardLayout
  isDetail?: boolean
  role: UserRole
  onDetails: () => void
  onMakeBid?: (id: number) => void
  onBuyNow?: (id: number) => void
}

const AuctionCard: React.FC<AuctionCardProps> = ({
  auction,
  layout,
  isDetail = false,
  onDetails,
}) => {
  const [cities, setCities] = useState<string[]>([])
  const [regions, setRegions] = useState<string[]>([])

  useEffect(() => {
    // charge une seule fois
    void getCities().then(setCities)
    void getRegions().then(setRegions)
  }, [])

  const bestBid = auction.bids.reduce(
    (max, b) => (b.amount > max ? b.amount : max),
    0
  )
  const expires = dayjs(auction.expirationDate)
  const isEndingSoon = expires.diff(dayjs(), 'hour') < 24

  const regionLabel = auction.product.store.address.regionId
    ? (regions[auction.product.store.address.regionId - 1] ?? '—')
    : '—'
  const cityLabel = auction.product.store.address.cityId
    ? (cities[auction.product.store.address.cityId - 1] ?? '—')
    : '—'

  /* -------------------- Table row layout -------------------- */
  if (layout === 'row') {
    return (
      <TableRow className="h-10 hover:bg-muted/50" key={auction.id}>
        <TableCell className="font-medium truncate">
          {auction.product.type === 'harvest' ? 'Récolte' : 'Transformé'}
        </TableCell>
        <TableCell>
          <CountdownTimer endDate={expires.toDate()} />
        </TableCell>
        <TableCell className="truncate">{regionLabel}</TableCell>
        <TableCell className="truncate">{cityLabel}</TableCell>
        <TableCell>{formatWeight(auction.productQuantity)}</TableCell>
        <TableCell>
          {auction.product.qualityControl?.quality.name ?? 'N/A'}
        </TableCell>
        <TableCell className="text-right">
          {formatPrice.format(auction.price)}
        </TableCell>
        <TableCell className="text-right">
          {bestBid ? formatPrice.format(bestBid) : '—'}
        </TableCell>
        <TableCell className="text-right space-x-1">
          <Button size="sm" variant="outline" onClick={onDetails}>
            Détails
          </Button>
          {/*
          {role === 'buyer' && onMakeBid && (
            <Button
              size="sm"
              className="bg-emerald-600 text-white"
              onClick={() => onMakeBid(auction.id)}
            >
              Enchérir
            </Button>
          )}
        
          {role === 'buyer' && auction.options?.buyNowPrice && (
            <Button
              size="sm"
              className="bg-amber-600 text-white"
              onClick={() => onBuyNow?.(auction.id)}
            >
              Acheter
            </Button>
          )}
                */}
        </TableCell>
      </TableRow>
    )
  }

  /* -------------------- Card layout -------------------- */
  return (
    <Card
      className={cn(
        'overflow-hidden shadow-sm gap-2 transition-all flex flex-col hover:shadow-lg bg-gradient-to-b to-yellow-50/50 from-green-50/50 hover:scale-102',
        isEndingSoon && 'ring-2 ring-red-400 bg-red-50/50'
      )}
    >
      <CardHeader className="pb-2">
        <CardTitle className="text-base font-semibold flex items-center gap-2 truncate">
          {auction.product.type === 'harvest' ? 'Brut' : 'Transformé'} · lot{' '}
          {auction.product.id}
          <Badge
            variant={auction.bids.length ? 'default' : 'outline'}
            className="ml-auto shrink-0"
          >
            {auction.bids.length} offre{auction.bids.length === 1 ? '' : 's'}
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
              {auction.product.store.name}
            </Badge>
          </div>
          <div className="flex items-center gap-1">
            <Badge variant="outline">
              <UserCircle2 className="size-3.5" /> {auction.trader.firstName}{' '}
              {auction.trader.lastName}
            </Badge>
          </div>
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-3 pt-0 flex-grow">
        <div className="flex items-center justify-between">
          <InfoTile
            icon={<Clock className="size-4" />}
            label="Expire dans"
            size="lg"
          >
            <CountdownTimer endDate={expires.toDate()} />
          </InfoTile>
        </div>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <InfoTile icon={<NotebookText className="size-4" />} label="Qualité">
            {auction.product.qualityControl?.quality.name ?? 'N/A'}
          </InfoTile>
          <InfoTile icon={<Package className="size-4" />} label="Quantité">
            {formatWeight(auction.productQuantity)}
          </InfoTile>
          <InfoTile
            icon={<DollarSign className="size-4" />}
            label="Prix demandé"
          >
            {formatPrice.format(auction.price)}
          </InfoTile>
          <InfoTile
            icon={<TrendingUp className="size-4" />}
            label="Meilleure offre"
          >
            {bestBid ? formatPrice.format(bestBid) : '—'}
          </InfoTile>
        </div>
      </CardContent>
      <CardFooter className="pt-1 pb-1  flex flex-col gap-2">
        {/*
        {role === 'buyer' && auction.options?.buyNowPrice && (
          <Button
            className="bg-amber-600 hover:bg-amber-700 text-white w-full"
            onClick={() => onBuyNow?.(auction.id)}
          >
            Achat immédiat {formatPrice.format(auction.options.buyNowPrice)}
          </Button>
        )}
        {role === 'buyer' && onMakeBid && (
          <Button className="w-full" onClick={() => onMakeBid(auction.id)}>
            Enchérir
          </Button>
        )}
        */}
        {!isDetail && (
          <Button variant="default" className="w-full" onClick={onDetails}>
            Voir détails
          </Button>
        )}
      </CardFooter>
    </Card>
  )
}

export default AuctionCard
