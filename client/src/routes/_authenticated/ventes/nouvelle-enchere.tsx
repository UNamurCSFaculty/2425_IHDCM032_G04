import { createFileRoute } from '@tanstack/react-router'
import { AuctionForm } from "@/components/auctions/AuctionForm";
import { useSuspenseQuery } from '@tanstack/react-query'
import { listProductsOptions } from '@/api/generated/@tanstack/react-query.gen'
import type { ProductDtoReadable } from '@/api/generated'
import { useUserStore } from '@/store/userStore'

export const Route = createFileRoute('/_authenticated/ventes/nouvelle-enchere')({
  component: RouteComponent
})

function RouteComponent() {
  const { user } = useUserStore()

  const { data } = useSuspenseQuery({
    ...listProductsOptions({ query: { traderId: user!.id! } }),
    staleTime: 10_000
  })

  const productsArray = data as ProductDtoReadable[]

  return <AuctionForm mode="create" products={productsArray} />
}
