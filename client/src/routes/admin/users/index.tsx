import { Button } from '@/components/ui/button'
import { Link, createFileRoute } from '@tanstack/react-router'
import { PlusCircle } from 'lucide-react'

// Si vous avez un layout spécifique pour /admin/users/*, créez src/routes/admin/users/__root.tsx
// et changez getParentRoute ici. Sinon, il hérite de /admin/__root.tsx.

export const Route = createFileRoute('/admin/users/')({
  component: AdminUsersListComponent,
})

function AdminUsersListComponent() {
  // Logique pour récupérer et afficher la liste des utilisateurs
  const users = [
    { id: '1', name: 'Alice Wonderland', email: 'alice@example.com' },
    { id: '2', name: 'Bob The Builder', email: 'bob@example.com' },
  ]

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Gestion des Utilisateurs</h1>
        <Button asChild>
          <Link to="/admin/users/new">
            <PlusCircle className="mr-2 size-4" /> Ajouter un utilisateur
          </Link>
        </Button>
      </div>
      {/* Ici, une table ou une liste des utilisateurs (ex: utilisant votre DataTable) */}
      <div className="rounded-md border">
        {/* Exemple de table simple */}
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Nom
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Email
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 bg-white">
            {users.map(user => (
              <tr key={user.id}>
                <td className="px-6 py-4 whitespace-nowrap">{user.name}</td>
                <td className="px-6 py-4 whitespace-nowrap">{user.email}</td>
                <td className="px-6 py-4 text-sm font-medium whitespace-nowrap">
                  <Link
                    to="/admin/users/$userId"
                    params={{ userId: user.id }}
                    className="text-indigo-600 hover:text-indigo-900"
                  >
                    Voir/Modifier
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
