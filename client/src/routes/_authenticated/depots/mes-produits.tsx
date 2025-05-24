import {
  listProductsOptions,
  listProductsQueryKey,
  listQualitiesOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import ProductList from '@/components/deposits/ProductList'
import { useAuthUser } from '@/store/userStore'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

const listProductsQueryOptions = (userId: number) => ({
  ...listProductsOptions({
    query: { traderId: userId },
  }),
  queryKey: listProductsQueryKey(),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/depots/mes-produits')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    await queryClient.ensureQueryData(listProductsQueryOptions(user!.id))
    await queryClient.ensureQueryData(listQualitiesOptions())
  },
})

export function RouteComponent() {
  const user = useAuthUser()

  const { data: productsData } = useSuspenseQuery(
    listProductsQueryOptions(user.id)
  )

  const { data: qualitiesData } = useSuspenseQuery(listQualitiesOptions())

  return (
    <>
      <BreadcrumbSection
        titleKey="app.deposits_list_products.title"
        subtitleKey="app.deposits_list_products.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.deposits' },
          { labelKey: 'breadcrumb.my_deposits' },
        ]}
      />
      <div className="container mx-auto mt-16 mb-16">
        <ProductList products={productsData} qualities={qualitiesData} />
      </div>
    </>
  )
}
