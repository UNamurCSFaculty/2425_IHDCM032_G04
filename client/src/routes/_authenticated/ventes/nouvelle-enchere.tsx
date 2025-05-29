import { AuctionForm } from '@/components/auctions/AuctionForm'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/ventes/nouvelle-enchere')(
  {
    component: RouteComponent,
  }
)

function RouteComponent() {
  return <AuctionForm />
}
