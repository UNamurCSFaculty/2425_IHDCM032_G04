import { listProductsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { AuctionForm } from '@/components/auctions/AuctionForm'
import { useAuthUser } from '@/store/userStore'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/ventes/nouvelle-enchere')(
  {
    component: RouteComponent,
  }
)

function RouteComponent() {
  const user = useAuthUser()

  const { data } = useSuspenseQuery({
    ...listProductsOptions({ query: { traderId: user.id } }),
    staleTime: 10_000,
  })

  return (
    <>
      <BreadcrumbSection
        titleKey="app.auctions_new_sale.title"
        subtitleKey="app.auctions_new_sale.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.vendre' },
          { labelKey: 'breadcrumb.new_auction' },
        ]}
        className="border-b border-gray-200 dark:border-gray-700"
      />
      <div className="container mx-auto px-4 py-8">
        <AuctionForm mode="create" products={data} />
      </div>
    </>
  )
}
