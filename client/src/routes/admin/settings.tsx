import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/settings')({
  component: () => (
    <div>
      <h1 className="text-2xl font-semibold mb-4">Paramètres Généraux</h1>
      <p>Formulaires de paramètres ici...</p>
    </div>
  ),
})
