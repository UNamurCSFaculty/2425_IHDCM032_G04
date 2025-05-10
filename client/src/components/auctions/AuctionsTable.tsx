import { type AuctionDto } from '@/api/generated'
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
import { MapPin } from 'lucide-react'
import React from 'react'

interface AuctionsTableProps {
  tableTitle: string
  showColumnBidder?: boolean
  showColumnBidderPrice?: boolean
  showColumnViewBids?: boolean
  showColumnDeleteAuction?: boolean
  showColumnMakeBid?: boolean
  handleViewBids?: (auctionId: number) => void
  handleDeleteAuction?: (auctionId: number) => void
  auctions: AuctionDto[]
  handleMakeBid?: (auctionId: number) => void
}

const AuctionsTable: React.FC<AuctionsTableProps> = ({
  tableTitle,
  auctions,
  showColumnBidder,
  showColumnBidderPrice,
  showColumnViewBids,
  showColumnDeleteAuction,
  showColumnMakeBid,
  handleViewBids,
  handleDeleteAuction,
  handleMakeBid,
}) => {
  return (
    <div className="bg-muted flex flex-col p-6 md:p-10">
      <Card className="overflow-hidden">
        <CardContent>
          <h2 className="text-2xl font-bold mb-4">{tableTitle}</h2>
          {auctions === null || auctions.length == 0 ? (
            <p>Aucune vente aux enchères n'est répertoriée.</p>
          ) : (
            <>
              <p className="mb-4">
                La liste des transactions peut être consultée ci-dessous.
              </p>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Début de l'enchère</TableHead>
                    <TableHead>Fin de l'enchère</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Marchandise</TableHead>
                    <TableHead>Num. Lot</TableHead>
                    <TableHead>Quantité</TableHead>
                    <TableHead>Qualité</TableHead>
                    <TableHead>Localisation</TableHead>
                    <TableHead>Date de dépôt</TableHead>
                    <TableHead>Prix demandé</TableHead>
                    {showColumnBidderPrice && (
                      <TableHead>Prix obtenu</TableHead>
                    )}
                    {showColumnBidder && <TableHead>Acheteur</TableHead>}
                    {showColumnViewBids && <TableHead></TableHead>}
                    {showColumnDeleteAuction && <TableHead></TableHead>}
                    {showColumnMakeBid && <TableHead></TableHead>}
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {auctions.map(auction => {
                    const acceptedBid =
                      auction.bids?.find(
                        bid => bid.status!.name === 'Accepté'
                      ) || null

                    return (
                      <TableRow key={auction.id}>
                        <TableCell>
                          {formatDate(auction.creationDate)}
                        </TableCell>
                        <TableCell>
                          {formatDate(auction.expirationDate)}
                        </TableCell>
                        <TableCell>{auction.status!.name}</TableCell>
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
                        {showColumnBidderPrice && (
                          <TableCell>
                            {acceptedBid ? acceptedBid.amount + ' CFA' : 'N/A'}
                          </TableCell>
                        )}
                        {showColumnBidder && (
                          <TableCell>
                            {acceptedBid
                              ? acceptedBid.trader.firstName +
                                ' ' +
                                acceptedBid.trader.lastName
                              : 'N/A'}
                          </TableCell>
                        )}
                        {showColumnViewBids && handleViewBids && (
                          <TableCell>
                            <Button
                              onClick={() => {
                                handleViewBids(auction.id)
                              }}
                            >
                              {' '}
                              Voir les offres
                            </Button>
                          </TableCell>
                        )}
                        {showColumnDeleteAuction && handleDeleteAuction && (
                          <TableCell>
                            <Button
                              onClick={() => {
                                handleDeleteAuction(auction.id)
                              }}
                            >
                              Supprimer
                            </Button>
                          </TableCell>
                        )}
                        {showColumnMakeBid && handleMakeBid && (
                          <TableCell>
                            <Button
                              onClick={() => {
                                handleMakeBid(auction.id)
                              }}
                            >
                              Faire une offre
                            </Button>
                          </TableCell>
                        )}
                      </TableRow>
                    )
                  })}
                </TableBody>
              </Table>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

export default AuctionsTable
