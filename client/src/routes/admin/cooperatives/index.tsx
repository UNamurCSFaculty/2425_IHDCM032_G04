import { Button } from '@/components/ui/button'
import { Link, createFileRoute } from '@tanstack/react-router'
import { PlusCircle } from 'lucide-react'

export const Route = createFileRoute('/admin/cooperatives/')({
  component: () => (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-semibold">Gestion des Coopératives</h1>
        <Button asChild>
          <Link to="/admin/cooperatives/new">
            <PlusCircle className="mr-2 size-4" /> Créer une coopérative
          </Link>
        </Button>
      </div>
      <p>Liste des coopératives ici...</p>
      {/* Exemple: <Link to="/admin/cooperatives/$coopId" params={{ coopId: '123' }}>Voir Coop Alpha</Link> */}
    </div>
  ),
})
