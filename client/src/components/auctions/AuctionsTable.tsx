import React from 'react';
import { Button } from '@/components/ui/button'
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table'
import { formatDate } from '@/lib/utils';
import { MapPin } from 'lucide-react'
import { type AuctionDtoReadable, type BidDtoReadable, type HarvestProductDtoReadable, type TransformedProductDtoReadable } from '@/api/generated'

interface AuctionsTableProps {
  auctions: AuctionDtoReadable[];
  bids: Map<number, BidDtoReadable | null>;
}

const AuctionsTable: React.FC<AuctionsTableProps> = ({ auctions }) => {

  if (auctions === null || auctions.length == 0) {
    return <p>Aucune vente aux enchères n'est actuellement ouverte.</p>
  }
  
  return (
      <>
        <p className="mb-4">La liste de vos ventes aux enchères peut être consultée ci-dessous.</p>
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
            </TableRow>
          </TableHeader>
          <TableBody>
            { auctions.map((auction) => (
              <TableRow key={auction.id}>
                <TableCell>{formatDate(auction.creationDate)}</TableCell>
                <TableCell>{formatDate(auction.expirationDate)}</TableCell>
                <TableCell>{auction.product.type === "harvest" ? "Récolte" : "Transformé"}</TableCell>
                <TableCell>{auction.product.id}</TableCell>
                <TableCell>{auction.productQuantity} kg</TableCell>
                <TableCell>{auction.product.qualityControlId ?? "N/A"}</TableCell>
                <TableCell>
                  <div className="flex items-center">
                  <MapPin className="size-5 shrink-0" />{ auction.product.type === "harvest" 
                    ? (auction.product as HarvestProductDtoReadable).store.name 
                    : (auction.product as TransformedProductDtoReadable).location
                  }
                  </div>
                </TableCell>
                <TableCell>{formatDate(auction.product.deliveryDate)}</TableCell>
                <TableCell>{auction.price.toLocaleString()} CFA</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </>
      )
};

export default AuctionsTable;
