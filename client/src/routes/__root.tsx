import { Outlet, createRootRouteWithContext } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'
import { Header } from '@/components/Header'
import { Footer } from '@/components/Footer'
import { FooterCta } from '@/components/FooterCta'
import type { User } from '@/types/api'

export interface MyRouterContext {
  user: User | null
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
})
