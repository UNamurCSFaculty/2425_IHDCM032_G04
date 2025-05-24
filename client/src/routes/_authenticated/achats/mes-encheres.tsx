import { listAuctionsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import AuctionMarketplace from '@/components/auctions/AuctionMarket/AuctionMarket'
import { useAuthUser } from '@/store/userStore'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({
    query: { buyerId: userId },
  }),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/achats/mes-encheres')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    await queryClient.ensureQueryData(listAuctionsQueryOptions(user!.id))
  },
})

export function RouteComponent() {
  const user = useAuthUser()

  const { data: auctionsData } = useSuspenseQuery(
    listAuctionsQueryOptions(user.id)
  )

  return (
    <>
      <BreadcrumbSection
        titleKey="app.auctions_buys.title"
        subtitleKey="app.auctions_buys.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.buy' },
          { labelKey: 'breadcrumb.marketplace' },
        ]}
      />
      <div className="container mx-auto mt-16 mb-16">
        <AuctionMarketplace
          auctions={auctionsData}
          userRole="buyer"
          filterByAuctionStatus={true}
        />
      </div>
    </>
  )
}
