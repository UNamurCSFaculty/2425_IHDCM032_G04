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
    <div className="@container/main flex flex-1 flex-col gap-4 md:gap-6">
      <SectionCards />
      <div className="px-0 lg:px-0">
        {' '}
        <ChartAreaInteractive />
      </div>
      <DataTable data={exampleData.tableData} />
    </div>
  )
}
