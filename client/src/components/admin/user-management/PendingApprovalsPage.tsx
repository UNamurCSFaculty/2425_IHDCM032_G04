import { useState, useMemo } from 'react'
import {
  listUsersOptions,
  updateUserMutation,
  deleteUserMutation,
  getUserOptions,
  listUsersQueryKey,
  getUserQueryKey,
} from '@/api/generated/@tanstack/react-query.gen'
import type { UserDetailDto, UserListDto } from '@/api/generated/types.gen'
import type { AppUpdateUserDto, AppUserDetailDto } from '@/schemas/api-schemas'
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
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogClose,
} from '@/components/ui/dialog'
import { ScrollArea } from '@/components/ui/scroll-area'
import {
  Eye,
  CheckCircle,
  XCircle,
  Loader2,
  CheckCircle2,
  ArrowUpDown,
  Search,
} from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { format } from 'date-fns'
import { toast } from 'sonner'

import { downloadDocument } from '@/api/generated'
import PaginationControls from '@/components/PaginationControls'
import { Input } from '@/components/ui/input'
import { UserDetailsDisplay } from './UserDetailsDisplay'

type SortableColumnPending = keyof Pick<
  UserDetailDto,
  'firstName' | 'email' | 'type' | 'registrationDate'
>

export function PendingApprovalsPage() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const [userForReviewId, setUserForReviewId] = useState<number | null>(null)
  const [isReviewDialogOpen, setIsReviewDialogOpen] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [downloadingDocIds, setDownloadingDocIds] = useState<
    Record<number, boolean>
  >({})
  const [viewingDocIds, setViewingDocIds] = useState<Record<number, boolean>>(
    {}
  )

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 10

  // Sorting state
  const [sortColumn, setSortColumn] = useState<SortableColumnPending | null>(
    null
  )
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')

  const { data: allPendingUsersInitial, isLoading: isLoadingPending } =
    useQuery({
      ...listUsersOptions(),
      select: data => data.filter(user => user.enabled === false),
    })

  const processedPendingUsers = useMemo(() => {
    if (!allPendingUsersInitial)
      return { paginatedUsers: [], totalPages: 0, totalItems: 0 }

    // 1. Filter users based on searchTerm
    let filteredUsers = allPendingUsersInitial.filter(
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
    if (sortColumn) {
      filteredUsers = [...filteredUsers].sort((a, b) => {
        const valA = a[sortColumn]
        const valB = b[sortColumn]

        let comparison = 0
        if (valA === null || valA === undefined) comparison = -1
        else if (valB === null || valB === undefined) comparison = 1
        else if (typeof valA === 'string' && typeof valB === 'string') {
          comparison = valA.localeCompare(valB)
        }
        // Handle registrationDate (which is string but represents date)
        else if (sortColumn === 'registrationDate') {
          const dateA = valA ? new Date(valA as string).getTime() : 0
          const dateB = valB ? new Date(valB as string).getTime() : 0
          comparison = dateA - dateB
        }

        return sortDirection === 'asc' ? comparison : comparison * -1
      })
    }

    // Paginate users
    const totalItems = filteredUsers.length
    const totalPages = Math.ceil(totalItems / itemsPerPage)
    const startIndex = (currentPage - 1) * itemsPerPage
    const paginatedUsers = filteredUsers.slice(
      startIndex,
      startIndex + itemsPerPage
    )

    return { paginatedUsers, totalPages, totalItems }
  }, [
    allPendingUsersInitial,
    searchTerm,
    sortColumn,
    sortDirection,
    currentPage,
    itemsPerPage,
  ])

  // Query to fetch detailed user for review
  const {
    data: detailedUserForReview,
    isLoading: isLoadingUserDetailsReview,
    isError: isErrorUserDetailsReview,
    error: userDetailsReviewError,
  } = useQuery({
    ...getUserOptions({ path: { id: userForReviewId! } }),
    enabled: !!userForReviewId && isReviewDialogOpen,
    staleTime: 5 * 60 * 1000, // Cache for 5 minutes
  })

  const mutationApprove = useMutation({
    ...updateUserMutation(),
    onSuccess: updatedUser => {
      queryClient.invalidateQueries({ queryKey: listUsersQueryKey() })
      queryClient.invalidateQueries({
        queryKey: getUserQueryKey({ path: { id: updatedUser.id } }),
      })
      toast.success(t('admin.user_management.toasts.user_approved_success'))
      setIsReviewDialogOpen(false)
      setUserForReviewId(null)
    },
    onError: error => {
      toast.error(t('common.error_general'), {
        description:
          error.errors.map(e => e.message).join(' ,') ||
          t('admin.user_management.toasts.user_approved_error'),
      })
    },
  })

  const mutationReject = useMutation({
    ...deleteUserMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listUsersQueryKey() })

      toast.success(t('admin.user_management.toasts.user_rejected_success'))
      setIsReviewDialogOpen(false)
      setUserForReviewId(null)
    },
    onError: (error: any) => {
      toast.error(t('common.error_general'), {
        description:
          error.message ||
          t('admin.user_management.toasts.user_rejected_error'),
      })
    },
  })

  const handleReviewUser = (user: UserListDto) => {
    setUserForReviewId(user.id)
    setIsReviewDialogOpen(true)
  }

  const handleApprove = () => {
    if (detailedUserForReview) {
      mutationApprove.mutate({
        path: { id: detailedUserForReview!.id },
        body: { ...detailedUserForReview, enabled: true } as AppUpdateUserDto,
      })
    }
  }

  const handleReject = () => {
    if (detailedUserForReview) {
      mutationReject.mutate({
        path: { id: detailedUserForReview.id },
      })
    }
  }

  const handleSort = (column: SortableColumnPending) => {
    if (sortColumn === column) {
      setSortDirection(prev => (prev === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortColumn(column)
      setSortDirection('asc')
    }
    setCurrentPage(1)
  }

  const renderSortIcon = (column: SortableColumnPending) => {
    if (sortColumn !== column) {
      return <ArrowUpDown className="text-muted-foreground/50 ml-2 h-3 w-3" />
    }
    return sortDirection === 'asc' ? (
      <ArrowUpDown className="ml-2 h-3 w-3" />
    ) : (
      <ArrowUpDown className="ml-2 h-3 w-3" />
    )
  }

  const triggerBlobDownload = (blob: Blob, filename: string) => {
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  }

  const getActualBlob = (response: any): Blob | null => {
    if (response instanceof Blob) {
      return response
    }
    if (
      response &&
      typeof response === 'object' &&
      'data' in response &&
      response.data instanceof Blob
    ) {
      return response.data
    }
    return null
  }

  const handleDownloadDocument = async (docId: number, docName: string) => {
    setDownloadingDocIds(prev => ({ ...prev, [docId]: true }))
    try {
      const response = await downloadDocument({ path: { id: docId } })
      const actualBlob = getActualBlob(response)

      if (actualBlob) {
        triggerBlobDownload(actualBlob, docName)
      } else {
        console.error(
          'Expected a Blob response for download, but received:',
          response
        )
        toast.error(t('common.download_error'), {
          description: t(
            'admin.user_management.toasts.invalid_file_format_received'
          ),
        })
      }
    } catch (error: any) {
      console.error('Download error:', error)
      toast.error(t('common.download_error'), {
        description: error?.message || t('common.unknown_error'),
      })
    } finally {
      setDownloadingDocIds(prev => ({ ...prev, [docId]: false }))
    }
  }

  const handleViewDocument = async (docId: number) => {
    setViewingDocIds(prev => ({ ...prev, [docId]: true }))
    try {
      const response = await downloadDocument({ path: { id: docId } }) // Assuming doc.id is number
      const actualBlob = getActualBlob(response)

      if (actualBlob) {
        const fileURL = URL.createObjectURL(actualBlob)
        const newWindow = window.open(fileURL, '_blank')
        if (!newWindow) {
          toast.error(t('common.error_general'), {
            description: t('common.popup_blocker_error_description'),
          })
        }
      } else {
        console.error(
          'Expected a Blob response for viewing, but received:',
          response
        )
        toast.error(t('common.error_general'), {
          description: t(
            'admin.user_management.toasts.invalid_file_format_received'
          ),
        })
      }
    } catch (error: any) {
      console.error('View document error:', error)
      toast.error(t('common.download_error'), {
        description: error?.message || t('common.unknown_error'),
      })
    } finally {
      setViewingDocIds(prev => ({ ...prev, [docId]: false }))
    }
  }

  if (isLoadingPending) {
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
          {/* This count will now reflect filtered results if searchTerm is active */}
          {t('admin.user_management.results_count', {
            count: processedPendingUsers.totalItems,
            total: allPendingUsersInitial?.length || 0,
          })}
        </div>
      </div>

      {processedPendingUsers.totalItems === 0 && searchTerm ? (
        <div className="flex flex-col items-center justify-center rounded-md border border-dashed p-8 text-center">
          <Search className="text-primary mb-4 h-10 w-10" />{' '}
          {/* Different icon for no search results */}
          <h2 className="text-xl font-semibold tracking-tight">
            {t('admin.user_management.no_users_match_search_pending.title')}
          </h2>
          <p className="text-muted-foreground text-sm">
            {t(
              'admin.user_management.no_users_match_search_pending.description'
            )}
          </p>
        </div>
      ) : processedPendingUsers.totalItems === 0 && !searchTerm ? (
        <div className="flex flex-col items-center justify-center rounded-md border border-dashed p-8 text-center">
          <div className="bg-primary/10 mb-4 flex h-20 w-20 items-center justify-center rounded-full">
            <CheckCircle2 className="text-primary h-10 w-10" />
          </div>
          <h2 className="text-xl font-semibold tracking-tight">
            {t('admin.user_management.no_pending_approvals.title')}
          </h2>
          <p className="text-muted-foreground text-sm">
            {t('admin.user_management.no_pending_approvals.description')}
          </p>
        </div>
      ) : (
        <>
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
                    onClick={() => handleSort('registrationDate')}
                  >
                    <div className="flex items-center">
                      {t('form.submitted_at')}
                      {renderSortIcon('registrationDate')}
                    </div>
                  </TableHead>
                  <TableHead className="text-right">
                    {t('form.actions')}
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {processedPendingUsers.paginatedUsers?.map(user => (
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
                      {user.registrationDate
                        ? format(new Date(user.registrationDate), 'PPp')
                        : '-'}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleReviewUser(user)}
                      >
                        <Eye className="mr-2 h-4 w-4" />
                        {t('buttons.review')}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
          {processedPendingUsers.totalPages > 1 && (
            <PaginationControls
              current={currentPage}
              total={processedPendingUsers.totalPages}
              onChange={setCurrentPage}
            />
          )}
        </>
      )}

      {/* Review User Dialog */}
      <Dialog
        open={isReviewDialogOpen}
        onOpenChange={isOpen => {
          setIsReviewDialogOpen(isOpen)
          if (!isOpen) {
            setUserForReviewId(null) // Reset on close
          }
        }}
      >
        <DialogContent className="max-h-[90vh] w-full max-w-[80vw]! overflow-y-auto">
          {' '}
          {/* Changed from max-w-2xl to max-w-4xl */}
          {isLoadingUserDetailsReview && (
            <div className="flex h-96 items-center justify-center">
              <Loader2 className="text-primary h-8 w-8 animate-spin" />
            </div>
          )}
          {isErrorUserDetailsReview && (
            <div className="text-destructive-foreground bg-destructive rounded-md p-4">
              <p>{t('common.error_loading_data')}</p>
              <p className="text-sm">{userDetailsReviewError?.message}</p>
            </div>
          )}
          {!isLoadingUserDetailsReview &&
            !isErrorUserDetailsReview &&
            detailedUserForReview && (
              <>
                <DialogHeader>
                  <DialogTitle>
                    {t('admin.user_management.review_dialog.title')}
                  </DialogTitle>
                  <DialogDescription>
                    {t('admin.user_management.review_dialog.description', {
                      userName: `${detailedUserForReview.firstName} ${detailedUserForReview.lastName}`,
                    })}
                  </DialogDescription>
                </DialogHeader>
                <ScrollArea className="max-h-[60vh] p-1">
                  <UserDetailsDisplay
                    user={detailedUserForReview as AppUserDetailDto}
                    onDownloadDocument={handleDownloadDocument} // Passez les gestionnaires
                    onViewDocument={handleViewDocument} // et les états
                    downloadingDocIds={downloadingDocIds}
                    viewingDocIds={viewingDocIds}
                  />
                </ScrollArea>
                <DialogFooter className="pt-4">
                  <DialogClose asChild>
                    <Button variant="outline">{t('buttons.close')}</Button>
                  </DialogClose>
                  <Button
                    variant="destructive"
                    onClick={handleReject}
                    disabled={
                      mutationReject.isPending || mutationApprove.isPending
                    }
                  >
                    <XCircle className="mr-2 h-4 w-4" />
                    {mutationReject.isPending
                      ? t('buttons.rejecting')
                      : t('buttons.reject')}
                  </Button>
                  <Button
                    onClick={handleApprove}
                    disabled={
                      mutationApprove.isPending || mutationReject.isPending
                    }
                    className="bg-green-600 text-white hover:bg-green-700"
                  >
                    <CheckCircle className="mr-2 h-4 w-4" />
                    {mutationApprove.isPending
                      ? t('buttons.approving')
                      : t('buttons.approve')}
                  </Button>
                </DialogFooter>
              </>
            )}
        </DialogContent>
      </Dialog>
    </div>
  )
}
