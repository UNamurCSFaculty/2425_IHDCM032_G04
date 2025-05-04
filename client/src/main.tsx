import { StrictMode, useEffect } from 'react'
import ReactDOM from 'react-dom/client'
import { Observer } from 'tailwindcss-intersect'
import { RouterProvider, createRouter } from '@tanstack/react-router'
import {
  QueryClient,
  QueryClientProvider,
  useQueries,
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
import { AppSkeleton } from './components/Skeleton/AppSkeleton.tsx'
import { useAppStore } from '@/store/appStore.tsx'

import { client } from '@/api/generated/client.gen.ts'
import {
  getCurrentUserOptions,
  getApplicationDataOptions,
} from './api/generated/@tanstack/react-query.gen.ts'

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
  defaultPendingComponent: AppSkeleton,
})

function AppWithProvider() {
  const user = useUserStore(s => s.user)
  const setUser = useUserStore(s => s.setUser)
  const setAppData = useAppStore(s => s.setAppData)

  const [currentUserQuery, appDataQuery] = useQueries({
    queries: [
      {
        ...getCurrentUserOptions(),
        retry: false,
        staleTime: Infinity,
      },
      {
        ...getApplicationDataOptions(),
        staleTime: Infinity,
      },
    ],
  })
  const { data: currentUser, isLoading: isLoadingUser } = currentUserQuery
  const { data: appData, isLoading: isLoadingApp } = appDataQuery

  useEffect(() => {
    if (currentUser) setUser(currentUser)
  }, [currentUser, setUser])

  useEffect(() => {
    if (appData) setAppData(appData)
  }, [appData, setAppData])

  if (isLoadingUser || isLoadingApp) {
    return <AppSkeleton />
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
