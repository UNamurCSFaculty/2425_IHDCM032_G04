import { Badge } from '@/components/ui/badge'
import {
  Card,
  CardAction,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { IconTrendingDown, IconTrendingUp } from '@tabler/icons-react'
import * as React from 'react'
import { useQuery } from '@tanstack/react-query'
import { getDashboardCardsOptions } from '@/api/generated/@tanstack/react-query.gen.ts'
import { AppSkeleton } from '@/components/Skeleton/AppSkeleton.tsx'
import { useTranslation } from 'react-i18next'

type Stats = {
  totalNbUsers: number
  totalNbUsersTendency: number
  pendingValidation: number
  pendingValidationTendency: number
  totalAuctions: number
  totalAuctionsTendency: number
  auctionsConcluded: number
  auctionsConcludedTendency: number
  totalLotWeightKg: number
  totalLotWeightKgTendency: number
  totalSoldWeightKg: number
  totalSoldWeightKgTendency: number
  totalSalesAmount: number
  totalSalesAmountTendency: number
  monthlySalesAmount: number
  monthlySalesAmountTendency: number
  grade1Price: number
  grade1PriceTendency: number
  grade2Price: number
  grade2PriceTendency: number
  grade3Price: number
  grade3PriceTendency: number
  horsCategoryPrice: number
  horsCategoryTendency: number
  transformedPrice: number
  transformedPriceTendency: number
}

/**
 * Composant React pour afficher les cartes de statistiques du tableau de bord
 */
export function SectionCards() {
  const { t } = useTranslation()

  const {
    data: rawStats,
    isLoading,
    error,
  } = useQuery(getDashboardCardsOptions())

  const stats = React.useMemo<Stats | null>(() => {
    if (!rawStats) return null
    const {
      totalNbUsers = 0,
      totalNbUsersTendency = 0,
      pendingValidation = 0,
      pendingValidationTendency = 0,
      totalAuctions = 0,
      totalAuctionsTendency = 0,
      auctionsConcluded = 0,
      auctionsConcludedTendency = 0,
      totalLotWeightKg = 0,
      totalLotWeightKgTendency = 0,
      totalSoldWeightKg = 0,
      totalSoldWeightKgTendency = 0,
      totalSalesAmount = 0,
      totalSalesAmountTendency = 0,
      monthlySalesAmount = 0,
      monthlySalesAmountTendency = 0,
      grade1Price = 0,
      grade1PriceTendency = 0,
      grade2Price = 0,
      grade2PriceTendency = 0,
      grade3Price = 0,
      grade3PriceTendency = 0,
      horsCategoryPrice = 0,
      horsCategoryTendency = 0,
      transformedPrice = 0,
      transformedPriceTendency = 0,
    } = rawStats

    return {
      totalNbUsers,
      totalNbUsersTendency,
      pendingValidation,
      pendingValidationTendency,
      totalAuctions,
      totalAuctionsTendency,
      auctionsConcluded,
      auctionsConcludedTendency,
      totalLotWeightKg,
      totalLotWeightKgTendency,
      totalSoldWeightKg,
      totalSoldWeightKgTendency,
      totalSalesAmount,
      totalSalesAmountTendency,
      monthlySalesAmount,
      monthlySalesAmountTendency,
      grade1Price,
      grade1PriceTendency,
      grade2Price,
      grade2PriceTendency,
      grade3Price,
      grade3PriceTendency,
      horsCategoryPrice,
      horsCategoryTendency,
      transformedPrice,
      transformedPriceTendency,
    }
  }, [rawStats])

  const cards = React.useMemo(() => {
    if (!stats) return []
    return [
      {
        title: t('admin.dashboard.cards.totalNbUsers.title'),
        value: stats.totalNbUsers.toLocaleString(),
        tendency: stats.totalNbUsersTendency,
        description: t('admin.dashboard.cards.totalNbUsers.description'),
      },
      {
        title: t('admin.dashboard.cards.pendingValidation.title'),
        value: stats.pendingValidation.toLocaleString(),
        tendency: stats.pendingValidationTendency,
        description: t('admin.dashboard.cards.pendingValidation.description'),
      },
      {
        title: t('admin.dashboard.cards.totalAuctions.title'),
        value: stats.totalAuctions.toLocaleString(),
        tendency: stats.totalAuctionsTendency,
        description: t('admin.dashboard.cards.totalAuctions.description'),
      },
      {
        title: t('admin.dashboard.cards.auctionsConcluded.title'),
        value: stats.auctionsConcluded.toLocaleString(),
        tendency: stats.auctionsConcludedTendency,
        description: t('admin.dashboard.cards.auctionsConcluded.description'),
      },
      {
        title: t('admin.dashboard.cards.totalLotWeightKg.title'),
        value:
          Math.round(stats.totalLotWeightKg * 0.001).toLocaleString() + ' T',
        tendency: stats.totalLotWeightKgTendency,
        description: t('admin.dashboard.cards.totalLotWeightKg.description'),
      },
      {
        title: t('admin.dashboard.cards.totalSoldWeightKg.title'),
        value:
          Math.round(stats.totalSoldWeightKg * 0.001).toLocaleString() + ' T',
        tendency: stats.totalSoldWeightKgTendency,
        description: t('admin.dashboard.cards.totalSoldWeightKg.description'),
      },
      {
        title: t('admin.dashboard.cards.totalSalesAmount.title'),
        value:
          Math.round(stats.totalSalesAmount * 0.000001).toLocaleString() +
          ' M CFA',
        tendency: stats.totalSalesAmountTendency,
        description: t('admin.dashboard.cards.totalSalesAmount.description'),
      },
      {
        title: t('admin.dashboard.cards.monthlySalesAmount.title'),
        value:
          Math.round(stats.monthlySalesAmount * 0.000001).toLocaleString() +
          ' M CFA',
        tendency: stats.monthlySalesAmountTendency,
        description: t('admin.dashboard.cards.monthlySalesAmount.description'),
      },
      {
        title: t('admin.dashboard.cards.grade1Price.title'),
        value: Math.round(stats.grade1Price).toLocaleString() + ' CFA/kg',
        tendency: stats.grade1PriceTendency,
        description: t('admin.dashboard.cards.grade1Price.description'),
      },
      {
        title: t('admin.dashboard.cards.grade2Price.title'),
        value: Math.round(stats.grade2Price).toLocaleString() + ' CFA/kg',
        tendency: stats.grade2PriceTendency,
        description: t('admin.dashboard.cards.grade2Price.description'),
      },
      {
        title: t('admin.dashboard.cards.grade3Price.title'),
        value: Math.round(stats.grade3Price).toLocaleString() + ' CFA/kg',
        tendency: stats.grade3PriceTendency,
        description: t('admin.dashboard.cards.grade3Price.description'),
      },
      {
        title: t('admin.dashboard.cards.horsCategoryPrice.title'),
        value: Math.round(stats.horsCategoryPrice).toLocaleString() + ' CFA/kg',
        tendency: stats.horsCategoryTendency,
        description: t('admin.dashboard.cards.horsCategoryPrice.description'),
      },
      {
        title: t('admin.dashboard.cards.transformedPrice.title'),
        value: Math.round(stats.transformedPrice).toLocaleString() + ' CFA/kg',
        tendency: stats.transformedPriceTendency,
        description: t('admin.dashboard.cards.transformedPrice.description'),
      },
    ]
  }, [stats, t])

  if (isLoading) {
    return <AppSkeleton />
  }

  if (error || !stats) {
    return <div>{t('admin.dashboard.cards.error_loading')}</div>
  }

  return (
    <div className="grid grid-cols-1 gap-4 px-4 lg:grid-cols-2 xl:grid-cols-4">
      {cards.map(({ title, value, tendency, description }) => {
        const isUp = tendency >= 0
        const pct = Math.abs(tendency).toFixed(1) + '%'

        return (
          <Card key={title} className="@container/card">
            <CardHeader>
              <CardDescription>{title}</CardDescription>
              <CardTitle className="text-2xl font-semibold tabular-nums">
                {value}
              </CardTitle>
              <CardAction>
                <Badge variant="outline" className="flex items-center gap-1">
                  {isUp ? <IconTrendingUp /> : <IconTrendingDown />}
                  {isUp ? '+' : '-'}
                  {pct}
                </Badge>
              </CardAction>
            </CardHeader>
            <CardFooter className="flex-col items-start gap-1.5 text-sm">
              <div className="line-clamp-1 flex gap-2 font-medium">
                {description}{' '}
                {isUp ? (
                  <IconTrendingUp className="size-4" />
                ) : (
                  <IconTrendingDown className="size-4" />
                )}
              </div>
              <div className="text-muted-foreground">
                {t('admin.dashboard.cards.compared_to_30_days')}
              </div>
            </CardFooter>
          </Card>
        )
      })}
    </div>
  )
}
