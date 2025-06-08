import React, { useMemo, useState, useCallback } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  listCooperativesOptions,
  listUsersOptions,
  deleteCooperativeMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import type {
  CooperativeDto,
  ProducerListDto,
  UserListDto,
} from '@/api/generated/types.gen'

import { Button } from '@/components/ui/button'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
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
  Loader2,
  PlusCircle,
  Trash2,
  Edit,
  Users,
  AlertCircle,
  Search,
  ArrowUpDown,
} from 'lucide-react'
import { toast } from 'sonner'
import { CooperativeMembersDialog } from './CooperativeMembersDialog'
import PaginationControls from '@/components/PaginationControls'
import { sortData, type SortConfig } from '@/lib/sorting'
import { useTranslation } from 'react-i18next'
import { format } from 'date-fns'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog'
import { CooperativeForm } from './CooperativeForm'

type SortableCooperativeColumn =
  | keyof Pick<CooperativeDto, 'name' | 'creationDate'>
  | 'presidentName'
  | 'membersCounts'

/**
 * Composant pour afficher la liste des coopératives
 */
export const CooperativeListPage: React.FC = () => {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const [selectedCooperativeForMembers, setSelectedCooperativeForMembers] =
    useState<CooperativeDto | null>(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 10
  const [sortColumn, setSortColumn] =
    useState<SortableCooperativeColumn | null>('name')
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')

  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [editingCooperative, setEditingCooperative] =
    useState<CooperativeDto | null>(null)

  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)

  const [isConfirmDeleteDialogOpen, setIsConfirmDeleteDialogOpen] =
    useState(false)
  const [cooperativeIdToDelete, setCooperativeIdToDelete] = useState<
    number | null
  >(null)

  const cooperativesQuery = useQuery(listCooperativesOptions())
  const usersQuery = useQuery(listUsersOptions())

  const deleteMutation = useMutation({
    ...deleteCooperativeMutation(),
    onSuccess: () => {
      toast.success(t('admin.cooperative_management.toasts.deleted_success'))
      queryClient.invalidateQueries({
        queryKey: listCooperativesOptions().queryKey,
      })
      queryClient.invalidateQueries({ queryKey: listUsersOptions().queryKey })
      setCooperativeIdToDelete(null)
    },
    onError: error => {
      toast.error(t('common.error_general'), {
        description:
          error.errors.map(e => e.message).join(' ,') ||
          t('admin.cooperative_management.toasts.deleted_error'),
      })
      setCooperativeIdToDelete(null)
    },
  })

  const usersMap = useMemo(() => {
    if (!usersQuery.data) return new Map<number, UserListDto>()
    return new Map(usersQuery.data.map(user => [user.id, user]))
  }, [usersQuery.data])

  const getPresidentName = useCallback(
    (presidentId: number | null | undefined) => {
      if (!presidentId) return t('common.not_applicable_short')
      const president = usersMap.get(presidentId)
      return president
        ? `${president.firstName} ${president.lastName}`
        : t('common.unknown')
    },
    [usersMap, t]
  )

  const getMemberCounts = useCallback(
    (cooperativeId: number, users: UserListDto[]) => {
      return users.filter(
        user =>
          user.type === 'producer' &&
          (user as ProducerListDto).cooperativeId === cooperativeId
      ).length
    },
    []
  )

  const processedCooperatives = useMemo(() => {
    if (!cooperativesQuery.data)
      return { paginatedCooperatives: [], totalPages: 0, totalItems: 0 }

    // 1. Enrichir le DTO (nom du président, nombre de membres)
    const enrichedCooperatives = cooperativesQuery.data.map(coop => ({
      ...coop,
      presidentName: getPresidentName(coop.presidentId),
      membersCounts: getMemberCounts(coop.id, usersQuery.data || []),
    }))

    // 2. Filter cooperatives
    const filteredCooperatives = enrichedCooperatives.filter(
      coop =>
        coop.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        getPresidentName(coop.presidentId)
          .toLowerCase()
          .includes(searchTerm.toLowerCase())
    )

    // 3. Sort cooperatives
    const sortConfig: SortConfig<(typeof enrichedCooperatives)[0]> = {
      sortKey: sortColumn,
      sortDirection: sortDirection,
      dateColumns: ['creationDate'],
    }
    const sortedCooperatives = sortData(filteredCooperatives, sortConfig)

    // 4. Paginate cooperatives
    const totalPages = Math.ceil(sortedCooperatives.length / itemsPerPage)
    const startIndex = (currentPage - 1) * itemsPerPage
    const paginatedCooperatives = sortedCooperatives.slice(
      startIndex,
      startIndex + itemsPerPage
    )

    return {
      paginatedCooperatives,
      totalPages,
      totalItems: sortedCooperatives.length,
    }
  }, [
    cooperativesQuery.data,
    searchTerm,
    sortColumn,
    sortDirection,
    currentPage,
    itemsPerPage,
    getPresidentName,
    getMemberCounts,
    usersQuery.data,
  ])

  const handleDelete = (id: number) => {
    setCooperativeIdToDelete(id)
    setIsConfirmDeleteDialogOpen(true)
  }

  const confirmDelete = () => {
    if (cooperativeIdToDelete !== null) {
      deleteMutation.mutate({ path: { id: cooperativeIdToDelete } })
    }
    setIsConfirmDeleteDialogOpen(false)
  }

  const handleOpenMembersDialog = (cooperative: CooperativeDto) => {
    setSelectedCooperativeForMembers(cooperative)
  }

  const handleCloseMembersDialog = () => {
    setSelectedCooperativeForMembers(null)
  }

  const handleEditCooperative = (cooperative: CooperativeDto) => {
    setEditingCooperative(cooperative)
    setIsEditDialogOpen(true)
  }

  const handleSort = (column: SortableCooperativeColumn) => {
    if (sortColumn === column) {
      setSortDirection(prev => (prev === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortColumn(column)
      setSortDirection('asc')
    }
    setCurrentPage(1)
  }

  const renderSortIcon = (column: SortableCooperativeColumn) => {
    if (sortColumn !== column) {
      return <ArrowUpDown className="text-muted-foreground/50 ml-2 h-3 w-3" />
    }
    return <ArrowUpDown className="ml-2 h-3 w-3" />
  }

  if (cooperativesQuery.isLoading || usersQuery.isLoading) {
    return (
      <div className="flex min-h-[200px] items-center justify-center">
        <Loader2 className="text-primary h-8 w-8 animate-spin" />
      </div>
    )
  }

  return (
    <>
      <div className="m-4 space-y-4">
        {/* Search and Add Button Row */}
        <div className="flex flex-col items-start gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div className="relative w-full max-w-xs">
            <Search className="text-muted-foreground absolute top-2.5 left-2.5 h-4 w-4" />
            <Input
              type="search"
              placeholder={t('admin.cooperative_management.search_placeholder')}
              className="w-full pl-8"
              value={searchTerm}
              onChange={e => {
                setSearchTerm(e.target.value)
                setCurrentPage(1)
              }}
            />
          </div>
          <Button onClick={() => setIsCreateDialogOpen(true)}>
            <PlusCircle className="mr-2 h-4 w-4" />
            {t('admin.cooperative_management.add_button')}
          </Button>
        </div>
        {/* Results Count */}
        <div className="text-muted-foreground text-sm">
          {t('admin.user_management.results_count', {
            count: processedCooperatives.totalItems,
            total: cooperativesQuery.data?.length || 0,
          })}
        </div>
        {/* Error Alerts */}
        {cooperativesQuery.isError && (
          <Alert variant="destructive" className="mb-4">
            <AlertCircle className="h-4 w-4" />
            <AlertTitle>{t('common.error_loading_data_title')}</AlertTitle>
            <AlertDescription>
              {cooperativesQuery.error?.message || t('common.error_occurred')}
            </AlertDescription>
          </Alert>
        )}
        {usersQuery.isError && (
          <Alert variant="destructive" className="mb-4">
            <AlertCircle className="h-4 w-4" />
            <AlertTitle>{t('common.error_loading_users_title')}</AlertTitle>
            <AlertDescription>
              {usersQuery.error?.message || t('common.error_occurred')}
            </AlertDescription>
          </Alert>
        )}
        {/* Table */}
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead
                  className="hover:bg-muted/50 cursor-pointer"
                  onClick={() => handleSort('name')}
                >
                  <div className="flex items-center">
                    {t('form.name')}
                    {renderSortIcon('name')}
                  </div>
                </TableHead>
                <TableHead
                  className="hover:bg-muted/50 cursor-pointer"
                  onClick={() => handleSort('creationDate')}
                >
                  <div className="flex items-center">
                    {t('form.creation_date')}
                    {renderSortIcon('creationDate')}
                  </div>
                </TableHead>
                <TableHead
                  className="hover:bg-muted/50 cursor-pointer"
                  onClick={() => handleSort('presidentName')}
                >
                  <div className="flex items-center">
                    {t('form.president')}
                    {renderSortIcon('presidentName')}
                  </div>
                </TableHead>
                <TableHead
                  className="hover:bg-muted/50 cursor-pointer text-center"
                  onClick={() => handleSort('membersCounts')}
                >
                  <div className="flex items-center justify-center">
                    {t('admin.cooperative_management.members_count_column')}
                    {renderSortIcon('membersCounts')}
                  </div>
                </TableHead>
                <TableHead className="text-right">
                  {t('form.actions')}
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {processedCooperatives.paginatedCooperatives.length > 0 ? (
                processedCooperatives.paginatedCooperatives.map(cooperative => (
                  <TableRow key={cooperative.id}>
                    <TableCell className="font-medium">
                      {cooperative.name}
                    </TableCell>
                    <TableCell>
                      {format(new Date(cooperative.creationDate), 'PP')}
                    </TableCell>
                    <TableCell>
                      {getPresidentName(cooperative.presidentId)}
                    </TableCell>
                    <TableCell className="text-center">
                      {cooperative.membersCounts}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleOpenMembersDialog(cooperative)}
                        title={t(
                          'admin.cooperative_management.manage_members_button_title'
                        )}
                      >
                        <Users className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleEditCooperative(cooperative)}
                        title={t('buttons.edit')}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleDelete(cooperative.id)}
                        disabled={deleteMutation.isPending}
                        title={t('buttons.delete')}
                        className="text-destructive hover:text-destructive"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    colSpan={5}
                    className="text-muted-foreground h-24 text-center"
                  >
                    {searchTerm
                      ? t(
                          'admin.cooperative_management.no_cooperatives_match_search'
                        )
                      : t('admin.cooperative_management.no_cooperatives_found')}
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
        {processedCooperatives.totalPages > 1 && (
          <PaginationControls
            current={currentPage}
            total={processedCooperatives.totalPages}
            onChange={setCurrentPage}
          />
        )}
      </div>{' '}
      {selectedCooperativeForMembers && (
        <CooperativeMembersDialog
          open={!!selectedCooperativeForMembers}
          onClose={handleCloseMembersDialog}
          cooperative={selectedCooperativeForMembers}
          allCooperatives={cooperativesQuery.data}
        />
      )}
      {editingCooperative && (
        <Dialog
          open={isEditDialogOpen}
          onOpenChange={isOpen => {
            setIsEditDialogOpen(isOpen)
            if (!isOpen) {
              setEditingCooperative(null)
            }
          }}
        >
          <DialogContent className="max-h-[90vh] w-full overflow-y-auto sm:max-w-2xl">
            <DialogHeader>
              <DialogTitle>
                {t('admin.cooperative_management.edit_dialog.title')}
              </DialogTitle>
              <DialogDescription>
                {t('admin.cooperative_management.edit_dialog.description')}
              </DialogDescription>
            </DialogHeader>
            <CooperativeForm
              isEditMode={true}
              cooperativeIdToEdit={editingCooperative.id}
              allCooperatives={cooperativesQuery.data}
              onSuccess={() => {
                setIsEditDialogOpen(false)
                setEditingCooperative(null)
              }}
              onCancel={() => {
                setIsEditDialogOpen(false)
                setEditingCooperative(null)
              }}
            />
          </DialogContent>
        </Dialog>
      )}
      {/* Nouvelle Coopérative Dialog */}
      {isCreateDialogOpen && (
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogContent className="max-h-[90vh] w-full max-w-2xl overflow-y-auto sm:max-w-xl">
            <DialogHeader>
              <DialogTitle>
                {t('admin.cooperative_management.create_dialog.title')}
              </DialogTitle>
              <DialogDescription>
                {t('admin.cooperative_management.create_dialog.description')}
              </DialogDescription>
            </DialogHeader>
            <CooperativeForm
              allCooperatives={cooperativesQuery.data} // Pass all cooperatives
              onSuccess={() => {
                setIsCreateDialogOpen(false)
              }}
              onCancel={() => {
                setIsCreateDialogOpen(false)
              }}
            />
          </DialogContent>
        </Dialog>
      )}
      <AlertDialog
        open={isConfirmDeleteDialogOpen}
        onOpenChange={setIsConfirmDeleteDialogOpen}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>
              {t('admin.cooperative_management.delete_dialog.title')}
            </AlertDialogTitle>
            <AlertDialogDescription>
              {t(
                'admin.cooperative_management.delete_dialog.description_confirm'
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel
              onClick={() => setCooperativeIdToDelete(null)}
              disabled={deleteMutation.isPending}
            >
              {t('buttons.cancel')}
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDelete}
              disabled={deleteMutation.isPending}
              className="bg-destructive hover:bg-destructive/90 text-white"
            >
              {deleteMutation.isPending ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                t('buttons.delete')
              )}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}
