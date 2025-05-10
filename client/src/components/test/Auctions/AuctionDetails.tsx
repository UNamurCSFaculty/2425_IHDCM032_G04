import type { AuctionDtoReadable } from '@/api/generated'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import { CheckCircle, XCircle } from 'lucide-react'
import React from 'react'

interface AuctionDetailsProps {
  auction: AuctionDtoReadable
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
    <div className="space-y-6">
      <Card className="overflow-hidden animate-in fade-in duration-300">
        <CardHeader>
          <CardTitle>Gestion des offres</CardTitle>
          <CardDescription>
            {sortedBids.length} {sortedBids.length === 1 ? 'offre' : 'offres'}{' '}
            reçue{sortedBids.length > 1 ? 's' : ''}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {sortedBids.length === 0 ? (
              <div className="text-center py-8 text-gray-500 dark:text-gray-400">
                Aucune offre n'a encore été effectué.
              </div>
            ) : (
              sortedBids.map((bid, index) => (
                <div
                  key={bid.id}
                  className={`p-3 rounded-lg ${
                    index === 0
                      ? 'bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800'
                      : 'bg-gray-50 dark:bg-gray-800 shadow'
                  }`}
                >
                  <div className="flex justify-between items-start mb-2">
                    <div>
                      <div className="font-medium">
                        {bid.trader.firstName} {bid.trader.lastName}
                      </div>
                      <div className="text-xs text-gray-500 dark:text-gray-400">
                        {new Date(bid.auctionDate).toLocaleDateString('fr-FR')}
                      </div>
                    </div>
                    <div
                      className={`font-bold ${index === 0 ? 'text-green-600 dark:text-green-400' : ''}`}
                    >
                      €{bid.amount.toLocaleString('fr-BE')}
                    </div>
                  </div>
                  {bid.status.name === 'Pending' ? (
                    <div className="flex gap-2 mt-2">
                      <Button
                        size="sm"
                        variant="outline"
                        className="flex-1 bg-green-50 hover:bg-green-100 text-green-700 border-green-200 hover:border-green-300 dark:bg-green-900/20 dark:hover:bg-green-900/40 dark:text-green-400 dark:border-green-800"
                        onClick={() =>
                          onBidAction(auction.id, bid.id, 'accept')
                        }
                      >
                        <CheckCircle className="h-4 w-4 mr-1" /> Accepter
                      </Button>
                      <Button
                        size="sm"
                        variant="outline"
                        className="flex-1 bg-red-50 hover:bg-red-100 text-red-700 border-red-200 hover:border-red-300 dark:bg-red-900/20 dark:hover:bg-red-900/40 dark:text-red-400 dark:border-red-800"
                        onClick={() =>
                          onBidAction(auction.id, bid.id, 'reject')
                        }
                      >
                        <XCircle className="h-4 w-4 mr-1" /> Refuser
                      </Button>
                    </div>
                  ) : (
                    <div className="mt-2">
                      <Badge
                        variant="outline"
                        className={
                          bid.status.name === 'Accepted'
                            ? 'bg-green-50 text-green-700 border-green-200 dark:bg-green-900/20 dark:text-green-400 dark:border-green-800'
                            : 'bg-red-50 text-red-700 border-red-200 dark:bg-red-900/20 dark:text-red-400 dark:border-red-800'
                        }
                      >
                        {bid.status.name === 'Accepted' ? (
                          <>
                            <CheckCircle className="h-3 w-3 mr-1" /> Acceptée
                          </>
                        ) : (
                          <>
                            <XCircle className="h-3 w-3 mr-1" /> Refusée
                          </>
                        )}
                      </Badge>
                    </div>
                  )}
                </div>
              ))
            )}
          </div>
        </CardContent>
        <CardFooter className="flex-col">
          <Separator className="mb-4" />
          <div className="text-sm text-gray-500 dark:text-gray-400 mb-2">
            Offre la plus haute :{' '}
            <span className="font-semibold text-gray-700 dark:text-gray-300">
              €
              {sortedBids
                .reduce((max, b) => (b.amount > max ? b.amount : max), 0)
                .toLocaleString('fr-BE')}
            </span>
          </div>
          <div className="text-sm text-gray-500 dark:text-gray-400">
            Fin de l'enchère :{' '}
            <span className="font-semibold text-gray-700 dark:text-gray-300">
              {new Date(auction.expirationDate).toLocaleDateString('fr-FR')}
            </span>
          </div>
        </CardFooter>
      </Card>
    </div>
  )
}
