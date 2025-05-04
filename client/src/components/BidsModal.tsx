import React from 'react';
import * as Dialog from '@radix-ui/react-dialog';
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/table';
import { Button } from '@/components/ui/button'
import {useQuery } from '@tanstack/react-query'
import { useMutation } from '@tanstack/react-query'
import type { BidDtoReadable } from '@/api/generated';
import { acceptBidMutation, listBidsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { formatDate } from '@/lib/utils';

interface BidsModalProps {
  isOpen: boolean;
  onClose: (open: boolean) => void;
  auctionId: number;
}

const BidsModal: React.FC<BidsModalProps> = ({ isOpen, onClose, auctionId }) => {
  const { data: bidsData, isLoading, isError } = useQuery(
    {
      ...listBidsOptions({ path: { auctionId: auctionId } }),
      enabled: !!auctionId,
    }
  );

  const { mutate: acceptBid } = useMutation(acceptBidMutation());

  const handleAcceptBid = (bidId: number) => {
    acceptBid({
        path: { auctionId, bidId }
      }
    );
  };

  return (
    <Dialog.Root open={isOpen} onOpenChange={onClose}>
      <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 backdrop-blur-xs" />
        <Dialog.Content
          aria-describedby={undefined}
          className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white p-6 rounded-lg shadow-lg w-auto max-w-[90%]"
        >
          <Dialog.Title className="text-xl font-bold">Offres pour votre enchère</Dialog.Title>
          <Dialog.Close asChild>
            <button className="absolute top-2 right-2 text-gray-500 hover:text-gray-700">✕</button>
          </Dialog.Close>
          <div className="mt-4">
            {isLoading && <p>Chargement des offres...</p>}
            {isError && <p>Une erreur s'est produite lors du chargement des offres.</p>}
            {(bidsData as BidDtoReadable[]) && (
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
                      <TableCell>
                        {bid.trader.firstName} {bid.trader.lastName}
                      </TableCell>
                      <TableCell>{bid.amount.toLocaleString()} CFA</TableCell>
                      <TableCell>
                        <Button onClick={() => { handleAcceptBid(bid.id!); }}>
                          Accepter
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </div>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  );
};

export default BidsModal;