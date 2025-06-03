import type { NewsDto, NewsCreateDto } from '@/api/generated'
import {
  createNewsMutation,
  deleteNewsMutation,
  listNewsCategoriesOptions,
  listNewsOptions,
  listNewsQueryKey,
  updateNewsMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import { zNewsCreateDto } from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog' // Import AlertDialog components
import { Input } from '@/components/ui/input'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Edit,
  PlusCircle,
  Trash2,
  Loader2,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react'
import { useMemo, useState, useEffect, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'

export function NewsArticleManagement() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingArticle, setEditingArticle] = useState<NewsDto | null>(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [page, setPage] = useState(0)
  const [isConfirmDeleteDialogOpen, setIsConfirmDeleteDialogOpen] =
    useState(false)
  const [articleIdToDelete, setArticleIdToDelete] = useState<number | null>(
    null
  )

  const { data: articlesPage, isLoading: isLoadingArticles } = useQuery({
    ...listNewsOptions(),
    placeholderData: previousData => previousData,
  })
  const { data: categories, isLoading: isLoadingCategories } = useQuery(
    listNewsCategoriesOptions()
  )

  const form = useAppForm({
    validators: {
      onChange: zNewsCreateDto,
    },
    defaultValues: {
      title: '',
      content: '',
      publicationDate: new Date().toISOString(),
      authorName: '',
      categoryId: undefined,
    } as Partial<NewsCreateDto>,
    onSubmit: async ({ value }) => {
      try {
        if (editingArticle) {
          await updateMutation.mutateAsync({
            path: { id: editingArticle.id! },
            body: value as NewsCreateDto,
          })
          toast.success(
            t(
              'admin.blog_management.articles.update_success',
              'Article mis à jour.'
            )
          )
        } else {
          const createPayload = {
            ...value,
          }
          await createMutation.mutateAsync({
            body: createPayload as any,
          })
          toast.success(
            t('admin.blog_management.articles.create_success', 'Article créé.')
          )
        }
        queryClient.invalidateQueries({ queryKey: listNewsQueryKey() })
        handleCloseForm()
      } catch (error: any) {
        const errorMessage =
          error.errors?.[0]?.message ||
          error.message ||
          t('common.error_unknown')
        toast.error(
          t('admin.blog_management.articles.submit_error', 'Erreur: ') +
            errorMessage
        )
      }
    },
  })

  useEffect(() => {
    if (isFormOpen) {
      if (editingArticle) {
        form.reset({
          title: editingArticle.title,
          content: editingArticle.content || '',
          publicationDate: editingArticle.publicationDate
            ? new Date(editingArticle.publicationDate).toISOString()
            : new Date().toISOString(),
          categoryId: editingArticle.category?.id,
          authorName: editingArticle.authorName || '',
        })
      } else {
        // Pour un nouvel article
        form.reset({
          title: '',
          content: '',
          publicationDate: new Date().toISOString(),
          authorName: '',
          categoryId: undefined,
        })
      }
    }
  }, [editingArticle, isFormOpen, form])

  const createMutation = useMutation(createNewsMutation())
  const updateMutation = useMutation(updateNewsMutation())
  const deleteMutation = useMutation(deleteNewsMutation())

  const handleOpenForm = useCallback((article?: NewsDto) => {
    setEditingArticle(article || null)
    setIsFormOpen(true)
  }, [])

  const handleCloseForm = useCallback(() => {
    setIsFormOpen(false)
    setEditingArticle(null)
    form.reset()
  }, [form])

  const handleDelete = useCallback((id: number) => {
    setArticleIdToDelete(id)
    setIsConfirmDeleteDialogOpen(true)
  }, [])

  const confirmDelete = useCallback(async () => {
    if (articleIdToDelete === null) return

    try {
      await deleteMutation.mutateAsync({ path: { id: articleIdToDelete } })
      toast.success(
        t('admin.blog_management.articles.delete_success', 'Article supprimé.')
      )
      queryClient.invalidateQueries({
        queryKey: listNewsQueryKey(),
      })
    } catch (error: any) {
      const errorMessage =
        error.errors?.[0]?.message || error.message || t('common.error_unknown')
      toast.error(
        t(
          'admin.blog_management.articles.delete_error',
          'Erreur lors de la suppression: '
        ) + errorMessage
      )
    } finally {
      setIsConfirmDeleteDialogOpen(false)
      setArticleIdToDelete(null)
    }
  }, [articleIdToDelete, t, deleteMutation, queryClient])

  const categoryOptions = useMemo(() => {
    return categories?.map(cat => ({ id: cat.id!, label: cat.name! })) || []
  }, [categories])

  const filteredArticles = useMemo(() => {
    if (!articlesPage?.content) return []
    if (!searchTerm) return articlesPage.content
    return articlesPage.content.filter(article =>
      article.title?.toLowerCase().includes(searchTerm.toLowerCase())
    )
  }, [articlesPage?.content, searchTerm])

  const totalPages = articlesPage?.totalPages || 0

  if (isLoadingArticles && !articlesPage && page === 0) {
    return (
      <div className="flex min-h-[300px] items-center justify-center">
        <Loader2 className="text-primary h-8 w-8 animate-spin" />
      </div>
    )
  }

  return (
    <div>
      <div className="flex items-center justify-between py-4">
        <Input
          placeholder={t(
            'admin.blog_management.articles.filter_placeholder',
            'Filtrer par titre...'
          )}
          value={searchTerm}
          onChange={e => {
            setSearchTerm(e.target.value)
          }}
          className="max-w-sm"
        />
        <Button onClick={() => handleOpenForm()}>
          <PlusCircle className="mr-2 h-4 w-4" />
          {t('admin.blog_management.articles.add_button', 'Ajouter un article')}
        </Button>
      </div>

      {/* Manual Table Implementation */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>
                {t('admin.blog_management.articles.id', 'ID')}
              </TableHead>
              <TableHead>
                {t('admin.blog_management.articles.title', 'Titre')}
              </TableHead>
              <TableHead>
                {t('admin.blog_management.articles.category', 'Catégorie')}
              </TableHead>
              <TableHead>
                {t('admin.blog_management.articles.author', 'Auteur')}
              </TableHead>
              <TableHead>
                {t(
                  'admin.blog_management.articles.publication_date',
                  'Date de publication'
                )}
              </TableHead>
              <TableHead className="text-right">
                {t('admin.blog_management.articles.actions', 'Actions')}
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {/*
            {isLoadingArticles &&
              page !== articlesPage?.number && (
                <TableRow>
                  <TableCell colSpan={6} className="h-24 text-center">
                    <Loader2 className="mx-auto h-6 w-6 animate-spin" />
                  </TableCell>
                </TableRow>
              )}
                */}
            {!isLoadingArticles &&
            filteredArticles &&
            filteredArticles.length > 0
              ? filteredArticles.map(article => (
                  <TableRow key={article.id}>
                    <TableCell>{article.id}</TableCell>
                    <TableCell className="max-w-[200px] truncate md:max-w-[300px] lg:max-w-[400px]">
                      {article.title}
                    </TableCell>
                    <TableCell>{article.category?.name || '-'}</TableCell>
                    <TableCell className="max-w-[100px] truncate md:max-w-[150px]">
                      {article.authorName || t('common.admin', 'Admin')}
                    </TableCell>
                    <TableCell>
                      {new Date(article.publicationDate!).toLocaleDateString()}
                    </TableCell>
                    <TableCell className="space-x-2 text-right">
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => handleOpenForm(article)}
                        title={t('buttons.edit', 'Modifier')}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="destructive"
                        size="icon"
                        onClick={() => handleDelete(article.id!)}
                        disabled={
                          deleteMutation.isPending &&
                          deleteMutation.variables?.path.id === article.id
                        }
                        title={t('buttons.delete', 'Supprimer')}
                      >
                        {deleteMutation.isPending &&
                        deleteMutation.variables?.path.id === article.id &&
                        articleIdToDelete === article.id ? (
                          <Loader2 className="h-4 w-4 animate-spin" />
                        ) : (
                          <Trash2 className="h-4 w-4" />
                        )}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              : !isLoadingArticles && (
                  <TableRow>
                    <TableCell
                      colSpan={6}
                      className="text-muted-foreground h-24 text-center"
                    >
                      {searchTerm
                        ? t(
                            'admin.blog_management.articles.no_articles_match_search',
                            'Aucun article ne correspond à votre recherche.'
                          )
                        : t(
                            'admin.blog_management.articles.no_articles_found',
                            'Aucun article trouvé.'
                          )}
                    </TableCell>
                  </TableRow>
                )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination Controls */}
      {totalPages > 0 && (
        <div className="flex items-center justify-end space-x-2 py-4">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0 || isLoadingArticles}
          >
            <ChevronLeft className="mr-2 h-4 w-4" />
            {t('pagination.previous', 'Précédent')}
          </Button>
          <span className="text-sm">
            {t('pagination.page_x_of_y', `Page ${page + 1} sur ${totalPages}`, {
              currentPage: page + 1,
              totalPages,
            })}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
            disabled={page >= totalPages - 1 || isLoadingArticles}
          >
            {t('pagination.next', 'Suivant')}
            <ChevronRight className="ml-2 h-4 w-4" />
          </Button>
        </div>
      )}

      <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
        <DialogContent className="max-h-[90vh] w-full max-w-[80vw]! overflow-y-auto">
          {/* Important: Wrap form content with form.Provider if useAppForm is used this way */}
          <form
            onSubmit={e => {
              e.preventDefault()
              e.stopPropagation()
              form.handleSubmit()
            }}
            className="space-y-4"
          >
            <DialogHeader>
              <DialogTitle>
                {editingArticle
                  ? t(
                      'admin.blog_management.articles.edit_title',
                      "Modifier l'article"
                    )
                  : t(
                      'admin.blog_management.articles.add_title',
                      'Ajouter un article'
                    )}
              </DialogTitle>
              <DialogDescription>
                {t(
                  'admin.blog_management.articles.form_description',
                  "Remplissez les détails de l'article."
                )}
              </DialogDescription>
            </DialogHeader>

            <form.AppField name="title">
              {field => (
                <field.TextField
                  label={t(
                    'admin.blog_management.articles.title_label',
                    'Titre'
                  )}
                  required
                />
              )}
            </form.AppField>

            {/* Grouped fields for author, category, and publication date */}
            <div className="flex flex-col space-y-4 md:flex-row md:space-y-0 md:space-x-4">
              <div className="flex-1 md:w-1/3">
                <form.AppField name="authorName">
                  {field => (
                    <field.TextField
                      label={t(
                        'admin.blog_management.articles.author_name_label',
                        "Nom de l'auteur"
                      )}
                      placeholder={t(
                        'admin.blog_management.articles.author_name_placeholder',
                        'Optionnel, "Admin" par défaut si vide par le système'
                      )}
                    />
                  )}
                </form.AppField>
              </div>

              <div className="flex-1 md:w-1/3">
                <form.AppField name="categoryId">
                  {field => (
                    <field.VirtualizedSelectField
                      label={t(
                        'admin.blog_management.articles.category_label',
                        'Catégorie'
                      )}
                      options={categoryOptions}
                      placeholder={t(
                        'admin.blog_management.articles.select_category_placeholder',
                        'Sélectionner une catégorie'
                      )}
                      required
                      disabled={isLoadingCategories}
                      modal={true}
                    />
                  )}
                </form.AppField>
              </div>

              <div className="flex-1 md:w-1/3">
                <form.AppField name="publicationDate">
                  {field => (
                    <field.DateTimePickerField
                      label={t(
                        'admin.blog_management.articles.publication_date',
                        'Date de publication'
                      )}
                      required
                    />
                  )}
                </form.AppField>
              </div>
            </div>

            <form.AppField name="content">
              {field => (
                <field.RichTextEditorField
                  label={t(
                    'admin.blog_management.articles.content_label',
                    'Contenu'
                  )}
                  placeholder={t(
                    'admin.blog_management.articles.content_placeholder',
                    'Écrivez votre article ici...'
                  )}
                />
              )}
            </form.AppField>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={handleCloseForm}>
                {t('common.cancel', 'Annuler')}
              </Button>
              <Button
                type="submit"
                disabled={
                  form.state.isSubmitting ||
                  createMutation.isPending ||
                  updateMutation.isPending
                }
              >
                {form.state.isSubmitting ||
                createMutation.isPending ||
                updateMutation.isPending
                  ? t('buttons.saving')
                  : t('common.save', 'Enregistrer')}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <AlertDialog
        open={isConfirmDeleteDialogOpen}
        onOpenChange={setIsConfirmDeleteDialogOpen}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>
              {t('common.confirm_delete_title', 'Êtes-vous sûr ?')}
            </AlertDialogTitle>
            <AlertDialogDescription>
              {t(
                'admin.blog_management.articles.confirm_delete_description',
                "Cette action est irréversible et supprimera l'article."
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setArticleIdToDelete(null)}>
              {t('common.cancel', 'Annuler')}
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              disabled={deleteMutation.isPending}
            >
              {deleteMutation.isPending && articleIdToDelete !== null ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : null}
              {t('common.delete', 'Supprimer')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
