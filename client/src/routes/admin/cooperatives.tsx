import { CooperativeListPage } from '@/components/admin/cooperative-management/CooperativeListPage'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/cooperatives')({
  component: CooperativeManagementPage,
})

function CooperativeManagementPage() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto flex flex-1 flex-col gap-2 lg:py-2">
      <div className="mb-4">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.cooperative_management.title')}
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">
          {t('admin.cooperative_management.description')}
        </p>
      </div>
      <CooperativeListPage />
    </div>
  )
}
