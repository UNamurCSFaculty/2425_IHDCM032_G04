import { listProductsOptions } from '@/api/generated/@tanstack/react-query.gen'
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

  return <AuctionForm mode="create" products={data} />
}
