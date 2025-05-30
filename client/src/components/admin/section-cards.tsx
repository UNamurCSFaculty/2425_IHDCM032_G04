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
}

export function SectionCards() {
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
    }
  }, [rawStats])

  if (isLoading) {
    return <AppSkeleton />
  }
  if (error || !stats) {
    return <div>Error loading dashboard</div>
  }

  const cards = [
    {
      title: "Nombre d'utilisateurs",
      value: stats.totalNbUsers.toLocaleString(),
      tendency: stats.totalNbUsersTendency,
      description: 'Inscriptions',
    },
    {
      title: 'Nouvelles inscriptions',
      value: stats.pendingValidation.toLocaleString(),
      tendency: stats.pendingValidationTendency,
      description: "En attente d'approbation",
    },
    {
      title: 'Nouvelles enchères',
      value: stats.totalAuctions.toLocaleString(),
      tendency: stats.totalAuctionsTendency,
      description: 'Enchères en cours',
    },
    {
      title: 'Enchères conclues',
      value: stats.auctionsConcluded.toLocaleString(),
      tendency: stats.auctionsConcludedTendency,
      description: '30 derniers jours',
    },
    {
      title: 'Volume total',
      value: Math.round(stats.totalLotWeightKg * 0.001).toLocaleString() + ' T',
      tendency: stats.totalLotWeightKgTendency,
      description: 'Poids total vendu',
    },
    {
      title: 'Volume mensuel',
      value:
        Math.round(stats.totalSoldWeightKg * 0.001).toLocaleString() + ' T',
      tendency: stats.totalSoldWeightKgTendency,
      description: '30 derniers jours',
    },
    {
      title: 'Montant total',
      value:
        Math.round(stats.totalSalesAmount * 0.000001).toLocaleString() +
        ' M CFA',
      tendency: stats.totalSalesAmountTendency,
      description: 'Toutes les ventes',
    },
    {
      title: 'Ventes mensuelles',
      value:
        Math.round(stats.monthlySalesAmount * 0.000001).toLocaleString() +
        ' M CFA',
      tendency: stats.monthlySalesAmountTendency,
      description: '30 derniers jours',
    },
  ] as const

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
                Par rapport à il y a 30 jours
              </div>
            </CardFooter>
          </Card>
        )
      })}
    </div>
  )
}
