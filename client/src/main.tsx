import {
  getApplicationDataOptions,
  getCurrentUserOptions,
} from './api/generated/@tanstack/react-query.gen.ts'
import { ErrorBoundary } from './components/ErrorBoundary.tsx'
import NotFound from './components/NotFound.tsx'
import { AppSkeleton } from './components/Skeleton/AppSkeleton.tsx'
// initialisation i18n
import './i18n'
import reportWebVitals from './reportWebVitals.ts'
import { routeTree } from './routeTree.gen'
import { useUserStore } from './store/userStore.tsx'
import './styles.css'
import { getCookie } from './utils/cookies.ts'
import { setDayjsLocale } from './utils/dayjs-config.ts'
import '/node_modules/react-leaflet-markercluster/dist/styles.min.css'
import { client } from '@/api/generated/client.gen.ts'
import { useAppStore } from '@/store/appStore.tsx'
import '@/utils/zod-config.ts'
import {
  QueryClient,
  QueryClientProvider,
  useQueries,
} from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { RouterProvider, createRouter } from '@tanstack/react-router'
import i18n from 'i18next'
import 'leaflet/dist/leaflet.css'
import { StrictMode, useEffect } from 'react'
import ReactDOM from 'react-dom/client'
import { Observer } from 'tailwindcss-intersect'

// Initialise dayjs sur la langue courante
setDayjsLocale(i18n.language)

// Met à jour dayjs si la langue change dynamiquement
i18n.on('languageChanged', lng => {
  setDayjsLocale(lng)
})

client.setConfig({
  credentials: 'include', // pour les cookies (HTTP Only)
})

client.interceptors.request.use(request => {
  const csrfToken = getCookie('XSRF-TOKEN')
  if (csrfToken) {
    // seules les requêtes non-GET ont besoin du CSRF
    if (!/^GET$/i.test(request.method)) {
      request.headers.set('X-XSRF-TOKEN', csrfToken)
    }
  }
  return request
})

const queryClient = new QueryClient()

const router = createRouter({
  routeTree,
  context: {
    user: undefined,
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
