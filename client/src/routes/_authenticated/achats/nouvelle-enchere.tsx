import {
  listAuctionsOptions,
  listAuctionsQueryKey,
} from '@/api/generated/@tanstack/react-query.gen'
import AuctionsTable from '@/components/auctions/AuctionsTable'
import BidsDialog from '@/components/auctions/BidsDialog'
import { TradeStatus } from '@/lib/utils'
import { useAuctionStore } from '@/store/auctionStore'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'

const listAuctionsQueryOptions = () => ({
  ...listAuctionsOptions({ query: { status: TradeStatus.OPEN } }),
  queryKey: listAuctionsQueryKey(),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/achats/nouvelle-enchere')(
  {
    component: RouteComponent,
    loader: async ({ context: { queryClient } }) => {
      return queryClient.ensureQueryData(listAuctionsQueryOptions())
    },
  }
)

export function RouteComponent() {
  const { selectedAuctionId, setSelectedAuctionId } = useAuctionStore()

  const [isDialogOpen, setIsDialogOpen] = useState(false)

  const { data: auctionsData } = useSuspenseQuery(listAuctionsQueryOptions())

  const handleMakeBid = (auctionId: number) => {
    setSelectedAuctionId(auctionId)
    setIsDialogOpen(true)
  }

  const isDialogOpenChanged = (isOpen: boolean) => {
    setIsDialogOpen(isOpen)
  }

  return (
    <>
      <AuctionsTable
        tableTitle="Acheter un produit"
        showColumnMakeBid={true}
        handleMakeBid={handleMakeBid}
        auctions={auctionsData}
      />
      <BidsDialog
        auctionId={selectedAuctionId!}
        isOpen={isDialogOpen}
        openChange={isDialogOpenChanged}
        showBidForm={true}
      />
    </>
  )
}
