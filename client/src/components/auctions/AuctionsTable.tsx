import { ContractModal } from '../ContractModal'
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
import { formatDate, formatPrice, formatWeight } from '@/utils/formatter'
import { MapPin } from 'lucide-react'
import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'

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
  const [isOpen, setIsOpen] = useState(false)
  const { t } = useTranslation()

  return (
    <div className="bg-muted flex flex-col p-6 md:p-10">
      <Card className="overflow-hidden">
        <CardContent>
          <h2 className="mb-4 text-2xl font-bold">{tableTitle}</h2>
          {auctions === null || auctions.length == 0 ? (
            <p>{t('auction.table.no_auctions')}</p>
          ) : (
            <>
              <p className="mb-4">
                {t('auction.table.transactions_list_message')}
              </p>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>
                      {t('auction.table.start_date_header')}
                    </TableHead>
                    <TableHead>{t('auction.table.end_date_header')}</TableHead>
                    <TableHead>{t('auction.table.status_header')}</TableHead>
                    <TableHead>{t('product.merchandise_label')}</TableHead>
                    <TableHead>{t('product.lot_number_label')}</TableHead>
                    <TableHead>{t('product.quantity_label')}</TableHead>
                    <TableHead>{t('product.quality_label')}</TableHead>
                    <TableHead>{t('form.location')}</TableHead>
                    <TableHead>{t('product.deposit_date_label')}</TableHead>
                    <TableHead>{t('auction.asking_price')}</TableHead>
                    {showColumnBidderPrice && (
                      <TableHead>
                        {t('auction.table.obtained_price_header')}
                      </TableHead>
                    )}
                    {showColumnBidder && (
                      <TableHead>{t('auction.table.buyer_header')}</TableHead>
                    )}
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
                          {t('database.' + auction.product.type)}
                        </TableCell>
                        <TableCell>{auction.product.id}</TableCell>
                        <TableCell>
                          {formatWeight(auction.productQuantity)}
                        </TableCell>
                        <TableCell>
                          {auction.product.qualityControl?.quality.name ??
                            'N/A'}
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
                          {formatPrice.format(auction.price)}
                        </TableCell>
                        {showColumnBidderPrice && (
                          <TableCell>
                            {acceptedBid
                              ? formatPrice.format(acceptedBid.amount)
                              : 'N/A'}
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
                              {t('auction.table.view_bids_button')}
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
                              {t('buttons.delete')}
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
                              {t('auction.table.make_bid_button')}
                            </Button>
                          </TableCell>
                        )}
                        {acceptedBid && (
                          <TableCell>
                            <Button
                              onClick={() => {
                                setIsOpen(true)
                              }}
                            >
                              {t('auction.table.propose_contract_button')}
                            </Button>

                            <ContractModal
                              acceptedBid={acceptedBid}
                              auction={auction}
                              isOpen={isOpen}
                              onClose={() => setIsOpen(false)}
                              onSubmit={() => {
                                setIsOpen(false)
                              }}
                            />
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
