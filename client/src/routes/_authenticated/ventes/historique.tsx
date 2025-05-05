import { createFileRoute } from '@tanstack/react-router'
import { useSuspenseQuery } from '@tanstack/react-query'
import { listAuctionsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { type AuctionDtoReadable, type HarvestProductDtoReadable, type TransformedProductDtoReadable } from '@/api/generated'
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import { useUserStore } from '@/store/userStore'
import { formatDate } from '@/lib/utils'

export const Route = createFileRoute('/_authenticated/ventes/historique')({
  component: RouteComponent,
  loader: async ({ context: { queryClient } }) => {
    const user = useUserStore.getState().user // cannot use hook here...
    if (!user) {
      throw new Error('L\'utilisateur n\'est pas connecté');
    }
    return queryClient.ensureQueryData(listAuctionsQueryOptions(user.id!));
  },
});

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({ query: { traderId: userId } }),
  staleTime: 10_000,
});

export function RouteComponent() {
  const { user } = useUserStore();

  const { data } = useSuspenseQuery(
    listAuctionsQueryOptions(user!.id!),
  );

  const auctionsData = data as AuctionDtoReadable[];

  return (
    <div className="container m-20 mx-auto">
      <h2 className="text-2xl font-bold mb-4">Mes ventes passées</h2>

      {(!auctionsData || auctionsData.filter((auction) => auction.status.name !== "Ouvert").length == 0)
        ? (
            <p>Aucune vente aux enchères trouvée.</p>
          ) 
        : (
          <>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Fin de l'enchère</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Marchandise</TableHead>
                  <TableHead>Quantité</TableHead>
                  <TableHead>Qualité</TableHead>
                  <TableHead>Magasin</TableHead>
                  <TableHead>Date de dépôt</TableHead>
                  <TableHead>Prix demandé</TableHead>
                  <TableHead>Prix obtenu</TableHead>
                  <TableHead>Acheteur</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {(auctionsData)
                  .filter((auction) => auction.status.name !== "Ouvert")
                  .sort((a, b) => b.id! - a.id!)
                  .map((auction) => (
                  <TableRow key={auction.id}>
                    <TableCell>{auction.id}</TableCell>
                    <TableCell>{formatDate(auction.expirationDate)}</TableCell>
                    <TableCell>{auction.status.name}</TableCell>
                    <TableCell>{auction.product.type === "harvest" ? "Récolte" : "Transformé"}</TableCell>
                    <TableCell>{auction.productQuantity} kg</TableCell>
                    <TableCell>{auction.product.qualityControlId ?? "N/A"}</TableCell>
                    <TableCell>
                      { auction.product.type === "harvest" 
                        ? (auction.product as HarvestProductDtoReadable).store.name 
                        : (auction.product as TransformedProductDtoReadable).location
                      }
                    </TableCell>
                    <TableCell>{formatDate(auction.product.deliveryDate)}</TableCell>
                    <TableCell>{auction.price.toLocaleString()} CFA</TableCell>
                    <TableCell>brol</TableCell>
                    <TableCell>brol</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </>
          )
      }
    </div>
  )
}
