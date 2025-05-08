import {
  acceptAuctionMutation,
  acceptBidMutation,
  listBidsOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { formatDate } from '@/lib/utils'
import * as Dialog from '@radix-ui/react-dialog'
import { useQuery } from '@tanstack/react-query'
import { useMutation } from '@tanstack/react-query'
import React from 'react'

interface BidsDialogProps {
  auctionId: number
  isOpen: boolean
  setIsOpen: (open: boolean) => void
}

const BidsDialog: React.FC<BidsDialogProps> = ({
  auctionId,
  isOpen,
  setIsOpen,
}) => {
  const {
    data: bidsData,
    isLoading,
    isError,
  } = useQuery({
    ...listBidsOptions({ path: { auctionId: auctionId } }),
    enabled: !!auctionId,
  })

  const { mutate: acceptAuction } = useMutation(acceptAuctionMutation())
  const { mutate: acceptBid } = useMutation(acceptBidMutation())

  const handleAcceptBid = (bidId: number) => {
    acceptBid({ path: { auctionId, bidId } })
    acceptAuction({ path: { id: auctionId } })
    setIsOpen(false)
  }

  return (
    <Dialog.Root open={isOpen} onOpenChange={setIsOpen}>
      <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 backdrop-blur-xs" />
        <Dialog.Content
          aria-describedby={undefined}
          className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white p-6 rounded-lg shadow-lg w-auto max-w-[90%]"
        >
          <Dialog.Title className="text-xl font-bold">
            Offres pour votre enchère
          </Dialog.Title>
          <Dialog.Close asChild>
            <button className="absolute top-2 right-2 text-gray-500 hover:text-gray-700">
              ✕
            </button>
          </Dialog.Close>
          <div className="mt-4">
            {isLoading && <p>Chargement des offres...</p>}
            {isError && (
              <p>Une erreur s'est produite lors du chargement des offres.</p>
            )}
            {!bidsData || bidsData.length == 0 ? (
              <p>Aucune offre trouvée pour cette enchère.</p>
            ) : (
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
                  {bidsData.map(bid => (
                    <TableRow key={bid.id}>
                      <TableCell>{formatDate(bid.creationDate)}</TableCell>
                      <TableCell>
                        {bid.trader.firstName} {bid.trader.lastName}
                      </TableCell>
                      <TableCell>{bid.amount.toLocaleString()} CFA</TableCell>
                      <TableCell>
                        <Button
                          onClick={() => {
                            handleAcceptBid(bid.id)
                          }}
                        >
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
  )
}

export default BidsDialog
