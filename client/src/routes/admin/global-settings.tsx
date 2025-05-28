import { AdminGlobalSettingsForm } from '@/components/admin/AdminGlobalSettingsForm'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/global-settings')({
  component: AdminGlobalSettingsPage,
})

function AdminGlobalSettingsPage() {
  const { t } = useTranslation()
  return (
    <section className="container mx-auto py-6 lg:py-10">
      <div className="mb-8">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.settings.title')}
        </h1>
        <p className="text-muted-foreground">
          {t('admin.settings.description')}
        </p>
      </div>
      <AdminGlobalSettingsForm />
    </section>
  )
}
