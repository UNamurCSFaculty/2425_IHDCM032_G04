import {
  createFileRoute,
  Link,
  type ErrorComponentProps,
  useLoaderData,
} from '@tanstack/react-router'
import { useQuery } from '@tanstack/react-query'
import { CalendarDays, Tag, ArrowLeft, Eye, User2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { Skeleton } from '@/components/ui/skeleton'
import { Card, CardContent } from '@/components/ui/card'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { getNewsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { useTranslation } from 'react-i18next'
import type { NewsDto } from '@/api/generated'
import { getCategoryBackground } from '@/lib/utils'

const NewsArticleErrorComponent: React.FC<ErrorComponentProps> = ({
  error,
}) => {
  const { t } = useTranslation()
  return (
    <>
      <BreadcrumbSection
        titleKey="common.error"
        breadcrumbs={[{ labelKey: 'breadcrumb.news', href: '/actualites' }]}
      />
      <div className="container mx-auto px-4 py-16 text-center">
        <div className="mx-auto max-w-md">
          <div className="mb-6 text-6xl">😕</div>
          <h2 className="mb-4 text-2xl font-bold text-gray-900 dark:text-white">
            {t('common.error')}
          </h2>
          <p className="mb-8 text-gray-600 dark:text-gray-400">
            {error instanceof Error
              ? error.message
              : t('common.error_loading_data')}
          </p>
          <Button asChild className="w-full sm:w-auto">
            <Link to="/actualites">
              <ArrowLeft className="mr-2 h-4 w-4" />
              {t('buttons.back_to_list', 'Retour à la liste des actualités')}
            </Link>
          </Button>
        </div>
      </div>
    </>
  )
}

const NewsArticlePendingComponent: React.FC = () => {
  return (
    <>
      <BreadcrumbSection
        titleElement={<Skeleton className="mx-auto h-12 w-3/4" />}
        breadcrumbs={[{ labelKey: 'breadcrumb.news', href: '/actualites' }]}
      />
      <div className="container mx-auto max-w-4xl px-4 py-8">
        <Card className="overflow-hidden">
          <Skeleton className="h-96 w-full" />
          <CardContent className="p-8">
            <div className="space-y-3">
              {Array.from({ length: 6 }).map((_, i) => (
                <Skeleton key={i} className="h-4 w-full" />
              ))}
              <Skeleton className="h-4 w-3/4" />
            </div>
          </CardContent>
        </Card>
      </div>
    </>
  )
}

// --- Définition de la route ---
export const Route = createFileRoute('/actualites/$newsId')({
  loader: async ({ params, context }) => {
    const newsId = parseInt(params.newsId, 10)
    if (isNaN(newsId)) {
      throw new Error('Invalid news ID')
    }

    const queryOptions = getNewsOptions({ path: { id: newsId } })

    try {
      // D'abord essayer de récupérer depuis le cache
      const cachedData = context.queryClient.getQueryData(queryOptions.queryKey)

      if (cachedData) {
        return {
          newsId,
          articleTitle: (cachedData as NewsDto).title,
          initialArticleData: cachedData as NewsDto,
        }
      }

      // Sinon, charger les données
      await context.queryClient.ensureQueryData(queryOptions)
      const article = (await context.queryClient.fetchQuery(
        queryOptions
      )) as NewsDto

      if (!article) {
        throw new Error('Article not found')
      }

      return {
        newsId,
        articleTitle: article.title,
        initialArticleData: article,
      }
    } catch (error) {
      // Gérer les erreurs d'annulation
      if (error instanceof Error && error.name === 'CancelledError') {
        throw new Error('Navigation cancelled')
      }
      throw error
    }
  },
  component: NewsArticleDetail,
  errorComponent: NewsArticleErrorComponent,
  pendingComponent: NewsArticlePendingComponent,
})

// Helper pour formater la date
const formatDate = (dateString?: string) => {
  if (!dateString) return 'Date inconnue'
  try {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  } catch {
    return 'Date invalide'
  }
}

function NewsArticleDetail() {
  const { articleTitle, initialArticleData } = useLoaderData({
    from: '/actualites/$newsId',
  })
  const { t } = useTranslation()

  const queryOptions = getNewsOptions({ path: { id: initialArticleData.id } })
  const { data: article } = useQuery({
    ...queryOptions,
    initialData: initialArticleData,
  })

  if (!article) return null

  const ArticleHero = () => {
    const categoryName = article.category?.name || t('common.unknown')
    const backgroundClass = getCategoryBackground(categoryName)

    return (
      <div
        className={`relative w-full overflow-hidden rounded-t-xl px-8 py-12 ${backgroundClass}`}
      >
        <div className="absolute inset-0 bg-black/40" />
        <div className="relative z-10">
          <Badge
            variant="secondary"
            className="mb-4 bg-white/20 text-white backdrop-blur-sm"
          >
            <Tag className="mr-1.5 h-3 w-3" />
            {categoryName}
          </Badge>
          <h2 className="mb-4 text-2xl font-bold text-white md:text-3xl lg:text-4xl">
            {article.title || articleTitle}
          </h2>

          {/* Métadonnées dans le hero */}
          <div className="flex flex-wrap items-center gap-4 text-sm text-white/90">
            <div className="flex items-center">
              <User2 className="mr-2 h-4 w-4" />
              <span>{article.authorName || t('common.admin', 'Admin')}</span>
            </div>

            <div className="h-4 w-px bg-white/30" />

            <div className="flex items-center">
              <CalendarDays className="mr-2 h-4 w-4" />
              <span>{formatDate(article.publicationDate)}</span>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <>
      <BreadcrumbSection
        titleElement={
          <h1 className="text-2xl font-bold text-gray-900 sm:text-4xl dark:text-white">
            {article.title || articleTitle}
          </h1>
        }
        breadcrumbs={[
          { labelKey: 'breadcrumb.news', href: '/actualites' },
          { label: article.title || articleTitle },
        ]}
      />

      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-8 dark:from-gray-900 dark:to-gray-800">
        <div className="container mx-auto max-w-4xl px-4">
          <Card className="overflow-hidden p-0 shadow-2xl">
            <ArticleHero />

            <CardContent className="p-8">
              {article.content ? (
                <div
                  className="prose prose-lg prose-gray dark:prose-invert prose-h2:text-2xl prose-h2:font-semibold prose-h2:mb-4 prose-h2:mt-8 prose-h3:text-xl prose-h3:font-semibold prose-h3:mb-3 prose-h3:mt-6 prose-p:text-base prose-p:leading-relaxed prose-p:mb-4 prose-img:mx-auto prose-img:max-w-full prose-img:rounded-lg prose-img:shadow-md prose-iframe:mx-auto prose-iframe:max-w-full prose-iframe:aspect-video prose-iframe:rounded-lg prose-a:text-primary hover:prose-a:text-primary/80 prose-blockquote:border-primary prose-blockquote:pl-4 prose-blockquote:italic prose-strong:text-gray-900 dark:prose-strong:text-white mx-auto max-w-none"
                  dangerouslySetInnerHTML={{ __html: article.content }}
                />
              ) : (
                <div className="py-12 text-center">
                  <div className="mx-auto mb-4 h-16 w-16 rounded-full bg-gray-100 p-4 dark:bg-gray-800">
                    <Eye className="h-full w-full text-gray-400" />
                  </div>
                  <p className="text-gray-500 dark:text-gray-400">
                    {t(
                      'homepage.blog.no_content_available',
                      'Contenu non disponible.'
                    )}
                  </p>
                </div>
              )}

              <Separator className="my-12" />

              <div className="flex flex-col items-center space-y-4 text-center sm:flex-row sm:justify-center sm:space-y-0">
                <Button
                  asChild
                  variant="outline"
                  size="lg"
                  className="w-full sm:w-auto"
                >
                  <Link to="/actualites">
                    <ArrowLeft className="mr-2 h-4 w-4" />
                    {t('buttons.back_to_all_news', 'Retour aux actualités')}
                  </Link>
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </>
  )
}
