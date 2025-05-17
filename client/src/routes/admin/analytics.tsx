import { Button } from '@/components/ui/button'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/analytics')({
  component: () => (
    <div>
      <h1 className="text-2xl font-semibold mb-4">Zone d'Export BI</h1>
      <p>Options d'exportation et actions ici...</p>
      <Button>Exporter les Données Utilisateurs (CSV)</Button>
    </div>
  ),
})
