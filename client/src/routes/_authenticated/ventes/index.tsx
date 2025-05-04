import { createFileRoute } from '@tanstack/react-router'
import {useQueryClient, useQuery, useSuspenseQuery, useMutation  } from '@tanstack/react-query'
import { listAuctionsOptions, listBidsOptions, deleteAuctionMutation, listAuctionsQueryKey } from '@/api/generated/@tanstack/react-query.gen'
import type { AuctionDtoReadable, BidDtoReadable, HarvestProductDtoReadable, TransformedProductDtoReadable } from '@/api/generated'
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { useUserStore } from '@/store/userStore'
import { useAuctionStore } from '@/store/auctionStore'; 
import { useState } from 'react';
import * as Dialog from '@radix-ui/react-dialog';

const formatDate = (dateString: string | undefined): string => {
  if (!dateString) {
    return "—";
  }

  return new Date(dateString).toLocaleString('fr-FR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({ query: { traderId: userId } }),
  staleTime: 10_000,
});

export const Route = createFileRoute('/_authenticated/ventes/')({
  component: RouteComponent,
  loader: async ({ context: { queryClient } }) => {
    const user = useUserStore.getState().user // cannot use hook here...
    if (!user) {
      throw new Error('L\'utilisateur n\'est pas connecté');
    }
    return queryClient.ensureQueryData(listAuctionsQueryOptions(user.id!))
  },
});

export function RouteComponent() {
  const { selectedAuctionId, setSelectedAuctionId } = useAuctionStore();

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const { user } = useUserStore();

  const { data: auctionsData } = useSuspenseQuery(
    listAuctionsQueryOptions(user!.id!),
  );

  const { data: bidsData, isLoading, isError } = useQuery(
    {
      ...listBidsOptions({ path: { auctionId: selectedAuctionId! } }),
      enabled: !!selectedAuctionId,
    }
  );

  const queryClient = useQueryClient();

  const deleteAuction = useMutation({
    ...deleteAuctionMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() });
    },
    onError: (error) => {
      console.error('Erreur :', error);
    },
  });

  const handleCancelAuction = (auctionId: number) => {
    deleteAuction.mutate({ path: { id: auctionId } });
  };

  return (
    <div className="container m-20 mx-auto">
      <h2 className="text-2xl font-bold mb-4">Mes ventes en cours</h2>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Marchandise</TableHead>
            <TableHead>Quantité</TableHead>
            <TableHead>Qualité</TableHead>
            <TableHead>Prix demandé</TableHead>
            <TableHead>Magasin</TableHead>
            <TableHead>Date de dépôt</TableHead>
            <TableHead>Début de l'enchère</TableHead>
            <TableHead>Fin de l'enchère</TableHead>
            <TableHead></TableHead>
            <TableHead></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {(auctionsData as AuctionDtoReadable[]).map((auction: AuctionDtoReadable) => (
            <TableRow key={auction.id}>
              <TableCell>{auction.product.type == "harvest" ? "Récolte" : "Transformé"}</TableCell>
              <TableCell>{auction.productQuantity} kg</TableCell>
              <TableCell>{auction.product.qualityControlId ?? "N/A"}</TableCell>
              <TableCell>{auction.price.toLocaleString()} CFA</TableCell>
              <TableCell>
                { auction.product.type == "harvest" 
                  ? (auction.product as HarvestProductDtoReadable).store.name 
                  : (auction.product as TransformedProductDtoReadable).location
                }
              </TableCell>
              <TableCell>{formatDate(auction.product.deliveryDate)}</TableCell>
              <TableCell>{formatDate(auction.creationDate)}</TableCell>
              <TableCell>{formatDate(auction.expirationDate)}</TableCell>
              <TableCell>
                <Button
                  onClick={() => {
                    setSelectedAuctionId(auction.id!);
                    setIsDialogOpen(true);
                  }}
                > Voir les offres
                </Button>
              </TableCell>
              <TableCell>
                  <Button
                    onClick={() => {
                      handleCancelAuction(auction.id!);
                    }}
                  >Annuler la vente
                  </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Dialog.Root open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 backdrop-blur-xs" />
        <Dialog.Content aria-describedby={undefined} className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white p-6 rounded-lg shadow-lg w-auto max-w-[90%]">
          <Dialog.Title className="text-xl font-bold">Offres pour votre enchère</Dialog.Title>
          <Dialog.Close asChild>
            <button className="absolute top-2 right-2 text-gray-500 hover:text-gray-700">✕</button>
          </Dialog.Close>
          <div className="mt-4">
            {isLoading && <p>Chargement des offres...</p>}
            {isError && <p>Une erreur s'est produite lors du chargement des offres.</p>}
            {bidsData as BidDtoReadable[] && (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Date</TableHead>
                    <TableHead>Enchérisseur</TableHead>
                    <TableHead>Montant</TableHead>
                    <TableHead></TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {(bidsData as BidDtoReadable[]).map((bid: BidDtoReadable) => (
                    <TableRow key={bid.id}>
                      <TableCell>{formatDate(bid.creationDate)}</TableCell>
                      <TableCell>{bid.trader.firstName} {bid.trader.lastName}</TableCell>
                      <TableCell>{bid.amount.toLocaleString()} CFA</TableCell>
                      <TableCell><Button>Accepter</Button></TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </div>
        </Dialog.Content>
        </Dialog.Portal>
      </Dialog.Root>
    </div>
  )
}
