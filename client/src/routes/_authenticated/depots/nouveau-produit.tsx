import { ProductForm } from '@/components/deposits/ProductForm'
import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/depots/nouveau-produit')({
  beforeLoad: async ({ context }) => {
    if (!context.user || context.user.storeAssociated !== true) {
      throw redirect({
        to: '/403',
      })
    }
  },
  component: RouteComponent,
})

function RouteComponent() {
  return <ProductForm />
}
