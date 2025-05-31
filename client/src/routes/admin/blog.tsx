import { NewsArticleManagement } from '@/components/admin/blog-management/NewsArticleManagement'
import { NewsCategoryManagement } from '@/components/admin/blog-management/NewsCategoryManagement'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/admin/blog')({
  component: BlogManagementPage,
})

function BlogManagementPage() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto flex flex-1 flex-col gap-2 lg:py-2">
      <div className="mb-4">
        <h1 className="text-3xl font-bold tracking-tight">
          {t('admin.blog_management.title', 'Gestion du Blog')}
        </h1>
        <p className="text-muted-foreground mt-2 text-lg">
          {t(
            'admin.blog_management.description',
            'Gérez les catégories et les articles de votre blog.'
          )}
        </p>
      </div>

      <Tabs defaultValue="articles" className="space-y-4">
        <TabsList>
          <TabsTrigger value="articles">
            {t('admin.blog_management.tabs.articles', 'Articles')}
          </TabsTrigger>
          <TabsTrigger value="categories">
            {t('admin.blog_management.tabs.categories', 'Catégories')}
          </TabsTrigger>
        </TabsList>
        <TabsContent value="articles">
          <NewsArticleManagement />
        </TabsContent>
        <TabsContent value="categories">
          <NewsCategoryManagement />
        </TabsContent>
      </Tabs>
    </div>
  )
}
