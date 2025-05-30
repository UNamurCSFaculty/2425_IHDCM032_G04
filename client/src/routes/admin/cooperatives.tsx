import { CooperativeListPage } from '@/components/admin/cooperative-management/CooperativeListPage'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/cooperatives')({
  component: CooperativeManagementPage,
})

function CooperativeManagementPage() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto py-6 lg:py-10">
      <div className="mb-8">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.cooperative_management.title')}
        </h1>
        <p className="text-muted-foreground">
          {t('admin.cooperative_management.description')}
        </p>
      </div>
      <CooperativeListPage />
    </div>
  )
}
