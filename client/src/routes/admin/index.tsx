import { ChartAreaInteractive } from '@/components/admin/chart-area-interactive'
import { SectionCards } from '@/components/admin/section-cards'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/')({
  component: AdminDashboardComponent,
})

function AdminDashboardComponent() {
  return (
    <div className="@container/main flex flex-1 flex-col gap-4 md:gap-6">
      <div className="px-0 lg:px-0">
        {' '}
        <ChartAreaInteractive />
      </div>
      <SectionCards />
    </div>
  )
}
