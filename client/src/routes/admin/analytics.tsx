import { AnalyticsExportForm } from '@/components/admin/AnalyticsExportForm' // Importez le nouveau composant
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/analytics')({
  component: AnalyticsPage,
})

function AnalyticsPage() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto flex w-full max-w-2xl flex-1 flex-col gap-2 lg:py-2">
      <div className="mb-4">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.analytics.title')}
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">
          {t('admin.analytics.description')}
        </p>
      </div>

      <div className="grid max-w-2xl gap-4">
        <AnalyticsExportForm />
      </div>
    </div>
  )
}
