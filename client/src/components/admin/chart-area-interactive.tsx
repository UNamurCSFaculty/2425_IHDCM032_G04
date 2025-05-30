import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from '@/components/ui/chart'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { ToggleGroup, ToggleGroupItem } from '@/components/ui/toggle-group'
import { useIsMobile } from '@/hooks/use-mobile'
import * as React from 'react'
import { Area, AreaChart, CartesianGrid, XAxis } from 'recharts'
import { useQuery } from '@tanstack/react-query'
import { getDashboardGraphicSeriesOptions } from '@/api/generated/@tanstack/react-query.gen'
import { AppSkeleton } from '@/components/Skeleton/AppSkeleton.tsx'

interface DashboardGraphicDto {
  date: string // "2025-01-01T00:00:00"
  totalOpenAuctions: number
  totalNewAuctions: number
}

export function ChartAreaInteractive() {
  const isMobile = useIsMobile()

  // 1) Lancement de la requête, on précise le generic pour rawData
  const {
    data: rawData = [],
    isLoading,
    error,
  } = useQuery(getDashboardGraphicSeriesOptions())

  // 2) On filtre / affine le type : on ne garde que les éléments complets
  const chartData: DashboardGraphicDto[] = React.useMemo(() => {
    return rawData
      .filter(
        (
          item
        ): item is Required<DashboardGraphicDto> & {
          date: string
          totalOpenAuctions: number
          totalNewAuctions: number
        } =>
          typeof item.date === 'string' &&
          typeof item.totalOpenAuctions === 'number' &&
          typeof item.totalNewAuctions === 'number'
      )
      .map(item => ({
        // maintenant TS sait que date est string et les totaux sont bien des nombres
        date: item.date.split('T')[0],
        totalOpenAuctions: item.totalOpenAuctions,
        totalNewAuctions: item.totalNewAuctions * 10,
      }))
  }, [rawData])

  // 3) Gestion du timeRange comme avant
  const [timeRange, setTimeRange] = React.useState<'90d' | '30d' | '7d'>(
    isMobile ? '7d' : '90d'
  )

  const filteredData = React.useMemo(() => {
    const daysMap = { '90d': 90, '30d': 30, '7d': 7 }
    const days = daysMap[timeRange]
    const ref = chartData.length
      ? new Date(chartData[chartData.length - 1].date)
      : new Date()
    const cutoff = new Date(ref)
    cutoff.setDate(cutoff.getDate() - days)
    return chartData.filter(({ date }) => new Date(date) >= cutoff)
  }, [chartData, timeRange])

  // 4) Config du chart
  const chartConfig = {
    totalOpenAuctions: { label: 'Enchères en cours', color: 'var(--primary)' },
    totalNewAuctions: { label: 'Nouvelles enchères', color: 'var(--primary)' },
  } satisfies Record<string, { label: string; color: string }>

  // 5) Render
  if (isLoading) return <AppSkeleton />
  if (error) return <div>Erreur de chargement</div>

  return (
    <Card className="@container/card">
      <CardHeader>
        <CardTitle>Enchères en cours / Nouvelles enchères</CardTitle>
        <CardDescription>
          <span className="hidden @[540px]/card:block">
            {timeRange === '90d'
              ? '3 derniers mois'
              : timeRange === '30d'
                ? '30 derniers jours'
                : '7 derniers jours'}
          </span>
          <span className="@[540px]/card:hidden">
            {timeRange === '90d' ? '3m' : timeRange === '30d' ? '30j' : '7j'}
          </span>
        </CardDescription>
        <CardAction>
          <ToggleGroup
            type="single"
            value={timeRange}
            onValueChange={(value: string) =>
              setTimeRange(value as '90d' | '30d' | '7d')
            }
            variant="outline"
            className="hidden *:data-[slot=toggle-group-item]:!px-4 @[767px]/card:flex"
          >
            <ToggleGroupItem value="90d">3m</ToggleGroupItem>
            <ToggleGroupItem value="30d">30j</ToggleGroupItem>
            <ToggleGroupItem value="7d">7j</ToggleGroupItem>
          </ToggleGroup>
          <Select
            value={timeRange}
            onValueChange={(value: string) =>
              setTimeRange(value as '90d' | '30d' | '7d')
            }
          >
            <SelectTrigger
              className="flex w-40 **:data-[slot=select-value]:block **:data-[slot=select-value]:truncate @[767px]/card:hidden"
              size="sm"
              aria-label="Select a value"
            >
              <SelectValue placeholder="Sélectionner" />
            </SelectTrigger>
            <SelectContent className="rounded-xl">
              <SelectItem value="90d" className="rounded-lg">
                3 derniers mois
              </SelectItem>
              <SelectItem value="30d" className="rounded-lg">
                30 derniers jours
              </SelectItem>
              <SelectItem value="7d" className="rounded-lg">
                7 derniers jours
              </SelectItem>
            </SelectContent>
          </Select>
        </CardAction>
      </CardHeader>

      <CardContent className="px-2 pt-4 sm:px-6 sm:pt-6">
        <ChartContainer
          config={chartConfig}
          className="aspect-auto h-[250px] w-full"
        >
          <AreaChart data={filteredData}>
            <defs>
              <linearGradient id="fillOpenAuctions" x1="0" y1="0" x2="0" y2="1">
                <stop
                  offset="5%"
                  stopColor="var(--primary)"
                  stopOpacity={0.8}
                />
                <stop
                  offset="95%"
                  stopColor="var(--primary)"
                  stopOpacity={0.1}
                />
              </linearGradient>
              <linearGradient id="fillNewAuctions" x1="0" y1="0" x2="0" y2="1">
                <stop
                  offset="5%"
                  stopColor="var(--primary)"
                  stopOpacity={0.8}
                />
                <stop
                  offset="95%"
                  stopColor="var(--primary)"
                  stopOpacity={0.1}
                />
              </linearGradient>
            </defs>
            <CartesianGrid vertical={false} />
            <XAxis
              dataKey="date"
              tickLine={false}
              axisLine={false}
              tickMargin={8}
              minTickGap={32}
              tickFormatter={value =>
                new Date(value).toLocaleDateString('fr-FR', {
                  month: 'short',
                  day: 'numeric',
                })
              }
            />
            <ChartTooltip
              cursor={false}
              defaultIndex={isMobile ? -1 : undefined}
              content={
                <ChartTooltipContent
                  labelFormatter={value =>
                    new Date(value).toLocaleDateString('fr-FR', {
                      month: 'short',
                      day: 'numeric',
                    })
                  }
                  indicator="dot"
                />
              }
            />
            <Area
              dataKey="totalOpenAuctions"
              type="natural"
              fill="url(#fillOpenAuctions)"
              stroke="var(--primary)"
            />
            <Area
              dataKey="totalNewAuctions"
              type="natural"
              fill="url(#fillNewAuctions)"
              stroke="var(--primary)"
            />
          </AreaChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}
