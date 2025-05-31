import type { NewsCategoryDto } from '@/api/generated'
import {
  createNewsCategoryMutation,
  deleteNewsCategoryMutation,
  listNewsCategoriesOptions,
  updateNewsCategoryMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import { zNewsCategoryDto } from '@/api/generated/zod.gen'
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
import { Edit, PlusCircle, Trash2, Loader2 } from 'lucide-react'
import { useMemo, useState, useEffect, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'

export function NewsCategoryManagement() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingCategory, setEditingCategory] =
    useState<NewsCategoryDto | null>(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [isConfirmDeleteDialogOpen, setIsConfirmDeleteDialogOpen] =
    useState(false)
  const [categoryIdToDelete, setCategoryIdToDelete] = useState<number | null>(
    null
  )

  const { data: categories, isLoading } = useQuery(listNewsCategoriesOptions())

  const form = useAppForm({
    validators: {
      onChange: zNewsCategoryDto,
    },
    defaultValues: {
      id: 0,
      name: '',
      description: '',
    } as NewsCategoryDto,
    onSubmit: async ({ value }) => {
      try {
        if (editingCategory) {
          await updateMutation.mutateAsync({
            path: { id: editingCategory.id! },
            body: value,
          })
          toast.success(
            t(
              'admin.blog_management.categories.update_success',
              'Catégorie mise à jour.'
            )
          )
        } else {
          await createMutation.mutateAsync({ body: value })
          toast.success(
            t(
              'admin.blog_management.categories.create_success',
              'Catégorie créée.'
            )
          )
        }
        queryClient.invalidateQueries({
          queryKey: listNewsCategoriesOptions().queryKey,
        })
        handleCloseForm()
      } catch (error: any) {
        const errorMessage =
          error.errors?.[0]?.message ||
          error.message ||
          t('common.error_unknown')
        toast.error(
          t('admin.blog_management.categories.submit_error', 'Erreur: ') +
            errorMessage
        )
      }
    },
  })

  const createMutation = useMutation(createNewsCategoryMutation())
  const updateMutation = useMutation(updateNewsCategoryMutation())
  const deleteMutation = useMutation(deleteNewsCategoryMutation())

  useEffect(() => {
    if (isFormOpen) {
      if (editingCategory) {
        form.reset({
          id: editingCategory.id,
          name: editingCategory.name,
          description: editingCategory.description || '',
        })
      } else {
        form.reset({ id: 0, name: '', description: '' })
      }
    }
  }, [isFormOpen, editingCategory, form])

  const handleOpenForm = useCallback((category?: NewsCategoryDto) => {
    setEditingCategory(category || null)
    setIsFormOpen(true)
  }, [])

  const handleCloseForm = () => {
    setIsFormOpen(false)
    setEditingCategory(null)
    form.reset()
  }

  const handleDelete = useCallback((id: number) => {
    setCategoryIdToDelete(id)
    setIsConfirmDeleteDialogOpen(true)
  }, [])

  const confirmDelete = useCallback(async () => {
    if (categoryIdToDelete === null) return

    try {
      await deleteMutation.mutateAsync({ path: { id: categoryIdToDelete } })
      toast.success(
        t(
          'admin.blog_management.categories.delete_success',
          'Catégorie supprimée.'
        )
      )
      queryClient.invalidateQueries({
        queryKey: listNewsCategoriesOptions().queryKey,
      })
    } catch (error: any) {
      const errorMessage =
        error.errors?.[0]?.message || error.message || t('common.error_unknown')
      toast.error(
        t(
          'admin.blog_management.categories.delete_error',
          'Erreur lors de la suppression: '
        ) + errorMessage
      )
    } finally {
      setIsConfirmDeleteDialogOpen(false)
      setCategoryIdToDelete(null)
    }
  }, [categoryIdToDelete, t, deleteMutation, queryClient])

  const filteredCategories = useMemo(() => {
    if (!categories) return []
    return categories.filter(category =>
      category.name?.toLowerCase().includes(searchTerm.toLowerCase())
    )
  }, [categories, searchTerm])

  if (isLoading && !categories) {
    return (
      <div className="flex min-h-[200px] items-center justify-center">
        <Loader2 className="text-primary h-8 w-8 animate-spin" />
      </div>
    )
  }

  return (
    <div>
      <div className="flex items-center justify-between py-4">
        <Input
          placeholder={t(
            'admin.blog_management.categories.filter_placeholder',
            'Filtrer par nom...'
          )}
          value={searchTerm}
          onChange={e => setSearchTerm(e.target.value)}
          className="max-w-sm"
        />
        <Button onClick={() => handleOpenForm()}>
          <PlusCircle className="mr-2 h-4 w-4" />
          {t(
            'admin.blog_management.categories.add_button',
            'Ajouter une catégorie'
          )}
        </Button>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>
                {t('admin.blog_management.categories.id', 'ID')}
              </TableHead>
              <TableHead>
                {t('admin.blog_management.categories.name', 'Nom')}
              </TableHead>
              <TableHead>
                {t(
                  'admin.blog_management.categories.description',
                  'Description'
                )}
              </TableHead>
              <TableHead className="text-right">
                {t('admin.blog_management.categories.actions', 'Actions')}
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading && (
              <TableRow>
                <TableCell colSpan={4} className="h-24 text-center">
                  <Loader2 className="mx-auto h-6 w-6 animate-spin" />
                </TableCell>
              </TableRow>
            )}
            {!isLoading && filteredCategories && filteredCategories.length > 0
              ? filteredCategories.map(category => (
                  <TableRow key={category.id}>
                    <TableCell>{category.id}</TableCell>
                    <TableCell>{category.name}</TableCell>
                    <TableCell>{category.description}</TableCell>
                    <TableCell className="space-x-2 text-right">
                      <Button
                        variant="outline"
                        size="icon"
                        onClick={() => handleOpenForm(category)}
                        title={t('buttons.edit', 'Modifier')}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="destructive"
                        size="icon"
                        onClick={() => handleDelete(category.id!)}
                        disabled={
                          deleteMutation.isPending &&
                          deleteMutation.variables?.path.id === category.id
                        }
                        title={t('buttons.delete', 'Supprimer')}
                      >
                        {deleteMutation.isPending &&
                        deleteMutation.variables?.path.id === category.id &&
                        categoryIdToDelete === category.id ? (
                          <Loader2 className="h-4 w-4 animate-spin" />
                        ) : (
                          <Trash2 className="h-4 w-4" />
                        )}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              : !isLoading && (
                  <TableRow>
                    <TableCell
                      colSpan={4}
                      className="text-muted-foreground h-24 text-center"
                    >
                      {searchTerm
                        ? t(
                            'admin.blog_management.categories.no_categories_match_search',
                            'Aucune catégorie ne correspond à votre recherche.'
                          )
                        : t(
                            'admin.blog_management.categories.no_categories_found',
                            'Aucune catégorie trouvée.'
                          )}
                    </TableCell>
                  </TableRow>
                )}
          </TableBody>
        </Table>
      </div>

      <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
        <DialogContent>
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
                {editingCategory
                  ? t(
                      'admin.blog_management.categories.edit_title',
                      'Modifier la catégorie'
                    )
                  : t(
                      'admin.blog_management.categories.add_title',
                      'Ajouter une catégorie'
                    )}
              </DialogTitle>
              <DialogDescription>
                {t(
                  'admin.blog_management.categories.form_description',
                  'Remplissez les détails de la catégorie.'
                )}
              </DialogDescription>
            </DialogHeader>

            <form.AppField name="name">
              {field => (
                <field.TextField
                  label={t(
                    'admin.blog_management.categories.name_label',
                    'Nom'
                  )}
                  required
                />
              )}
            </form.AppField>

            <form.AppField name="description">
              {field => (
                <field.TextAreaField
                  label={t(
                    'admin.blog_management.categories.description_label',
                    'Description'
                  )}
                  required
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
                'admin.blog_management.categories.confirm_delete_description',
                'Cette action est irréversible et supprimera la catégorie.'
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setCategoryIdToDelete(null)}>
              {t('common.cancel', 'Annuler')}
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              disabled={deleteMutation.isPending}
            >
              {deleteMutation.isPending && categoryIdToDelete !== null ? (
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
