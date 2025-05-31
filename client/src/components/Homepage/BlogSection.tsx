import { Button } from '../ui/button'
import { Link } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'
import { useQuery } from '@tanstack/react-query'
import { ChevronRight, Loader2 } from 'lucide-react'
import { listNewsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { ArticleCard } from '../ArticleCard'
import { useEffect } from 'react'
import { Observer } from 'tailwindcss-intersect'

export function BlogSection() {
  const { t } = useTranslation()

  const {
    data: articlesPage,
    isLoading,
    error,
  } = useQuery({
    ...listNewsOptions({
      query: { page: 0, size: 3, sort: ['publicationDate,desc'] },
    }),
    staleTime: 5 * 60 * 1000,
    gcTime: 10 * 60 * 1000,
    retry: false,
  })

  const articles = articlesPage?.content

  useEffect(() => {
    if (articles && articles.length > 0) {
      if (typeof Observer !== 'undefined' && Observer.restart) {
        Observer.restart()
      }
    }
  }, [articles])

  if (isLoading) {
    return (
      <section className="bg-gray-50 py-8 lg:py-16 dark:bg-gray-900">
        <div className="mx-auto max-w-screen-xl px-4 text-center lg:px-6">
          <Loader2 className="text-primary mx-auto h-12 w-12 animate-spin" />
          <p className="mt-4 text-gray-500 dark:text-gray-400">
            {t('common.loading')}
          </p>
        </div>
      </section>
    )
  }

  if (error) {
    if (error.name === 'CancelledError') {
      return null
    }
    return (
      <section className="bg-gray-50 py-8 lg:py-16 dark:bg-gray-900">
        <div className="mx-auto max-w-screen-xl px-4 text-center lg:px-6">
          <p className="text-red-500">{t('common.error_loading_data')}</p>
        </div>
      </section>
    )
  }

  if (!articles || articles.length === 0) {
    return (
      <section className="bg-gray-50 py-8 lg:py-16 dark:bg-gray-900">
        <div className="intersect-once intersect-half intersect:opacity-100 intersect:translate-y-0 mx-auto max-w-screen-xl translate-y-4 px-4 opacity-0 transition-all duration-500 ease-out lg:px-6">
          <div className="mx-auto mb-8 max-w-screen-sm text-center lg:mb-16">
            <h2 className="mb-4 text-3xl font-extrabold tracking-tight text-gray-900 lg:text-4xl dark:text-white">
              {t('homepage.blog.title')}
            </h2>
            <p className="font-light text-gray-500 sm:text-xl dark:text-gray-400">
              {t(
                'homepage.blog.no_articles_yet',
                'Aucun article à afficher pour le moment.'
              )}
            </p>
          </div>
        </div>
      </section>
    )
  }

  return (
    <section className="bg-gray-50 py-8 lg:py-16 dark:bg-gray-900">
      <div className="intersect-once intersect-half intersect:opacity-100 intersect:translate-y-0 mx-auto max-w-screen-xl translate-y-4 px-4 opacity-0 transition-all duration-500 ease-out lg:px-6">
        <div className="mx-auto mb-8 max-w-screen-sm text-center lg:mb-16">
          <h2 className="mb-4 text-3xl font-extrabold tracking-tight text-gray-900 lg:text-4xl dark:text-white">
            {t('homepage.blog.title')}
          </h2>
          <p className="font-light text-gray-500 sm:text-xl dark:text-gray-400">
            {t('homepage.blog.subtitle')}
          </p>
        </div>
        <div className="grid gap-8 lg:grid-cols-3">
          {articles.map((article, index) => (
            <ArticleCard
              key={article.id}
              article={article}
              animationDelay={`delay-${index * 150}`}
            />
          ))}
        </div>

        {articles && articles.length > 0 && (
          <div className="mt-12 text-center">
            <Link to="/actualites">
              <Button variant="outline" size="lg" className="group">
                {t('buttons.view_all_news')}
                <ChevronRight className="ml-2 h-5 w-5 transition-transform group-hover:translate-x-1" />
              </Button>
            </Link>
          </div>
        )}
      </div>
    </section>
  )
}
