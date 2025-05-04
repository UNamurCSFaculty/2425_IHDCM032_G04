import { Outlet, createRootRouteWithContext } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'
import { Header } from '@/components/Header'
import { Footer } from '@/components/Footer'
import { FooterCta } from '@/components/FooterCta'
import type { QueryClient } from '@tanstack/react-query'
import type { UserDetailDtoReadable } from '@/api/generated'
import { ContentSkeleton } from '@/components/Skeleton/ContentSkeleton'

export interface MyRouterContext {
  user: UserDetailDtoReadable | null
  queryClient: QueryClient
}

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: () => (
    <>
      <Header />
      <Outlet />

      <FooterCta />
      <Footer />
      <TanStackRouterDevtools />
    </>
  ),
  errorComponent: () => <div>Erreur de chargement</div>,
  pendingComponent: () => <ContentSkeleton />,
})
