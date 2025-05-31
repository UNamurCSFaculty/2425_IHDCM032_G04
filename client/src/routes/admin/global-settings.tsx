import { AdminGlobalSettingsForm } from '@/components/admin/AdminGlobalSettingsForm'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/global-settings')({
  component: AdminGlobalSettingsPage,
})

function AdminGlobalSettingsPage() {
  const { t } = useTranslation()
  return (
    <section className="container mx-auto flex flex-1 flex-col gap-2 lg:py-2">
      <div className="mb-4">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.settings.title')}
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">
          {t('admin.settings.description')}
        </p>
      </div>
      <div className="mx-auto w-full max-w-2xl">
        <AdminGlobalSettingsForm />
      </div>
    </section>
  )
}
