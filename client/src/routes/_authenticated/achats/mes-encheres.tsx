import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import AuctionMarketplace from '@/components/auctions/AuctionMarket/AuctionMarket'
import { useAuthUser } from '@/store/userStore'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/achats/mes-encheres')({
  component: RouteComponent,
})

export function RouteComponent() {
  const user = useAuthUser()

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
          userRole="buyer"
          buyerId={user.id}
          filterByAuctionStatus={true}
        />
      </div>
    </>
  )
}
