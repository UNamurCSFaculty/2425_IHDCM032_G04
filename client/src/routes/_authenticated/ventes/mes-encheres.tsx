import { createFileRoute } from '@tanstack/react-router'
import {useQueryClient, useSuspenseQuery, useMutation  } from '@tanstack/react-query'
import { listAuctionsOptions, deleteAuctionMutation } from '@/api/generated/@tanstack/react-query.gen'
import { useAuthUser } from '@/store/userStore'
import { useAuctionStore } from '@/store/auctionStore'
import { useState } from 'react';
import BidsDialog from '@/components/auctions/BidsDialog'
import AuctionsTable from '@/components/auctions/AuctionsTable'

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({ query: { traderId: userId, status: "Ouvert" } })
});

export const Route = createFileRoute('/_authenticated/ventes/mes-encheres')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    return queryClient.ensureQueryData(listAuctionsQueryOptions(user!.id))
  },
});

export function RouteComponent() {
  const { selectedAuctionId, setSelectedAuctionId } = useAuctionStore();

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const user = useAuthUser();

  const queryClient = useQueryClient();

  const { data: auctionsData } = useSuspenseQuery(
    listAuctionsQueryOptions(user!.id!),
  );

  const deleteAuction = useMutation({
    ...deleteAuctionMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries();
    },
    onError: (error) => {
      console.error('Erreur :', error);
    },
  });

  const handleDeleteAuction = (auctionId: number) => {
    deleteAuction.mutate({ path: { id: auctionId } });
  };

  const handleViewBids = (auctionId: number) => {
    setSelectedAuctionId(auctionId);
    setIsDialogOpen(true);
  };

  const isDialogOpenChanged = (isOpen: boolean) => {
    setIsDialogOpen(isOpen);
    if (!isOpen) {
      queryClient.invalidateQueries();
    }
  }

  return (
          <>
            <AuctionsTable 
              tableTitle="Mes ventes en cours" 
              showColumnViewBids={true}
              showColumnDeleteAuction={true}
              handleViewBids={handleViewBids}
              handleDeleteAuction={handleDeleteAuction}
              auctions={auctionsData} 
            />
            <BidsDialog
              auctionId={selectedAuctionId!}
              isOpen={isDialogOpen}
              openChange={isDialogOpenChanged}
              showColumnAcceptBid={true}
            />
          </>
      )
}
