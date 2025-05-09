import type { AuctionDto } from '@/api/generated'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { Separator } from '@/components/ui/separator'
import dayjs from '@/utils/dayjs-config'
import { formatPrice } from '@/utils/formatter'
import { CheckCircle, XCircle } from 'lucide-react'
import React from 'react'

interface AuctionDetailsProps {
  auction: AuctionDto
  onBidAction: (
    auctionId: number,
    bidId: number,
    action: 'accept' | 'reject'
  ) => void
}

export const AuctionDetails: React.FC<AuctionDetailsProps> = ({
  auction,
  onBidAction,
}) => {
  const sortedBids = [...auction.bids].sort((a, b) => b.amount - a.amount)

  return (
    <div className="space-y-6 max-w-lg mx-auto">
      <Card className="overflow-hidden animate-in fade-in duration-300 gap-2 bg-white shadow-lg">
        <CardHeader>
          <CardTitle>
            <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
              <span className="bg-background text-muted-foreground relative z-10 px-2">
                <span className="text-gray-900 px-2 text-xl">
                  {sortedBids.length}{' '}
                  {sortedBids.length === 1 ? 'offre' : 'offres'} reçue
                  {sortedBids.length > 1 ? 's' : ''}
                </span>
              </span>
            </div>
          </CardTitle>
        </CardHeader>

        <CardContent className="max-h-80 overflow-y-auto space-y-2 bg-neutral-100 pt-2 pb-2">
          {sortedBids.length === 0 ? (
            <div className="text-center py-8 text-gray-500 text-xl">
              Aucune offre n'a encore été effectuée.
            </div>
          ) : (
            sortedBids.map(bid => (
              <div
                key={bid.id}
                className="flex items-start justify-between p-3 rounded-lg mb-1  bg-green-50 shadow-sm"
              >
                <div className="flex-1">
                  <div className="font-medium text-sm">
                    {bid.trader.firstName} {bid.trader.lastName}
                  </div>
                  <div className="text-md text-gray-600">
                    {dayjs(bid.creationDate).fromNow()}
                  </div>
                </div>
                <div className="flex flex-col items-end">
                  <div className="font-semibold text-lg text-green-700">
                    {formatPrice.format(bid.amount)}
                  </div>
                  {bid.status.name === 'Pending' && (
                    <div className="flex space-x-1 mt-1">
                      {/* Accept Popover */}
                      <Popover>
                        <PopoverTrigger asChild>
                          <Button
                            size="sm"
                            variant="outline"
                            className="flex items-center px-2 py-1 bg-green-700 text-white border-green-200"
                          >
                            <CheckCircle className="h-3 w-3 mr-1" /> Acceptée
                          </Button>
                        </PopoverTrigger>
                        <PopoverContent className="w-50 p-2">
                          <p className="text-sm mb-4 text-center font-semibold">
                            Confirmer l'acceptation ?
                          </p>
                          <div className="flex justify-end space-x-2">
                            <Button size="sm" variant="ghost">
                              Annuler
                            </Button>
                            <Button
                              size="sm"
                              variant="default"
                              onClick={() =>
                                onBidAction(auction.id, bid.id, 'accept')
                              }
                            >
                              Confirmer
                            </Button>
                          </div>
                        </PopoverContent>
                      </Popover>

                      {/* Reject Popover */}
                      <Popover>
                        <PopoverTrigger asChild>
                          <Button
                            size="sm"
                            variant="outline"
                            className="flex items-center px-2 py-1 bg-red-600 text-white border-red-200"
                          >
                            <XCircle className="h-3 w-3 mr-1" /> Refusée
                          </Button>
                        </PopoverTrigger>
                        <PopoverContent className="w-50 p-2">
                          <p className="text-sm mb-4 text-center font-semibold">
                            Confirmer le refus ?
                          </p>
                          <div className="flex justify-end space-x-2">
                            <Button size="sm" variant="ghost">
                              Annuler
                            </Button>
                            <Button
                              size="sm"
                              variant="destructive"
                              onClick={() =>
                                onBidAction(auction.id, bid.id, 'reject')
                              }
                            >
                              Confirmer
                            </Button>
                          </div>
                        </PopoverContent>
                      </Popover>
                    </div>
                  )}
                  {bid.status.name !== 'Pending' && (
                    <Badge
                      variant="outline"
                      className={
                        bid.status.name === 'Accepted'
                          ? 'bg-green-50 text-green-700 border-green-200'
                          : 'bg-red-50 text-red-700 border-red-200'
                      }
                    >
                      {bid.status.name === 'Accepted' ? (
                        <>
                          <CheckCircle className="h-3 w-3 mr-1 inline-block" />
                          Acceptée
                        </>
                      ) : (
                        <>
                          <XCircle className="h-3 w-3 mr-1 inline-block" />
                          Refusée
                        </>
                      )}
                    </Badge>
                  )}
                </div>
              </div>
            ))
          )}
        </CardContent>

        <CardFooter className="flex-col space-y-1 mt-2">
          <Separator />
          <div className="text-xs text-gray-500">
            Offre la plus haute :{' '}
            <span className="font-semibold text-gray-700">
              {sortedBids
                .reduce((max, b) => (b.amount > max ? b.amount : max), 0)
                .toLocaleString('fr-BE')}{' '}
              CFA
            </span>
          </div>
          <div className="text-xs text-gray-500">
            Fin de l'enchère :{' '}
            <span className="font-semibold text-gray-700">
              {new Date(auction.expirationDate).toLocaleDateString('fr-FR')}
            </span>
          </div>
        </CardFooter>
      </Card>
    </div>
  )
}
