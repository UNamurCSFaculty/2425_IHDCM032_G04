import { AuctionCard } from './AuctionCard'
import { AuctionDetails } from './AuctionDetails'
import { AuctionHeader } from './AuctionHeader'
import type { AuctionDto } from '@/api/generated'
import {
  acceptAuctionMutation,
  acceptBidMutation,
  listAuctionsQueryKey,
  rejectBidMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import { Button } from '@/components/ui/button'
import { TradeStatus } from '@/lib/utils'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import React, { useState } from 'react'

const ITEMS_PER_PAGE = 3

interface SellerAuctionsProps {
  auctions: AuctionDto[]
}
export type SellerAuctionsTab = 'active' | 'ended'

export const SellerAuctions: React.FC<SellerAuctionsProps> = ({ auctions }) => {
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [tab, setTab] = useState<SellerAuctionsTab>('active')
  const [page, setPage] = useState(1)

  const filtered = auctions.filter(
    a => (tab === 'active') === (a.status.name === TradeStatus.OPEN)
  )
  const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE)
  const paginated = filtered.slice(
    (page - 1) * ITEMS_PER_PAGE,
    page * ITEMS_PER_PAGE
  )

  const selectedAuction = auctions.find(a => a.id === selectedId)

  const queryClient = useQueryClient()

  const { mutate: acceptAuction } = useMutation(acceptAuctionMutation())
  const { mutate: acceptBid } = useMutation(acceptBidMutation())
  const { mutate: rejectBid } = useMutation(rejectBidMutation())

  const handleBidAction = (
    auctionId: number,
    bidId: number,
    action: 'accept' | 'reject'
  ) => {
    if (action == 'accept') {
      acceptBid({ path: { bidId } })
      acceptAuction({ path: { id: auctionId } })
    } else {
      rejectBid({ path: { bidId } })
    }
    queryClient.invalidateQueries({ queryKey: listAuctionsQueryKey() })
  }

  return (
    <div className="container mx-auto px-4 py-8 space-y-6">
      {/* En-tête */}
      <AuctionHeader
        auctions={auctions}
        selectedAuction={selectedAuction}
        setSelectedId={setSelectedId}
        setPage={setPage}
        tab={tab}
        setTab={setTab}
      />

      {/* Contenu principal */}
      {filtered.length === 0 ? (
        <div className="p-6 border border-dashed rounded-lg text-gray-500 min-h-100 flex justify-center items-center text-lg ">
          Aucune enchère pour l'instant. Revenez bientôt !
        </div>
      ) : selectedAuction ? (
        <div className="grid gap-6 md:grid-cols-3">
          <div className="md:col-span-1">
            <AuctionCard
              auction={selectedAuction}
              isSelected
              onVoirDetails={setSelectedId}
            />
          </div>
          <div className="md:col-span-2">
            <AuctionDetails
              auction={selectedAuction}
              onBidAction={handleBidAction}
            />
          </div>
        </div>
      ) : (
        <>
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3" key={page}>
            {paginated.map(a => (
              <AuctionCard
                key={a.id}
                auction={a}
                onVoirDetails={setSelectedId}
              />
            ))}
          </div>
          {filtered.length > ITEMS_PER_PAGE && (
            <div className="flex justify-center items-center space-x-4">
              <Button
                variant="outline"
                size="sm"
                disabled={page === 1}
                onClick={() => setPage(p => Math.max(1, p - 1))}
              >
                Précédent
              </Button>
              <span className="text-sm">
                Page {page} / {totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                disabled={page === totalPages}
                onClick={() => setPage(p => Math.min(totalPages, p + 1))}
              >
                Suivant
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  )
}
