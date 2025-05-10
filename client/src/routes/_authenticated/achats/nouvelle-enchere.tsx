import { createFileRoute } from '@tanstack/react-router'
import { useSuspenseQuery } from '@tanstack/react-query'
import { listAuctionsOptions, listAuctionsQueryKey } from '@/api/generated/@tanstack/react-query.gen'
import { useAuctionStore } from '@/store/auctionStore'
import { useState } from 'react';
import BidsDialog from '@/components/auctions/BidsDialog'
import AuctionsTable from '@/components/auctions/AuctionsTable'

const listAuctionsQueryOptions = () => ({
  ...listAuctionsOptions({ query: { status: "Ouvert" } }),
  queryKey: listAuctionsQueryKey(),
  staleTime: 10_000,
});

export const Route = createFileRoute('/_authenticated/achats/nouvelle-enchere')({
  component: RouteComponent,
  loader: async ({ context: { queryClient } }) => {
    return queryClient.ensureQueryData(listAuctionsQueryOptions())
  },
});

export function RouteComponent() {
  const { selectedAuctionId, setSelectedAuctionId } = useAuctionStore();

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const { data : auctionsData } = useSuspenseQuery(
    listAuctionsQueryOptions(),
  );

  const handleMakeBid = (auctionId: number) => {
    setSelectedAuctionId(auctionId);
    setIsDialogOpen(true);
  };

  return (
          <>
            <AuctionsTable 
              tableTitle="Acheter un produit" 
              showColumnMakeBid={true}
              handleMakeBid={handleMakeBid}
              auctions={auctionsData} 
            />
            <BidsDialog
              auctionId={selectedAuctionId!}
              isOpen={isDialogOpen}
              setIsOpen={setIsDialogOpen}
              showBidForm={true}
            />
          </>
      )
}
