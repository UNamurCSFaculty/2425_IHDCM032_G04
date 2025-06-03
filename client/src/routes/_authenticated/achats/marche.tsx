import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import AuctionMarketplace from '@/components/auctions/AuctionMarket/AuctionMarket'
import { TradeStatus } from '@/lib/utils'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/achats/marche')({
  component: RouteComponent,
})

export function RouteComponent() {
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
        <AuctionMarketplace userRole="buyer" auctionStatus={TradeStatus.OPEN} />
      </div>
    </>
  )
}
