import { ChartAreaInteractive } from '@/components/admin/chart-area-interactive'
import { DataTable } from '@/components/admin/data-table'
import { SectionCards } from '@/components/admin/section-cards'
import { createFileRoute } from '@tanstack/react-router'

// Simuler les données si data.json n'est pas directement accessible ou pour l'exemple
const exampleData = {
  /* ... structure de vos données pour DataTable ... */
  tableData: [
    {
      id: 1,
      header: 'Item 1',
      type: 'Type A',
      status: 'Done',
      target: '100',
      limit: '200',
      reviewer: 'Admin',
    },
    {
      id: 2,
      header: 'Item 2',
      type: 'Type B',
      status: 'Pending',
      target: '150',
      limit: '250',
      reviewer: 'UserX',
    },
  ],
  // ... autres données pour SectionCards, ChartAreaInteractive
}

export const Route = createFileRoute('/admin/')({
  component: AdminDashboardComponent,
})

function AdminDashboardComponent() {
  return (
    <div className="flex flex-1 flex-col gap-4 @container/main md:gap-6">
      {/* SectionCards nécessitera des données ou une logique appropriée */}
      <SectionCards />
      <div className="px-0 lg:px-0">
        {' '}
        {/* Ajustez le padding si nécessaire */}
        <ChartAreaInteractive />
      </div>
      {/* Assurez-vous que DataTable est correctement initialisé avec des données et colonnes */}
      <DataTable data={exampleData.tableData} />
    </div>
  )
}
