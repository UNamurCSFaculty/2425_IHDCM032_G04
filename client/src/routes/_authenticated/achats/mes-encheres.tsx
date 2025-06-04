import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import AuctionMarketplace from '@/components/auctions/AuctionMarket/AuctionMarket'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/achats/mes-encheres')({
  component: RouteComponent,
})

export function RouteComponent() {
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
          marketMode="my-purchases"
          filterByAuctionStatus={true}
        />
      </div>
    </>
  )
}
