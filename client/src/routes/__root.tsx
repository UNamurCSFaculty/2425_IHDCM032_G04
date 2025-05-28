import type { UserDetailDto } from '@/api/generated'
import { Footer } from '@/components/Footer'
import { FooterCta } from '@/components/FooterCta'
import { Header } from '@/components/Header'
import { ContentSkeleton } from '@/components/Skeleton/ContentSkeleton'
import type { QueryClient } from '@tanstack/react-query'
import {
  Outlet,
  createRootRouteWithContext,
  useRouterState,
} from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'

export interface MyRouterContext {
  user?: UserDetailDto
  queryClient: QueryClient
}

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: RootComponent, // Utilisez un composant séparé pour la logique
  errorComponent: () => <div>Erreur de chargement</div>,
  pendingComponent: () => <ContentSkeleton />,
})

function RootComponent() {
  const { location } = useRouterState()
  const isAdminRoute = location.pathname.startsWith('/admin')

  if (isAdminRoute) {
    // Pour les routes admin, ne rend que l'Outlet (qui chargera le layout admin)
    // et les devtools.
    return (
      <>
        <Outlet />
        {import.meta.env.DEV && (
          <TanStackRouterDevtools position="bottom-right" />
        )}
      </>
    )
  }

  // Layout public
  return (
    <>
      <Header />
      <Outlet />
      <FooterCta />
      <Footer />
      {import.meta.env.DEV && (
        <TanStackRouterDevtools position="bottom-right" />
      )}
    </>
  )
}
