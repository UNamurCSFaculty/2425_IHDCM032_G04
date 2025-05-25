import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/settings')({
  component: () => (
    <div>
      <h1 className="mb-4 text-2xl font-semibold">Paramètres Généraux</h1>
      <p>Formulaires de paramètres ici...</p>
    </div>
  ),
})
