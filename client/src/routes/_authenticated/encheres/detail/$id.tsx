import NotFound from '@/components/NotFound'
import { Button } from '@/components/ui/button'
import {
  Link,
  PathParamError,
  createFileRoute,
  notFound,
} from '@tanstack/react-router'
import { z } from 'zod'

const paramsSchema = z.object({
  id: z
    .string()
    .regex(/^\d+$/, { message: 'L’id doit être un nombre entier' })
    .transform(s => Number(s)),
})

export const Route = createFileRoute('/_authenticated/encheres/detail/$id')({
  component: RouteComponent,
  params: {
    parse: paramsSchema.parse, // si invalide, ZodError (PathParamError) ⏤ on ne throw pas notFound() ici
    stringify: ({ id }) => ({ id: String(id) }),
  },
  loader: async ({ params }) => {
    // Simulate a network request
    console.log('Loading data for enchere with id:', params.id)
    // NE PAS FAIRE DE AWAIT DANS LE LOADER !!!
    await new Promise(resolve => setTimeout(resolve, 1000))
    // IF ID NOT FOUND
    if (params.id < 0) {
      throw notFound()
    }
    // Return the data
    return { id: params.id }
  },
  errorComponent: err => {
    if (err.error instanceof PathParamError) {
      return <NotFound />
    }
  },
})

function RouteComponent() {
  const { id } = Route.useParams()
  return (
    <div className="container mx-auto mt-4">
      Enchere avec id {id} | voir l'enchère{id + 1}
      <Button>
        <Link to="/encheres/detail/$id" params={{ id: id + 1 }}>
          Lien
        </Link>
      </Button>
    </div>
  )
}
