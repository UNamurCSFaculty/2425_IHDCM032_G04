import { type AuctionDto } from '@/api/generated'
import { listAuctionsOptions } from '@/api/generated/@tanstack/react-query.gen'
import AuctionsTable from '@/components/auctions/AuctionsTable'
import { useAuthUser } from '@/store/userStore'
import { useQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({ query: { traderId: userId } }),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/ventes/historique')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    return queryClient.ensureQueryData(listAuctionsQueryOptions(user!.id))
  },
})

export function RouteComponent() {
  const user = useAuthUser()

  const { data } = useQuery(listAuctionsQueryOptions(user.id))

  const historyAuctions = (data as AuctionDto[]).filter(
    auction => auction.status.name !== 'Ouvert'
  )

  return (
    <AuctionsTable
      tableTitle="Mes ventes passées"
      showColumnBidder={true}
      showColumnBidderPrice={true}
      auctions={historyAuctions}
    />
  )
}
