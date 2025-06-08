import { AdminAppSidebar } from '@/components/admin/AdminAppSidebar'
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from '@/components/ui/sidebar'
import { Outlet, createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin')({
  beforeLoad: async ({ context }) => {
    if (!context.user || (context.user && context.user.type !== 'admin')) {
      throw redirect({
        to: '/403',
      })
    }
  },
  component: AdminLayoutComponent,
})

function AdminLayoutComponent() {
  return (
    <SidebarProvider>
      <AdminAppSidebar />
      <SidebarInset>
        <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12">
          <div className="flex items-center gap-2 px-4">
            <SidebarTrigger className="-ml-1" />
            <div className="bg-sidebar-border h-4 w-px" />
            <h1 className="text-lg font-semibold">Administration</h1>
          </div>
        </header>
        <div className="flex flex-1 flex-col">
          <div className="@container/main flex flex-1 flex-col gap-2">
            <div className="flex flex-col gap-4 py-4 md:gap-6 md:py-6">
              <main className="flex-1 flex-col gap-4 p-4 sm:px-6 sm:py-0 md:gap-8">
                <Outlet />
              </main>
            </div>
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
