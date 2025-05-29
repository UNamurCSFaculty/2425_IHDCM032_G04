import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { ProductForm } from '@/components/deposits/ProductForm'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/depots/nouveau-produit')({
  component: RouteComponent,
})

function RouteComponent() {
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
      <ProductForm />
    </>
  )
}
