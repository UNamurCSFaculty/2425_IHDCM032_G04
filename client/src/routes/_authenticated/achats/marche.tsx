import {
  listAuctionsOptions,
  listAuctionsQueryKey,
  listQualitiesOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import AuctionMarketplace from '@/components/auctions/AuctionMarket'
import { ProductType, TradeStatus } from '@/lib/utils'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

const listAuctionsQueryOptions = () => ({
  ...listAuctionsOptions({ query: { status: TradeStatus.OPEN } }),
  queryKey: listAuctionsQueryKey(),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/achats/marche')({
  component: RouteComponent,
  loader: async ({ context: { queryClient } }) => {
    await queryClient.ensureQueryData(listAuctionsQueryOptions())
    await queryClient.ensureQueryData(listQualitiesOptions())
  },
})

export function RouteComponent() {
  const { data: auctionsData } = useSuspenseQuery(listAuctionsQueryOptions())

  const { data: qualitiesData } = useSuspenseQuery(listQualitiesOptions())

  const { t } = useTranslation()

  const productTypesData = [
    { id: 1, label: t('database.' + ProductType.HARVEST) },
    { id: 2, label: t('database.' + ProductType.TRANSFORMED) },
  ]

  return (
    <>
      <BreadcrumbSection
        titleKey="app.marketplace.title"
        subtitleKey="app.marketplace.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.buy', href: '/auctions' },
          { labelKey: 'breadcrumb.marketplace' },
        ]}
      />
      <div className="container mx-auto mt-16 mb-16">
        <AuctionMarketplace
          auctions={auctionsData}
          qualities={qualitiesData}
          productTypes={productTypesData}
          userRole="seller"
        />
      </div>
    </>
  )
}
