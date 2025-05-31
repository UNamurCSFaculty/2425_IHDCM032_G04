import { PendingApprovalsPage } from '@/components/admin/user-management/PendingApprovalsPage'
import { UserListPage } from '@/components/admin/user-management/UserListPage'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/users')({
  component: UserManagementPage,
})

function UserManagementPage() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto flex flex-1 flex-col gap-2 lg:py-2">
      <div className="mb-4">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.user_management.title')}
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">
          {t('admin.user_management.description')}
        </p>
      </div>

      <Tabs defaultValue="all-users" className="space-y-4">
        <TabsList>
          <TabsTrigger value="all-users">
            {t('admin.user_management.tabs.all_users')}
          </TabsTrigger>
          <TabsTrigger value="pending-approvals">
            {t('admin.user_management.tabs.pending_approvals')}
          </TabsTrigger>
        </TabsList>
        <TabsContent value="all-users">
          <UserListPage />
        </TabsContent>
        <TabsContent value="pending-approvals">
          <PendingApprovalsPage />
        </TabsContent>
      </Tabs>
    </div>
  )
}
