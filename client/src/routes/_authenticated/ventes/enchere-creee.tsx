import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { createFileRoute } from '@tanstack/react-router'
import { CheckCircle } from 'lucide-react'

export const Route = createFileRoute('/_authenticated/ventes/enchere-creee')({
  component: RouteComponent,
})

function RouteComponent() {
  return (
    <div className="container mx-auto flex min-h-[calc(100vh-200px)] items-center justify-center px-5 py-24">
      <Card className="w-full max-w-lg shadow-xl">
        <CardHeader className="items-center text-center">
          <div className="flex items-center justify-center">
            <CheckCircle className="mb-4 h-16 w-16 text-green-500" />
          </div>
          <CardTitle className="text-2xl font-bold">Enchère créée !</CardTitle>
          <CardDescription className="text-md pt-2">
            Votre nouvelle vente a été créée avec succès. Vous pouvez maintenant
            la gérer dans votre tableau de bord.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col items-center space-y-6"></CardContent>
      </Card>
    </div>
  )
}
