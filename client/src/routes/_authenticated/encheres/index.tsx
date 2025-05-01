import { createFileRoute } from '@tanstack/react-router'
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from '@/components/ui/table'
import { listAuctionsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { useSuspenseQuery } from '@tanstack/react-query'

const queryOptions = {
  ...listAuctionsOptions(),
  staleTime: 10_000, // 10 secondes avant de recharger les données
}

export const Route = createFileRoute('/_authenticated/encheres/')({
  component: RouteComponent,
  loader: async ({ context: { queryClient } }) => {
    return queryClient.ensureQueryData(queryOptions)
  },
})

export function RouteComponent() {
  const { data } = useSuspenseQuery(queryOptions)

  return (
    <div className="container m-20 mx-auto">
      <h1 className="text-2xl font-bold">Les enchères en cours</h1>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>ID</TableHead>
            <TableHead>Prix</TableHead>
            <TableHead>Quantité</TableHead>
            <TableHead>Expiration</TableHead>
            <TableHead>Actif</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.map(auc => (
            <TableRow key={auc.id}>
              <TableCell>{auc.id}</TableCell>
              <TableCell>{auc.price.toLocaleString()} €</TableCell>
              <TableCell>{auc.productQuantity}</TableCell>
              <TableCell>
                {new Date(auc.expirationDate).toLocaleDateString()}
              </TableCell>
              <TableCell>{auc.active ? '✅' : '❌'}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
