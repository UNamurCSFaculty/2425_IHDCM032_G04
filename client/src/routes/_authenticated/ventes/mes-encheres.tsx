import {
  deleteAuctionMutation,
  listAuctionsOptions,
  listAuctionsQueryKey,
} from '@/api/generated/@tanstack/react-query.gen'
import BidsDialog from '@/components/auctions/BidsDialog'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { formatDate } from '@/lib/utils'
import { useAuctionStore } from '@/store/auctionStore'
import { useAuthUser } from '@/store/userStore'
import {
  useMutation,
  useQueryClient,
  useSuspenseQuery,
} from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'
import { MapPin } from 'lucide-react'
import { useState } from 'react'

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({ query: { traderId: userId, status: 'Ouvert' } }),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/ventes/mes-encheres')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    return queryClient.ensureQueryData(listAuctionsQueryOptions(user!.id))
  },
})

export function RouteComponent() {
  const { selectedAuctionId, setSelectedAuctionId } = useAuctionStore()

  const [isDialogOpen, setIsDialogOpen] = useState(false)

  const user = useAuthUser()

  const { data: auctionsData } = useSuspenseQuery(
    listAuctionsQueryOptions(user!.id)
  )

  const queryClient = useQueryClient()

  const deleteAuction = useMutation({
    ...deleteAuctionMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
    },
    onError: error => {
      console.error('Erreur :', error)
    },
  })

  const handleUpdateAuction = () => {
    // TODO doesn't work
    queryClient.refetchQueries({ queryKey: listAuctionsQueryKey() })
    // queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() });
  }

  const handleDeleteAuction = (auctionId: number) => {
    deleteAuction.mutate({ path: { id: auctionId } })
  }

  // const handleEditAuction = (auctionId: number) => {
  //   console.log('Edit auction with ID:', auctionId);
  // };

  return (
    <div className="bg-muted flex flex-col p-6 md:p-10">
      <Card className="overflow-hidden">
        <CardContent>
          <h2 className="text-2xl font-bold mb-4">Mes ventes en cours</h2>

          {!auctionsData || auctionsData.length == 0 ? (
            <p>Aucune vente aux enchères n'est actuellement ouverte.</p>
          ) : (
            <>
              <p className="mb-4">
                La liste de vos ventes aux enchères ouvertes peut être consultée
                ci-dessous.
              </p>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Début de l'enchère</TableHead>
                    <TableHead>Fin de l'enchère</TableHead>
                    <TableHead>Marchandise</TableHead>
                    <TableHead>Num. Lot</TableHead>
                    <TableHead>Quantité</TableHead>
                    <TableHead>Qualité</TableHead>
                    <TableHead>Localisation</TableHead>
                    <TableHead>Date de dépôt</TableHead>
                    <TableHead>Prix demandé</TableHead>
                    <TableHead></TableHead>
                    <TableHead></TableHead>
                    {/* <TableHead></TableHead> */}
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {auctionsData.map(auction => (
                    <TableRow key={auction.id}>
                      <TableCell>{formatDate(auction.creationDate)}</TableCell>
                      <TableCell>
                        {formatDate(auction.expirationDate)}
                      </TableCell>
                      <TableCell>
                        {auction.product.type === 'harvest'
                          ? 'Récolte'
                          : 'Transformé'}
                      </TableCell>
                      <TableCell>{auction.product.id}</TableCell>
                      <TableCell>{auction.productQuantity} kg</TableCell>
                      <TableCell>
                        {auction.product.qualityControlId ?? 'N/A'}
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center">
                          <MapPin className="size-5 shrink-0" />
                          {auction.product.store.name}
                        </div>
                      </TableCell>
                      <TableCell>
                        {formatDate(auction.product.deliveryDate)}
                      </TableCell>
                      <TableCell>
                        {auction.price.toLocaleString()} CFA
                      </TableCell>
                      <TableCell>
                        <Button
                          onClick={() => {
                            setSelectedAuctionId(auction.id)
                            setIsDialogOpen(true)
                          }}
                        >
                          {' '}
                          Voir les offres
                        </Button>
                      </TableCell>
                      {/* <TableCell><Button onClick={() => { handleEditAuction(auction.id); }}>Modifier</Button></TableCell> */}
                      <TableCell>
                        <Button
                          onClick={() => {
                            handleDeleteAuction(auction.id)
                          }}
                        >
                          Supprimer
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              <BidsDialog
                auctionId={selectedAuctionId!}
                isOpen={isDialogOpen}
                setIsOpen={setIsDialogOpen}
                updateAuction={handleUpdateAuction}
              />
            </>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
