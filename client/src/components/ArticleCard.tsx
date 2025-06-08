import { useTranslation } from 'react-i18next'
import { Card, CardContent } from './ui/card'
import { getCategoryBackground } from '@/lib/utils'
import type { NewsDto } from '@/api/generated'
import { Link } from '@tanstack/react-router'
import { User, CalendarDays, ChevronRight } from 'lucide-react'
import { formatDateOnly } from '@/utils/formatter'

/**
 * Composant React pour afficher une carte d'article de blog ou d'actualité
 */
export const ArticleCard = ({
  article,
  animationDelay,
}: {
  article: NewsDto
  animationDelay?: string // ex: "delay-150", "delay-300"
}) => {
  const { t } = useTranslation()
  const categoryName = article.category?.name || t('common.unknown')
  const backgroundClass = getCategoryBackground(categoryName)

  const animationClasses = `
    opacity-0 translate-y-4
    intersect:opacity-100 intersect:translate-y-0
    transition-all duration-500 ease-out
    intersect-once intersect-half
    ${animationDelay || ''}
  `

  return (
    <div className={animationClasses.trim().replace(/\s+/g, ' ')}>
      <Card className="group flex h-full flex-col overflow-hidden rounded-lg border border-gray-200 bg-white p-0 shadow-lg transition-all hover:shadow-xl dark:border-gray-700 dark:bg-gray-800">
        <div
          className={`relative aspect-video w-full overflow-hidden ${backgroundClass}`}
        >
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/30 to-transparent" />
          <div className="absolute top-3 right-3 z-10">
            <span className="flex items-center rounded-md bg-black/10 px-2 py-1 text-xs text-gray-100 backdrop-blur-sm sm:text-[12px]">
              <CalendarDays className="mr-1.5 h-3 w-3 flex-shrink-0" />
              {formatDateOnly(article.publicationDate)}
            </span>
          </div>
          <div className="absolute top-1/2 left-1/2 z-10 w-full max-w-[calc(100%-4rem)] -translate-x-1/2 -translate-y-1/2 px-4 text-center">
            <h3 className="text-xl font-bold text-white drop-shadow-md md:text-2xl">
              <Link
                to="/actualites/$newsId"
                params={{ newsId: article.id.toString() }}
                className="line-clamp-3 hover:underline"
              >
                {categoryName}
              </Link>
            </h3>
          </div>
          <div className="absolute right-3 bottom-3 left-3 z-10 text-white sm:text-[12px]">
            <div className="inline-flex items-center space-x-2 rounded-md bg-black/10 px-2 py-1">
              <User className="h-3.5 w-3.5 flex-shrink-0" />
              <span>{article.authorName || t('common.admin', 'Admin')}</span>
            </div>
          </div>
        </div>
        <CardContent className="flex flex-grow flex-col px-5 pt-4 pb-4">
          <h3 className="hover:text-primary-600 dark:hover:text-primary-400 mb-2 text-xl font-bold tracking-tight text-gray-900 transition-colors dark:text-white">
            <Link
              to="/actualites/$newsId"
              params={{ newsId: article.id.toString() }}
            >
              {article.title}
            </Link>
          </h3>
          <p className="mb-4 line-clamp-3 flex-grow text-sm text-gray-600 dark:text-gray-400">
            {article.content
              ? article.content.substring(0, 150).replace(/<[^>]+>/g, '')
              : t('homepage.blog.no_description')}
            {article.content && article.content.length > 150 && '…'}
          </p>
          <div className="mt-auto border-t border-gray-100 pt-4 text-right dark:border-gray-700">
            <Link
              to="/actualites/$newsId"
              params={{ newsId: article.id.toString() }}
              className="text-primary-600 dark:text-primary-400 hover:text-primary-700 dark:hover:text-primary-300 inline-flex items-center text-sm font-medium hover:underline"
            >
              {t('homepage.blog.read_more')}
              <ChevronRight className="ml-1 h-4 w-4" />
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
