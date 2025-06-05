import { InfoTile } from '../../InfoTile'
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
// Simple JSON import (no async fetch) -----------------------------------------
import cities from '@/data/cities.json'
import regions from '@/data/regions.json'
import { TradeStatus, cn, getPricePerKg } from '@/lib/utils'
import dayjs from '@/utils/dayjs-config'
import { formatPrice, formatWeight } from '@/utils/formatter'
import {
  Clock,
  DollarSign,
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
export type UserRole = 'buyer' | 'seller'
interface AuctionCardProps {
  auction: AuctionDto
  layout: CardLayout
  isDetail?: boolean
  role: UserRole
  onDetails: () => void
}

const AuctionCard: React.FC<AuctionCardProps> = ({
  auction,
  layout,
  isDetail = false,
  onDetails,
}) => {
  const { t } = useTranslation()

  const bestBid = auction.bids.reduce(
    (max, b) => (b.amount > max ? b.amount : max),
    0
  )
  const expires = dayjs.utc(auction.expirationDate)
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
      <TableRow className="hover:bg-muted/50 h-10" key={auction.id}>
        <TableCell className="truncate font-medium">
          {t('database.' + auction.product.type)}
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
        <TableCell className="space-x-1 text-right">
          <Button size="sm" variant="outline" onClick={onDetails}>
            {t('buttons.details')}
          </Button>
        </TableCell>
      </TableRow>
    )
  }

  /* -------------------- Card layout -------------------- */
  return (
    <Card
      className={cn(
        'flex flex-col gap-2 overflow-hidden bg-gradient-to-b from-green-50/50 to-yellow-50/50 shadow-sm transition-all hover:scale-102 hover:shadow-lg',
        isEndingSoon &&
          auction.status.name !== TradeStatus.ACCEPTED &&
          'bg-red-50/50 ring-2 ring-red-400'
      )}
    >
      <CardHeader className="pb-2">
        <CardTitle className="flex items-center gap-2 truncate text-base font-semibold">
          {t('database.' + auction.product.type)} ·{' '}
          {t('auction.lot_label', { id: auction.product.id })}
          <Badge
            variant={auction.bids.length ? 'default' : 'outline'}
            className="ml-auto shrink-0"
          >
            {t('auction.bid_count', { count: auction.bids.length })}
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
      <CardContent className="flex-grow space-y-3 pt-0">
        <div className="flex items-center justify-between">
          {auction.status.name === TradeStatus.OPEN ? (
            <InfoTile
              icon={<Clock className="size-4" />}
              label={t('auction.expires_in')}
              size="lg"
            >
              <CountdownTimer
                status={auction.status.name}
                endDate={expires.toDate()}
              />
            </InfoTile>
          ) : (
            <InfoTile label="" size="lg">
              <CountdownTimer
                status={auction.status.name}
                endDate={expires.toDate()}
              />
            </InfoTile>
          )}
        </div>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <InfoTile
            icon={<ShieldCheck className="size-4" />}
            label={t('product.quality_label')}
          >
            {auction.product.qualityControl?.quality.name ?? 'N/A'}
          </InfoTile>
          <InfoTile
            icon={<Package className="size-4" />}
            label={t('product.quantity_label')}
          >
            {formatWeight(auction.productQuantity)}
          </InfoTile>
          <InfoTile
            icon={<DollarSign className="size-4" />}
            label={t('auction.asking_price')}
          >
            {formatPrice.format(auction.price)}
            <br />
            <span className="text-xs font-normal text-neutral-700">
              {formatPrice.format(
                getPricePerKg(auction.price, auction.productQuantity)
              )}
              /kg
            </span>
          </InfoTile>
          <InfoTile
            icon={<TrendingUp className="size-4" />}
            label={t('auction.best_bid')}
          >
            {bestBid ? (
              <>
                {formatPrice.format(bestBid)}
                <br />
                <span className="text-xs font-normal text-neutral-700">
                  {formatPrice.format(
                    getPricePerKg(bestBid, auction.productQuantity)
                  )}
                  /kg
                </span>
              </>
            ) : (
              '—'
            )}
          </InfoTile>
        </div>
      </CardContent>
      <CardFooter className="flex flex-col gap-2 pt-1 pb-1">
        {!isDetail && (
          <Button variant="default" className="w-full" onClick={onDetails}>
            {t('buttons.view_details')}
          </Button>
        )}
      </CardFooter>
    </Card>
  )
}

export default AuctionCard
