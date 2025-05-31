import { ChartAreaInteractive } from '@/components/admin/chart-area-interactive'
import { SectionCards } from '@/components/admin/section-cards'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/')({
  component: AdminDashboardComponent,
})

function AdminDashboardComponent() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto flex flex-1 flex-col gap-2 lg:py-2">
      <div className="mb-4">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.dashboard.title')}
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">
          {t('admin.dashboard.description')}
        </p>
      </div>
      <div className="px-0 lg:px-0">
        <ChartAreaInteractive />
      </div>
      <SectionCards />
    </div>
  )
}
