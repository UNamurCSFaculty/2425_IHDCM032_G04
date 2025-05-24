import type { HarvestProductDto } from '@/api/generated'
import {
  listFieldsOptions,
  listProductsOptions,
  listQualitiesOptions,
  listStoresOptions,
  listUsersOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { ProductForm } from '@/components/deposits/ProductForm'
import { ProductType } from '@/lib/utils'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/depots/nouveau-produit')({
  component: RouteComponent,
})

function RouteComponent() {
  const staleTime = 10_000

  const { data: storesData } = useSuspenseQuery({
    ...listStoresOptions(),
    staleTime: staleTime,
  })

  const { data: qualitiesData } = useSuspenseQuery({
    ...listQualitiesOptions(),
    staleTime: staleTime,
  })

  const { data: fieldsData } = useSuspenseQuery({
    ...listFieldsOptions(),
    staleTime: staleTime,
  })

  // TODO this is too huge a query
  const { data: productsData } = useSuspenseQuery({
    ...listProductsOptions(),
    staleTime: staleTime,
  })

  const harvestProductsData = productsData.filter(
    product => product.type === ProductType.HARVEST
  ) as HarvestProductDto[]

  const { data: allUsersData } = useSuspenseQuery({
    ...listUsersOptions(),
    staleTime: staleTime,
  })

  const usersData = allUsersData.filter(
    user =>
      user.type === 'transformer' ||
      user.type === 'producer' ||
      user.type === 'quality_inspector'
  )

  return (
    <>
      <BreadcrumbSection
        titleKey="app.deposits_new_product.title"
        subtitleKey="app.deposits_new_product.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.deposits' },
          { labelKey: 'breadcrumb.new_deposit' },
        ]}
        className="border-b border-gray-200 dark:border-gray-700"
      />
      <ProductForm
        users={usersData}
        stores={storesData}
        qualities={qualitiesData}
        fields={fieldsData}
        harvestProducts={harvestProductsData}
      />
    </>
  )
}
