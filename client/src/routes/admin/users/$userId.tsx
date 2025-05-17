import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Link, createFileRoute, useNavigate } from '@tanstack/react-router'
import React, { useEffect, useState } from 'react'

export const Route = createFileRoute('/admin/users/$userId')({
  loader: async ({ params }) => {
    // Simuler un chargement de données utilisateur
    console.log('Chargement utilisateur avec ID:', params.userId)
    // Remplacez par votre véritable appel API: await fetchUser(params.userId)
    return {
      id: params.userId,
      name: `Utilisateur ${params.userId}`,
      email: `user${params.userId}@example.com`,
    }
  },
  component: AdminEditUserComponent,
})

function AdminEditUserComponent() {
  const navigate = useNavigate()
  const user = Route.useLoaderData() // Accès aux données chargées
  const params = Route.useParams()
  // const updateUserMutation = useUpdateUserMutation();

  const [name, setName] = useState(user.name)
  const [email, setEmail] = useState(user.email)

  useEffect(() => {
    setName(user.name)
    setEmail(user.email)
  }, [user])

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    console.log('Updating user:', params.userId, { name, email })
    // updateUserMutation.mutate({ id: params.userId, name, email }, { onSuccess: () => navigate({ to: '/admin/users' }) });
    alert('Utilisateur mis à jour (simulation) !')
    navigate({ to: '/admin/users' })
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold">
        Modifier l'utilisateur: {user.name}
      </h1>
      <form onSubmit={handleSubmit} className="max-w-md space-y-4">
        <div>
          <Label htmlFor="name">Nom</Label>
          <Input
            id="name"
            name="name"
            type="text"
            value={name}
            onChange={e => setName(e.target.value)}
            required
          />
        </div>
        <div>
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            name="email"
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
        </div>
        <div className="flex gap-2">
          <Button type="submit">
            {/* {updateUserMutation.isPending ? 'Sauvegarde...' : 'Sauvegarder'} */}
            Sauvegarder
          </Button>
          <Button variant="outline" asChild>
            <Link to="/admin/users">Annuler</Link>
          </Button>
        </div>
      </form>
    </div>
  )
}
