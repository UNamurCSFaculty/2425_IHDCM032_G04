import { createFileRoute } from '@tanstack/react-router'
import { useQuery } from '@tanstack/react-query'
import { listAuctionsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { type AuctionDtoReadable } from '@/api/generated'
import { useUserStore } from '@/store/userStore'
import AuctionsTable from '@/components/auctions/AuctionsTable'

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({ query: { buyerId: userId, status: "Accepté" } }),
  staleTime: 10_000,
});

export const Route = createFileRoute('/_authenticated/achats/historique')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    return queryClient.ensureQueryData(listAuctionsQueryOptions(user!.id));
  },
});

export function RouteComponent() {
  const { user } = useUserStore();

  const { data } = useQuery(listAuctionsQueryOptions(user!.id));

  const historyAuctions = data as AuctionDtoReadable[];

  return <AuctionsTable 
            tableTitle="Mes achats passés" 
            showColumnBidder={true}
            showColumnBidderPrice={true}
            auctions={historyAuctions} 
          />
}
