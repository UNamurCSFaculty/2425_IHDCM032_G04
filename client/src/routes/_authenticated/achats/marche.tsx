import {
  listAuctionsOptions,
  listAuctionsQueryKey,
} from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import AuctionMarketplace from '@/components/auctions/AuctionMarket/AuctionMarket'
import { TradeStatus } from '@/lib/utils'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

const listAuctionsQueryOptions = () => ({
  ...listAuctionsOptions({ query: { status: TradeStatus.OPEN } }),
  queryKey: listAuctionsQueryKey(),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/achats/marche')({
  component: RouteComponent,
  loader: async ({ context: { queryClient } }) => {
    await queryClient.ensureQueryData(listAuctionsQueryOptions())
  },
})

export function RouteComponent() {
  const { data: auctionsData } = useSuspenseQuery(listAuctionsQueryOptions())

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
        <AuctionMarketplace auctions={auctionsData} userRole="buyer" />
      </div>
    </>
  )
}
