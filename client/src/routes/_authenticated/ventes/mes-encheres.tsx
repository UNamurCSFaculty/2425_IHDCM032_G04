import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import AuctionMarketplace from '@/components/auctions/AuctionMarket/AuctionMarket'
import { useAuthUser } from '@/store/userStore'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/ventes/mes-encheres')({
  component: RouteComponent,
})

export function RouteComponent() {
  const user = useAuthUser()

  return (
    <>
      <BreadcrumbSection
        titleKey="app.auctions_sales.title"
        subtitleKey="app.auctions_sales.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.buy' },
          { labelKey: 'breadcrumb.marketplace' },
        ]}
      />
      <div className="container mx-auto mt-16 mb-16">
        <AuctionMarketplace
          userRole="seller"
          traderId={user.id}
          filterByAuctionStatus={true}
        />
      </div>
    </>
  )
}
