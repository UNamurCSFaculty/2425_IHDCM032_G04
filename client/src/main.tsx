import { StrictMode, useEffect } from 'react'
import ReactDOM from 'react-dom/client'
import { Observer } from 'tailwindcss-intersect'
import { RouterProvider, createRouter } from '@tanstack/react-router'
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

// initialisation i18n
import './i18n'
import '@/utils/zod-config.ts'

import { routeTree } from './routeTree.gen'

import './styles.css'
import reportWebVitals from './reportWebVitals.ts'
import NotFound from './components/NotFound.tsx'
import { ErrorBoundary } from './components/ErrorBoundary.tsx'

import { useUserStore } from './store/userStore.tsx'
import { GlobalSkeleton } from './components/GlobalSkeleton.tsx'

import { client } from '@/api/generated/client.gen.ts'
import { getCurrentUserOptions } from './api/generated/@tanstack/react-query.gen.ts'

client.setConfig({
  credentials: 'include', // pour les cookies (HTTP Only)
})

const queryClient = new QueryClient()

const router = createRouter({
  routeTree,
  context: {
    user: undefined!,
    queryClient,
  },
  defaultPreload: 'intent',
  scrollRestoration: true,
  defaultStructuralSharing: true,
  defaultPreloadStaleTime: 0,
  defaultNotFoundComponent: NotFound,
  defaultPendingComponent: GlobalSkeleton,
})

function AppWithProvider() {
  const user = useUserStore(s => s.user)
  const setUser = useUserStore(s => s.setUser)

  const { data, isLoading } = useQuery({
    ...getCurrentUserOptions(),
    retry: false, // pas de retry pour current user
    staleTime: Infinity, // jamais stale tant qu’on est connecté
  })

  useEffect(() => {
    if (data) {
      setUser(data)
    }
  }, [data, setUser])

  if (isLoading) {
    return <GlobalSkeleton />
  }

  return (
    <ErrorBoundary>
      <RouterProvider router={router} context={{ user }} />
    </ErrorBoundary>
  )
}

const root = ReactDOM.createRoot(document.getElementById('app')!)
root.render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <AppWithProvider />
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </StrictMode>
)

Observer.start()
router.subscribe('onRendered', () => Observer.restart())
reportWebVitals()
