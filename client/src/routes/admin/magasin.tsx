import { StoreManagement } from '@/components/admin/store-management/StoreManagement'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/magasin')({
  component: StoreManagementPage,
})

export function StoreManagementPage() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto flex flex-1 flex-col gap-2 lg:py-2">
      <div className="mb-4">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.store_management.title', 'Gestion des Magasins')}
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">
          {t(
            'admin.store_management.description',
            'Ajoutez, modifiez ou supprimez les magasins de la plateforme.'
          )}
        </p>
      </div>
      <StoreManagement />
    </div>
  )
}
