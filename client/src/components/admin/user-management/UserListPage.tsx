import { useState } from 'react'
import {
  listUsersOptions,
  updateUserMutation,
  deleteUserMutation,
  getUserOptions,
  listCooperativesOptions, // Importer listCooperativesOptions
} from '@/api/generated/@tanstack/react-query.gen'
import type { UserDetailDto, UserListDto } from '@/api/generated/types.gen'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu'
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
  MoreHorizontal,
  Search,
  UserCog,
  Trash2,
  CheckCircle2,
  XCircle,
  Loader2,
  ArrowUpDown,
} from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { UserForm } from './UserForm'
import { format } from 'date-fns'
import { toast } from 'sonner'
import PaginationControls from '@/components/PaginationControls'
import { useMemo } from 'react'
import { sortData, type SortConfig } from '@/lib/sorting'
import type { AppUpdateUserDto, AppUserDetailDto } from '@/schemas/api-schemas'

type SortableColumn = keyof Pick<
  UserDetailDto,
  'firstName' | 'email' | 'type' | 'registrationDate' | 'enabled'
>

export function UserListPage() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const [searchTerm, setSearchTerm] = useState('')
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [selectedUserForDelete, setSelectedUserForDelete] =
    useState<UserListDto | null>(null)
  const [editingUserId, setEditingUserId] = useState<number | null>(null)

  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 10

  const [sortColumn, setSortColumn] = useState<SortableColumn | null>(null)
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')

  const {
    data: detailedUserToEdit,
    isLoading: isLoadingUserDetails,
    isError: isErrorUserDetails,
    error: userDetailsError,
  } = useQuery({
    ...getUserOptions({ path: { id: editingUserId! } }),
    enabled: !!editingUserId && isEditDialogOpen,
    staleTime: 5 * 60 * 1000,
  })

  const { data: allUsers, isLoading: isLoadingUsers } = useQuery({
    ...listUsersOptions(),
  })

  // Ajouter la requête pour récupérer toutes les coopératives
  const { data: allCooperatives, isLoading: isLoadingCooperatives } = useQuery(
    listCooperativesOptions()
  )

  const processedUsers = useMemo(() => {
    if (!allUsers) return { paginatedUsers: [], totalPages: 0, totalItems: 0 }

    // 1. Filter users
    const filteredUsers = allUsers.filter(
      user =>
        (user.firstName?.toLowerCase() || '').includes(
          searchTerm.toLowerCase()
        ) ||
        (user.lastName?.toLowerCase() || '').includes(
          searchTerm.toLowerCase()
        ) ||
        (user.email?.toLowerCase() || '').includes(searchTerm.toLowerCase()) ||
        (user.type?.toLowerCase() || '').includes(searchTerm.toLowerCase())
    )

    // 2. Sort users
    const sortConfig: SortConfig<UserListDto> = {
      sortKey: sortColumn,
      sortDirection: sortDirection,
      dateColumns: ['registrationDate'], // Explicitly tell which columns are date strings
    }
    const sortedUsers = sortData(filteredUsers, sortConfig)

    // 3. Paginate users
    const totalPages = Math.ceil(sortedUsers.length / itemsPerPage)
    const startIndex = (currentPage - 1) * itemsPerPage
    const paginatedUsers = sortedUsers.slice(
      startIndex,
      startIndex + itemsPerPage
    )

    return { paginatedUsers, totalPages, totalItems: sortedUsers.length }
  }, [
    allUsers,
    searchTerm,
    sortColumn,
    sortDirection,
    currentPage,
    itemsPerPage,
  ])

  const mutationUpdate = useMutation({
    ...updateUserMutation(),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: listUsersOptions().queryKey })
      queryClient.invalidateQueries({
        queryKey: ['getUser', { path: { id: variables.path.id } }],
      })
      toast.success(t('admin.user_management.toasts.user_updated_success'))
    },
    onError: (error: any) => {
      toast.error(t('common.error'), {
        description:
          error.message || t('admin.user_management.toasts.user_updated_error'),
      })
    },
  })

  const mutationDelete = useMutation({
    ...deleteUserMutation({ path: { id: editingUserId! } }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listUsersOptions().queryKey })
      toast.success(t('admin.user_management.toasts.user_deleted_success'))
      setIsDeleteDialogOpen(false)
      setSelectedUserForDelete(null)
    },
    onError: (error: any) => {
      toast.error(t('common.error'), {
        description:
          error.message || t('admin.user_management.toasts.user_deleted_error'),
      })
    },
  })

  const handleEditUser = (user: UserListDto) => {
    setEditingUserId(user.id) // Set ID to trigger detail fetch
    setIsEditDialogOpen(true)
  }

  const handleDeleteUser = (user: UserListDto) => {
    setSelectedUserForDelete(user)
    setIsDeleteDialogOpen(true)
  }

  const confirmDeleteUser = () => {
    if (selectedUserForDelete) {
      mutationDelete.mutate({ path: { id: selectedUserForDelete.id } })
    }
  }

  const handleToggleUserStatus = (user: UserListDto) => {
    const updatedUserPayload = {
      type: user.type,
      enabled: !user.enabled,
    } as AppUpdateUserDto

    mutationUpdate.mutate({
      path: { id: user.id },
      body: updatedUserPayload,
    })
  }

  const getStatusVariant = (
    user: UserListDto
  ): 'success' | 'secondary' | 'warning' => {
    if (!user.validationDate) return 'warning'
    return user.enabled ? 'success' : 'secondary'
  }

  const getStatusText = (user: UserListDto): string => {
    if (!user.validationDate) return t('status.pending')
    return user.enabled ? t('status.active') : t('status.inactive')
  }

  const handleSort = (column: SortableColumn) => {
    if (sortColumn === column) {
      setSortDirection(prev => (prev === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortColumn(column)
      setSortDirection('asc')
    }
    setCurrentPage(1)
  }

  const renderSortIcon = (column: SortableColumn) => {
    if (sortColumn !== column) {
      return <ArrowUpDown className="text-muted-foreground/50 ml-2 h-3 w-3" />
    }
    return sortDirection === 'asc' ? (
      <ArrowUpDown className="ml-2 h-3 w-3" />
    ) : (
      <ArrowUpDown className="ml-2 h-3 w-3" />
    )
  }

  if (isLoadingUsers || isLoadingCooperatives) {
    // Ajouter isLoadingCooperatives à la condition de chargement
    return (
      <div className="flex items-center justify-center py-10">
        <Loader2 className="text-primary h-8 w-8 animate-spin" />
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-col items-start gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div className="relative w-full max-w-xs">
          {' '}
          {/* Adjusted width */}
          <Search className="text-muted-foreground absolute top-2.5 left-2.5 h-4 w-4" />
          <Input
            type="search"
            placeholder={t('admin.user_management.search_placeholder')}
            className="w-full pl-8"
            value={searchTerm}
            onChange={e => {
              setSearchTerm(e.target.value)
              setCurrentPage(1) // Reset to first page on new search
            }}
          />
        </div>
        <div className="text-muted-foreground text-sm">
          {t('admin.user_management.results_count', {
            count: processedUsers.totalItems,
            total: allUsers?.length || 0,
          })}
        </div>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead
                className="hover:bg-muted/50 cursor-pointer"
                onClick={() => handleSort('firstName')}
              >
                <div className="flex items-center">
                  {t('form.name')}
                  {renderSortIcon('firstName')}
                </div>
              </TableHead>
              <TableHead
                className="hover:bg-muted/50 cursor-pointer"
                onClick={() => handleSort('email')}
              >
                <div className="flex items-center">
                  {t('form.mail')}
                  {renderSortIcon('email')}
                </div>
              </TableHead>
              <TableHead
                className="hover:bg-muted/50 cursor-pointer"
                onClick={() => handleSort('type')}
              >
                <div className="flex items-center">
                  {t('form.type')}
                  {renderSortIcon('type')}
                </div>
              </TableHead>
              <TableHead
                className="hover:bg-muted/50 cursor-pointer"
                onClick={() => handleSort('enabled')}
              >
                <div className="flex items-center">
                  {t('form.status')}
                  {renderSortIcon('enabled')}
                </div>
              </TableHead>
              <TableHead
                className="hover:bg-muted/50 cursor-pointer"
                onClick={() => handleSort('registrationDate')}
              >
                <div className="flex items-center">
                  {t('form.created_at')}
                  {renderSortIcon('registrationDate')}
                </div>
              </TableHead>
              <TableHead className="text-right">{t('form.actions')}</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {processedUsers.paginatedUsers &&
            processedUsers.paginatedUsers.length > 0 ? (
              processedUsers.paginatedUsers.map(user => (
                <TableRow key={user.id}>
                  <TableCell className="font-medium">
                    {user.firstName} {user.lastName}
                  </TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>
                    <Badge variant="outline" className="capitalize">
                      {t(`types.${user.type}` as any)}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <Badge variant={getStatusVariant(user)}>
                      {getStatusText(user)}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    {user.registrationDate
                      ? format(new Date(user.registrationDate), 'PP')
                      : '-'}
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
                        <DropdownMenuItem onClick={() => handleEditUser(user)}>
                          <UserCog className="mr-2 h-4 w-4" />
                          {t('buttons.edit')}
                        </DropdownMenuItem>
                        {user.validationDate && ( // Only show activate/deactivate if user is validated
                          <DropdownMenuItem
                            onClick={() => handleToggleUserStatus(user)}
                          >
                            {user.enabled ? (
                              <XCircle className="mr-2 h-4 w-4" />
                            ) : (
                              <CheckCircle2 className="mr-2 h-4 w-4" />
                            )}
                            {user.enabled
                              ? t('buttons.deactivate')
                              : t('buttons.activate')}
                          </DropdownMenuItem>
                        )}
                        <DropdownMenuSeparator />
                        <DropdownMenuItem
                          onClick={() => handleDeleteUser(user)}
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
            ) : (
              <TableRow>
                <TableCell
                  colSpan={6}
                  className="text-muted-foreground h-24 text-center"
                >
                  {searchTerm
                    ? t('admin.user_management.no_users_match_search')
                    : t('admin.user_management.no_users_found')}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      {processedUsers.totalPages > 1 && (
        <PaginationControls
          current={currentPage}
          total={processedUsers.totalPages}
          onChange={setCurrentPage}
        />
      )}
      {/* Edit User Dialog */}
      <Dialog
        open={isEditDialogOpen}
        onOpenChange={isOpen => {
          setIsEditDialogOpen(isOpen)
          if (!isOpen) {
            setEditingUserId(null) // Reset on close
          }
        }}
      >
        <DialogContent className="max-h-[90vh] w-full max-w-[80vw]! overflow-y-auto">
          {(isLoadingUserDetails || isLoadingCooperatives) && ( // Vérifier aussi isLoadingCooperatives
            <div className="flex h-48 items-center justify-center">
              <Loader2 className="text-primary h-8 w-8 animate-spin" />
            </div>
          )}
          {isErrorUserDetails && (
            <div className="text-destructive-foreground bg-destructive rounded-md p-4">
              <p>{t('common.error_loading_data')}</p>
              <p className="text-sm">{userDetailsError?.message}</p>
            </div>
          )}
          {!isLoadingUserDetails &&
            !isLoadingCooperatives && // S'assurer que les coopératives sont chargées
            !isErrorUserDetails &&
            detailedUserToEdit &&
            allCooperatives && ( // S'assurer que allCooperatives est disponible
              <UserForm
                existingUser={detailedUserToEdit as AppUserDetailDto}
                onSubmitSuccess={() => {
                  setIsEditDialogOpen(false)
                  setEditingUserId(null)
                }}
                onCancel={() => {
                  setIsEditDialogOpen(false)
                  setEditingUserId(null)
                }}
                formTitle={t('admin.user_management.edit_dialog.title')}
                formDescription={t(
                  'admin.user_management.edit_dialog.description'
                )}
                submitButtonText={t('buttons.save_changes')}
                allCooperatives={allCooperatives} // Passer les coopératives ici
              />
            )}
        </DialogContent>
      </Dialog>

      {/* Delete User Confirmation Dialog */}
      <AlertDialog
        open={isDeleteDialogOpen}
        onOpenChange={setIsDeleteDialogOpen}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>
              {t('admin.user_management.delete_dialog.title')}
            </AlertDialogTitle>
            <AlertDialogDescription>
              {t('admin.user_management.delete_dialog.description', {
                userName: selectedUserForDelete
                  ? `${selectedUserForDelete.firstName} ${selectedUserForDelete.lastName}`
                  : t('common.this_user'),
              })}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{t('buttons.cancel')}</AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmDeleteUser}
              className="bg-red-600 hover:bg-red-700"
              disabled={mutationDelete.isPending}
            >
              {mutationDelete.isPending
                ? t('buttons.deleting')
                : t('buttons.delete')}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
