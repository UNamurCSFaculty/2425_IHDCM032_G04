import {
  // deleteAuctionMutation,
  listAuctionsOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
// import AuctionsTable from '@/components/auctions/AuctionsTable'
// import BidsDialog from '@/components/auctions/BidsDialog'
import { SellerAuctions } from '@/components/auctions/SellerAuctions'
// import { useAuctionStore } from '@/store/auctionStore'
import { useAuthUser } from '@/store/userStore'
import {
  // useMutation,
  // useQueryClient,
  useSuspenseQuery,
} from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'

// import { useState } from 'react'

const listAuctionsQueryOptions = (userId: number) => ({
  ...listAuctionsOptions({ query: { traderId: userId } }),
})

export const Route = createFileRoute('/_authenticated/ventes/mes-encheres')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    return queryClient.ensureQueryData(listAuctionsQueryOptions(user!.id))
  },
})

export function RouteComponent() {
  const user = useAuthUser()

  const { data: auctionsData } = useSuspenseQuery(
    listAuctionsQueryOptions(user.id)
  )

  return (
    <>
      <BreadcrumbSection
        titleKey="app.auctions_sales.title"
        subtitleKey="app.auctions_sales.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.vendre' },
          { labelKey: 'breadcrumb.my_auctions' },
        ]}
        className="border-b border-gray-200 dark:border-gray-700"
      />
      <div className="container mx-auto px-4 py-8">
        <SellerAuctions auctions={auctionsData} />
      </div>
    </>
  )
}

/*
export function RouteComponent() {
  const { selectedAuctionId, setSelectedAuctionId } = useAuctionStore()

  const [isDialogOpen, setIsDialogOpen] = useState(false)

  const user = useAuthUser()

  const queryClient = useQueryClient()

  const { data: auctionsData } = useSuspenseQuery(
    listAuctionsQueryOptions(user!.id!)
  )

  const deleteAuction = useMutation({
    ...deleteAuctionMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries()
    },
    onError: error => {
      console.error('Erreur :', error)
    },
  })

  const handleDeleteAuction = (auctionId: number) => {
    deleteAuction.mutate({ path: { id: auctionId } })
  }

  const handleViewBids = (auctionId: number) => {
    setSelectedAuctionId(auctionId)
    setIsDialogOpen(true)
  }

  const isDialogOpenChanged = (isOpen: boolean) => {
    setIsDialogOpen(isOpen)
    if (!isOpen) {
      queryClient.invalidateQueries()
    }
  }

  return (
    <>
      <AuctionsTable
        tableTitle="Mes ventes en cours"
        showColumnViewBids={true}
        showColumnDeleteAuction={true}
        handleViewBids={handleViewBids}
        handleDeleteAuction={handleDeleteAuction}
        auctions={auctionsData}
      />
      <BidsDialog
        auctionId={selectedAuctionId!}
        isOpen={isDialogOpen}
        openChange={isDialogOpenChanged}
        showColumnAcceptBid={true}
      />
    </>
  )
}
*/
