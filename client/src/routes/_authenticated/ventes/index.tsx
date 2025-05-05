import { createFileRoute } from '@tanstack/react-router'
import {useQueryClient, useSuspenseQuery, useMutation  } from '@tanstack/react-query'
import { listAuctionsOptions, deleteAuctionMutation, listAuctionsQueryKey } from '@/api/generated/@tanstack/react-query.gen'
import type { AuctionDtoReadable, HarvestProductDtoReadable, TransformedProductDtoReadable } from '@/api/generated'
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { useUserStore } from '@/store/userStore'
import { useAuctionStore } from '@/store/auctionStore'
import { useState } from 'react';
import { formatDate } from '@/lib/utils'
import BidsModal from '@/components/BidsModal'

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

  const handleDeleteAuction = (auctionId: number) => {
    deleteAuction.mutate({ path: { id: auctionId } });
  };

  const handleEditAuction = (auctionId: number) => {
    console.log('Edit auction with ID:', auctionId);
  };

  return (
    <div className="container m-20 mx-auto">
      <h2 className="text-2xl font-bold mb-4">Mes ventes en cours</h2>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>ID</TableHead>
            <TableHead>Début de l'enchère</TableHead>
            <TableHead>Fin de l'enchère</TableHead>
            <TableHead>Marchandise</TableHead>
            <TableHead>Quantité</TableHead>
            <TableHead>Qualité</TableHead>
            <TableHead>Magasin</TableHead>
            <TableHead>Date de dépôt</TableHead>
            <TableHead>Prix demandé</TableHead>
            <TableHead></TableHead>
            <TableHead></TableHead>
            <TableHead></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {(auctionsData as AuctionDtoReadable[])
            .filter((auction: AuctionDtoReadable) => auction.status.name === "Ouvert")
            .sort((a, b) => b.id! - a.id!)
            .map((auction: AuctionDtoReadable) => (
            <TableRow key={auction.id}>
              <TableCell>{auction.id}</TableCell>
              <TableCell>{formatDate(auction.creationDate)}</TableCell>
              <TableCell>{formatDate(auction.expirationDate)}</TableCell>
              <TableCell>{auction.product.type === "harvest" ? "Récolte" : "Transformé"}</TableCell>
              <TableCell>{auction.productQuantity} kg</TableCell>
              <TableCell>{auction.product.qualityControlId ?? "N/A"}</TableCell>
              <TableCell>{auction.price.toLocaleString()} CFA</TableCell>
              <TableCell>
                { auction.product.type === "harvest" 
                  ? (auction.product as HarvestProductDtoReadable).store.name 
                  : (auction.product as TransformedProductDtoReadable).location
                }
              </TableCell>
              <TableCell>{formatDate(auction.product.deliveryDate)}</TableCell>
              <TableCell>
                <Button
                  onClick={() => {
                    setSelectedAuctionId(auction.id!);
                    setIsDialogOpen(true);
                  }}
                > Voir les offres
                </Button>
              </TableCell>
              <TableCell><Button onClick={() => { handleEditAuction(auction.id!); }}>Modifier</Button></TableCell>
              <TableCell><Button onClick={() => { handleDeleteAuction(auction.id!); }}>Supprimer</Button></TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <BidsModal
        isOpen={isDialogOpen}
        setIsOpen={setIsDialogOpen}
        auctionId={selectedAuctionId!}
      />
    </div>
  )
}
