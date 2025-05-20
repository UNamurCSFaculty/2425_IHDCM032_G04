import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { createFileRoute, useNavigate } from '@tanstack/react-router'
import React from 'react'

export const Route = createFileRoute('/admin/users/new')({
  component: AdminCreateUserComponent,
})

function AdminCreateUserComponent() {
  const navigate = useNavigate()
  // const mutation = useCreateUserMutation(); // Exemple avec React Query

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const formData = new FormData(event.currentTarget)
    const name = formData.get('name') as string
    const email = formData.get('email') as string
    console.log('Creating user:', { name, email })
    // mutation.mutate({ name, email }, { onSuccess: () => navigate({ to: '/admin/users' }) });
    alert('Utilisateur créé (simulation) !')
    navigate({ to: '/admin/users' })
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold">Ajouter un nouvel utilisateur</h1>
      <form onSubmit={handleSubmit} className="max-w-md space-y-4">
        <div>
          <Label htmlFor="name">Nom</Label>
          <Input id="name" name="name" type="text" required />
        </div>
        <div>
          <Label htmlFor="email">Email</Label>
          <Input id="email" name="email" type="email" required />
        </div>
        <Button type="submit">
          {/* {mutation.isPending ? 'Création...' : 'Créer Utilisateur'} */}
          Créer Utilisateur
        </Button>
      </form>
    </div>
  )
}
