import { createFileRoute } from '@tanstack/react-router'
import { useQuery } from '@tanstack/react-query'
import { Search, Filter, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import PaginationControls from '@/components/PaginationControls'
import { listNewsOptions } from '@/api/generated/@tanstack/react-query.gen'
import { useTranslation } from 'react-i18next'
import { useState } from 'react' // Ajout de useEffect pour le debounce
import { ArticleCard } from '@/components/ArticleCard'

/* -------------------------------------------------------------------------- */
/* Page liste                                                                 */
/* -------------------------------------------------------------------------- */
const CATEGORIES = [
  'Alertes terrain',
  'Marché & Prix',
  'Recherche & Innovations',
  'Formation & Ressources',
]

const NewsListPage = () => {
  const { t } = useTranslation()

  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('all')
  const [currentPage, setCurrentPage] = useState(1)

  const {
    data: newsPage,
    isLoading,
    error,
  } = useQuery({
    ...listNewsOptions(), // `as any` pour simplifier, idéalement typer queryParams
    staleTime: 60 * 1000, // 1 minute
    gcTime: 5 * 60 * 1000, // 5 minutes
  })

  const articles = newsPage?.content || []
  const totalPages = newsPage?.totalPages || 0
  const totalElements = newsPage?.totalElements || 0

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value)
    setCurrentPage(1) // Réinitialiser à la première page lors d'une nouvelle recherche
  }

  const handleCategoryChange = (category: string) => {
    setSelectedCategory(category)
    setCurrentPage(1) // Réinitialiser à la première page lors d'un changement de catégorie
  }

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
  }

  const handleClearFilters = () => {
    setSearchTerm('')
    setSelectedCategory('all')
    setCurrentPage(1)
  }

  if (error) {
    return (
      <>
        <BreadcrumbSection
          titleKey="common.error"
          breadcrumbs={[{ labelKey: 'breadcrumb.news' }]}
        />
        <div className="container mx-auto px-4 py-16 text-center">
          <h2 className="mb-4 text-2xl font-bold text-gray-900 dark:text-white">
            {t('common.error')}
          </h2>
          <p className="text-gray-600 dark:text-gray-400">
            {t('common.error_loading_data')}
          </p>
        </div>
      </>
    )
  }

  const filteredArticles = articles.filter(article => {
    const matchesSearch =
      article.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      article.content?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      article.authorName?.toLowerCase().includes(searchTerm.toLowerCase())
    const matchesCategory =
      selectedCategory === 'all' ||
      article.category?.name.toLowerCase() === selectedCategory.toLowerCase()
    return matchesSearch && matchesCategory
  })

  return (
    <>
      <BreadcrumbSection
        titleKey="pages.news.title"
        subtitleKey="pages.news.subtitle"
        breadcrumbs={[{ labelKey: 'breadcrumb.news' }]}
      />

      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-8 dark:from-gray-900 dark:to-gray-800">
        <div className="container mx-auto max-w-7xl px-4">
          <div className="mb-8 rounded-lg bg-white p-6 shadow-sm dark:bg-gray-800">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
              <div className="relative w-full sm:max-w-md">
                <Search className="absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-gray-400" />
                <Input
                  placeholder={t('common.search_placeholder')}
                  value={searchTerm}
                  onChange={handleSearchChange}
                  className="pl-10"
                />
              </div>

              <div className="flex items-center gap-2">
                <Filter className="h-4 w-4 text-gray-500" />
                <Select
                  value={selectedCategory}
                  onValueChange={handleCategoryChange}
                >
                  <SelectTrigger className="w-full sm:w-48">
                    <SelectValue placeholder={t('common.all_categories')} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">
                      {t('common.all_categories')}
                    </SelectItem>
                    {CATEGORIES.map(c => (
                      <SelectItem key={c} value={c}>
                        {c}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="ml-auto flex items-center gap-4">
                <span className="text-sm text-gray-500 dark:text-gray-400">
                  {isLoading
                    ? t('common.loading')
                    : t('common.results_count', { count: totalElements })}
                </span>

                {(searchTerm || selectedCategory !== 'all') && (
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={handleClearFilters}
                  >
                    {t('common.clear_filters')}
                  </Button>
                )}
              </div>
            </div>
          </div>

          {isLoading ? (
            <div className="flex items-center justify-center py-16">
              <Loader2 className="text-primary h-12 w-12 animate-spin" />
            </div>
          ) : filteredArticles.length === 0 ? (
            <div className="py-16 text-center">
              <h3 className="mb-4 text-xl font-semibold text-gray-900 dark:text-white">
                {t('pages.news.no_articles_found')}
              </h3>
              <p className="text-gray-600 dark:text-gray-400">
                {searchTerm || selectedCategory !== 'all'
                  ? t('pages.news.try_different_search')
                  : t('pages.news.no_articles_yet')}
              </p>
              {(searchTerm || selectedCategory !== 'all') && (
                <Button
                  variant="outline"
                  className="mt-4"
                  onClick={handleClearFilters}
                >
                  {t('common.clear_filters')}
                </Button>
              )}
            </div>
          ) : (
            <>
              <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
                {filteredArticles.map(a => (
                  <ArticleCard key={a.id} article={a} />
                ))}
              </div>

              {totalPages > 1 && (
                <div className="mt-12 flex justify-center">
                  <PaginationControls
                    current={currentPage}
                    total={totalPages}
                    onChange={handlePageChange}
                  />
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </>
  )
}

export const Route = createFileRoute('/actualites/')({
  component: NewsListPage,
})
