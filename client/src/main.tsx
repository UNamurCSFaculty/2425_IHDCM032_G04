import { StrictMode, useEffect } from 'react'
import ReactDOM from 'react-dom/client'
import { Observer } from 'tailwindcss-intersect'
import { RouterProvider, createRouter } from '@tanstack/react-router'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

// initialisation i18n
import './i18n'
import '@/utils/zod-config.ts'

// Import the generated route tree
import { routeTree } from './routeTree.gen'

import './styles.css'
import reportWebVitals from './reportWebVitals.ts'
import NotFound from './components/NotFound.tsx'
import { ErrorBoundary } from './components/ErrorBoundary.tsx'

import { useUserStore } from './store/userStore.tsx'
import { GlobalSkeleton } from './components/GlobalSkeleton.tsx'

import { client } from '@/api/generated/client.gen.ts'
import { getCurrentUser } from './api/generated/sdk.gen.ts'
client.setConfig({
  credentials: 'include',
})

// Create React Query client
const queryClient = new QueryClient()

// Create a new router instance
const router = createRouter({
  routeTree,
  context: {
    user: undefined!,
  },
  defaultPreload: 'intent',
  scrollRestoration: true,
  defaultStructuralSharing: true,
  defaultPreloadStaleTime: 0,
  defaultNotFoundComponent: NotFound,
  defaultPendingComponent: GlobalSkeleton,
})

// Register the router instance for type safety
declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

// Render the app
function AppWithProvider() {
  // Appel du Hook à l’intérieur d’un composant React
  const user = useUserStore(state => state.user)
  const setUser = useUserStore(state => state.setUser)
  const loading = useUserStore(state => state.loading)
  const setLoading = useUserStore(state => state.setLoading)

  useEffect(() => {
    async function fetchUser() {
      const { data, error } = await getCurrentUser()
      if (!error && data) {
        setUser(data)
      }

      setLoading(false)
    }

    fetchUser()
  })

  if (loading) {
    return <GlobalSkeleton />
  }

  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} context={{ user }} />
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
    </ErrorBoundary>
  )
}

const rootElement = document.getElementById('app')
if (rootElement && !rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement)
  root.render(
    <StrictMode>
      <AppWithProvider />
    </StrictMode>
  )
}

// Require for
Observer.start()
router.subscribe('onRendered', () => {
  Observer.restart()
})

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()
