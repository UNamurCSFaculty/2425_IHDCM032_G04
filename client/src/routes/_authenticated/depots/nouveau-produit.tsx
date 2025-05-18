import type {
  QualityDto,
  QualityInspectorDetailDto,
  StoreDetailDto,
  TraderDetailDto,
} from '@/api/generated'
import {
  listQualitiesOptions,
  listStoresOptions,
  listUsersOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { ProductForm } from '@/components/deposits/ProductForm'
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

  const { data: usersData } = useSuspenseQuery({
    ...listUsersOptions(),
    staleTime: staleTime,
  })

  const tradersData = usersData.filter(
    user =>
      user.type === 'TransformerListDto' || user.type === 'ProducerListDto'
  )

  const qualityInspectorsData = usersData.filter(
    user => user.type === 'QualityInspectorListDto'
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
        traders={tradersData as TraderDetailDto[]}
        stores={storesData as StoreDetailDto[]}
        qualities={qualitiesData as QualityDto[]}
        qualityInspectors={qualityInspectorsData as QualityInspectorDetailDto[]}
      />
    </>
  )
}
