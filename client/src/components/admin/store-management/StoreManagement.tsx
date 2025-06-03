import type { StoreDetailDto } from '@/api/generated'
import {
  deleteStoreMutation,
  listStoresOptions,
  listStoresQueryKey,
  listUsersOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent } from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
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
  Search,
  MoreHorizontal,
  ArrowUpDown,
} from 'lucide-react'
import { useMemo, useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { StoreForm } from './StoreForm'
import PaginationControls from '@/components/PaginationControls'
import { sortData, type SortConfig } from '@/lib/sorting'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu'

type SortableStoreColumn = keyof Pick<StoreDetailDto, 'name'> | 'userFullName'
export function StoreManagement() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingStore, setEditingStore] = useState<StoreDetailDto | null>(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [isConfirmDeleteDialogOpen, setIsConfirmDeleteDialogOpen] =
    useState(false)
  const [storeIdToDelete, setStoreIdToDelete] = useState<number | null>(null)

  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 10

  const [sortColumn, setSortColumn] = useState<SortableStoreColumn | null>(
    'name'
  )
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')

  const { data: allStores, isLoading: isLoadingStores } = useQuery({
    ...listStoresOptions(),
    staleTime: 5 * 60 * 1000,
  })

  const { data: users, isLoading: isLoadingUsers } =
    useQuery(listUsersOptions())

  const userMap = useMemo(() => {
    if (!users) return new Map<number, string>()
    return new Map(
      users.map(user => [user.id, `${user.firstName} ${user.lastName}`])
    )
  }, [users])

  const handleOpenForm = useCallback((store?: StoreDetailDto) => {
    setEditingStore(store || null)
    setIsFormOpen(true)
  }, [])

  const handleCloseForm = () => {
    setIsFormOpen(false)
    setEditingStore(null)
  }

  const handleDelete = useCallback((id: number) => {
    setStoreIdToDelete(id)
    setIsConfirmDeleteDialogOpen(true)
  }, [])

  const deleteStore = useMutation({
    ...deleteStoreMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listStoresQueryKey() })
      toast.success(
        t(
          'admin.store_management.delete_success',
          'Magasin supprimé avec succès.'
        )
      )
    },
    onError: (error: any) => {
      const errorMessage =
        error.errors?.[0]?.message || error.message || t('common.error_unknown')
      toast.error(
        t(
          'admin.store_management.delete_error',
          "Impossible de supprimer ce magasin car il est référencé par d'autres entités. Détails : "
        ) + errorMessage
      )
    },
    onSettled: () => {
      setIsConfirmDeleteDialogOpen(false)
      setStoreIdToDelete(null)
    },
  })

  const confirmDelete = useCallback(async () => {
    if (storeIdToDelete === null) return
    deleteStore.mutate({ path: { id: storeIdToDelete } })
  }, [storeIdToDelete, deleteStore])

  const processedStores = useMemo(() => {
    if (!allStores) return { paginatedStores: [], totalPages: 0, totalItems: 0 }

    const storesWithUserNames = allStores.map(store => ({
      ...store,
      userFullName: userMap.get(store.userId) || '',
    }))

    const filtered = storesWithUserNames.filter(store => {
      const searchTermLower = searchTerm.toLowerCase()
      return (
        store.name?.toLowerCase().includes(searchTermLower) ||
        (userMap.get(store.userId) || '')
          .toLowerCase()
          .includes(searchTermLower) ||
        store.address.street?.toLowerCase().includes(searchTermLower)
      )
    })

    const sortConfig: SortConfig<(typeof filtered)[0]> = {
      sortKey: sortColumn,
      sortDirection: sortDirection,
    }
    const sorted = sortData(filtered, sortConfig)

    const totalPages = Math.ceil(sorted.length / itemsPerPage)
    const startIndex = (currentPage - 1) * itemsPerPage
    const paginatedStores = sorted.slice(startIndex, startIndex + itemsPerPage)
    return { paginatedStores, totalPages, totalItems: sorted.length }
  }, [
    allStores,
    searchTerm,
    userMap,
    sortColumn,
    sortDirection,
    currentPage,
    itemsPerPage,
  ])

  const handleSort = (column: SortableStoreColumn) => {
    if (sortColumn === column) {
      setSortDirection(prev => (prev === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortColumn(column)
      setSortDirection('asc')
    }
    setCurrentPage(1)
  }

  const renderSortIcon = (column: SortableStoreColumn) => {
    if (sortColumn !== column) {
      return <ArrowUpDown className="text-muted-foreground/50 ml-2 h-3 w-3" />
    }
    return <ArrowUpDown className="ml-2 h-3 w-3 opacity-100" />
  }

  if ((isLoadingStores && !allStores) || (isLoadingUsers && !users)) {
    return (
      <div className="flex min-h-[200px] items-center justify-center">
        <Loader2 className="text-primary h-8 w-8 animate-spin" />
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-col items-start gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div className="relative w-full max-w-xs">
          <Search className="text-muted-foreground absolute top-2.5 left-2.5 h-4 w-4" />
          <Input
            type="search"
            placeholder={t(
              'admin.store_management.filter_placeholder',
              'Filtrer par nom, utilisateur, rue...'
            )}
            className="w-full pl-8"
            value={searchTerm}
            onChange={e => {
              setSearchTerm(e.target.value)
              setCurrentPage(1)
            }}
          />
        </div>
        <Button onClick={() => handleOpenForm()}>
          <PlusCircle className="mr-2 h-4 w-4" />
          {t('admin.store_management.add_button', 'Ajouter un magasin')}
        </Button>
      </div>
      <div className="text-muted-foreground text-sm">
        {t(
          'admin.store_management.results_count',
          '{{count}} sur {{total}} magasins',
          {
            count: processedStores.totalItems,
            total: allStores?.length || 0,
          }
        )}
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead
                className="hover:bg-muted/50 cursor-pointer"
                onClick={() => handleSort('name')}
              >
                <div className="flex items-center">
                  {t('admin.store_management.name', 'Nom')}
                  {renderSortIcon('name')}
                </div>
              </TableHead>
              <TableHead
                className="hover:bg-muted/50 cursor-pointer"
                onClick={() => handleSort('userFullName')}
              >
                <div className="flex items-center">
                  {t('admin.store_management.user', 'Utilisateur')}
                  {renderSortIcon('userFullName')}
                </div>
              </TableHead>
              <TableHead>
                {t('admin.store_management.address', 'Adresse')}
              </TableHead>
              <TableHead className="text-right">
                {t('admin.store_management.actions', 'Actions')}
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoadingStores && (
              <TableRow>
                <TableCell colSpan={4} className="h-24 text-center">
                  <Loader2 className="mx-auto h-6 w-6 animate-spin" />
                </TableCell>
              </TableRow>
            )}
            {!isLoadingStores &&
            processedStores.paginatedStores &&
            processedStores.paginatedStores.length > 0
              ? processedStores.paginatedStores.map(store => (
                  <TableRow key={store.id}>
                    <TableCell className="font-medium">{store.name}</TableCell>
                    <TableCell>
                      {userMap.get(store.userId) || store.userId}
                    </TableCell>
                    <TableCell>
                      {store.address.street}
                      {store.address.street && store.address.cityId ? ', ' : ''}
                      {/* TODO : Récupérer et afficher le nom de la ville au lieu de cityId */}
                      {store.address.cityId || ''}
                    </TableCell>
                    <TableCell className="text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon">
                            <MoreHorizontal className="h-4 w-4" />
                            <span className="sr-only">{t('form.actions')}</span>
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem
                            onClick={() => handleOpenForm(store)}
                          >
                            <Edit className="mr-2 h-4 w-4" />
                            {t('buttons.edit')}
                          </DropdownMenuItem>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem
                            onClick={() => handleDelete(store.id!)}
                            className="text-red-600 focus:bg-red-50 focus:text-red-600 dark:focus:bg-red-700/20"
                          >
                            <Trash2 className="mr-2 h-4 w-4" />
                            {t('buttons.delete')}
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))
              : !isLoadingStores && (
                  <TableRow>
                    <TableCell
                      colSpan={4}
                      className="text-muted-foreground h-24 text-center"
                    >
                      {searchTerm
                        ? t(
                            'admin.store_management.no_stores_match_search',
                            'Aucun magasin ne correspond à votre recherche.'
                          )
                        : t(
                            'admin.store_management.no_stores_found',
                            'Aucun magasin trouvé.'
                          )}
                    </TableCell>
                  </TableRow>
                )}
          </TableBody>
        </Table>
      </div>

      {processedStores.totalPages > 1 && (
        <PaginationControls
          current={currentPage}
          total={processedStores.totalPages}
          onChange={setCurrentPage}
        />
      )}

      <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
        <DialogContent className="max-h-[90vh] w-full max-w-[60vw]! overflow-y-auto">
          <StoreForm
            mode={editingStore ? 'edit' : 'create'}
            existingStore={editingStore}
            users={users || []}
            onSubmitSuccess={handleCloseForm}
            onCancel={handleCloseForm}
          />
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
                'admin.store_management.confirm_delete_description',
                'Cette action est irréversible et supprimera le magasin.'
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setStoreIdToDelete(null)}>
              {t('common.cancel', 'Annuler')}
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              disabled={deleteStore.isPending}
            >
              {deleteStore.isPending && storeIdToDelete !== null ? (
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
