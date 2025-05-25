import { Button } from '@/components/ui/button'
import { Link, createFileRoute } from '@tanstack/react-router'
import { PlusCircle } from 'lucide-react'

export const Route = createFileRoute('/admin/blog/')({
  component: () => (
    <div>
      <div className="mb-4 flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Gestion du Blog</h1>
        <Button asChild>
          <Link to="/admin/blog/new">
            <PlusCircle className="mr-2 size-4" /> Nouvel Article
          </Link>
        </Button>
      </div>
      <p>Liste des articles de blog ici...</p>
    </div>
  ),
})
