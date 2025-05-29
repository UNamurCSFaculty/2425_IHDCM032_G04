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

interface DashboardCardsDto {
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
}

export function SectionCards() {
  const [stats, setStats] = React.useState<DashboardCardsDto | null>(null)
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    const ctl = new AbortController()

    ;(async () => {
      try {
        const res = await fetch('http://localhost:8080/api/dashboard/cards', {
          credentials: 'include',
          signal: ctl.signal,
        })

        if (res.status === 401) {
          console.warn('Non autorisé, redirection possible…')
          return
        }

        if (!res.ok) {
          console.error(`Erreur HTTP ${res.status}`)
          return
        }

        const contentType = res.headers.get('Content-Type') || ''
        if (!contentType.includes('application/json')) {
          console.error('Réponse inattendue, pas du JSON :', contentType)
          return
        }

        const data = (await res.json()) as DashboardCardsDto
        setStats(data)
      } catch (err: any) {
        if (err.name === 'AbortError') {
          return
        }
        console.error('Erreur inattendue', err)
      } finally {
        setLoading(false)
      }
    })()

    return () => {
      ctl.abort()
    }
  }, [])

  if (loading) {
    return <div>Loading dashboard…</div>
  }
  if (!stats) {
    return <div>Error loading dashboard</div>
  }

  const cards = [
    {
      title: 'Total Users',
      value: stats.totalNbUsers.toLocaleString(),
      tendency: stats.totalNbUsersTendency,
      description: 'Registrations',
    },
    {
      title: 'Pending Validation',
      value: stats.pendingValidation.toLocaleString(),
      tendency: stats.pendingValidationTendency,
      description: 'Awaiting approval',
    },
    {
      title: 'Total Auctions',
      value: stats.totalAuctions.toLocaleString(),
      tendency: stats.totalAuctionsTendency,
      description: 'Auctions created',
    },
    {
      title: 'Auctions Concluded',
      value: stats.auctionsConcluded.toLocaleString(),
      tendency: stats.auctionsConcludedTendency,
      description: 'Completed auctions',
    },
    {
      title: 'Total Lot Weight (kg)',
      value: stats.totalLotWeightKg.toLocaleString(),
      tendency: stats.totalLotWeightKgTendency,
      description: 'Cumulative weight listed',
    },
    {
      title: 'Sold Weight (kg)',
      value: stats.totalSoldWeightKg.toLocaleString(),
      tendency: stats.totalSoldWeightKgTendency,
      description: 'Weight sold last 30 days',
    },
    {
      title: 'Total Sales Amount',
      value: stats.totalSalesAmount.toLocaleString(),
      tendency: stats.totalSalesAmountTendency,
      description: 'Revenue last 30 days',
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
                Compared to 30 days ago
              </div>
            </CardFooter>
          </Card>
        )
      })}
    </div>
  )
}
