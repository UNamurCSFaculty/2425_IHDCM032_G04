import type { UserDetailDtoReadable } from '@/api/generated'
import { Footer } from '@/components/Footer'
import { FooterCta } from '@/components/FooterCta'
import { Header } from '@/components/Header'
import { ContentSkeleton } from '@/components/Skeleton/ContentSkeleton'
import type { QueryClient } from '@tanstack/react-query'
import { Outlet, createRootRouteWithContext } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'

export interface MyRouterContext {
  user?: UserDetailDtoReadable
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
