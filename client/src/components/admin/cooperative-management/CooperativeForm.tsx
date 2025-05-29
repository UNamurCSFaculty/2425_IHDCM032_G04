import React from 'react'
import * as z from 'zod/v4'
import { useNavigate } from '@tanstack/react-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  getCooperativeOptions,
  listUsersOptions,
  createCooperativeMutation,
  updateCooperativeMutation,
  listCooperativesOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import type { CooperativeUpdateDto } from '@/api/generated/types.gen'

import { Button } from '@/components/ui/button'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardFooter,
} from '@/components/ui/card'
import { Loader2, AlertCircle } from 'lucide-react'
import { useAppForm } from '@/components/form'
import { toast } from 'sonner'
import { useTranslation } from 'react-i18next'

const cooperativeSchema = z.object({
  name: z.string().min(3, 'Le nom doit contenir au moins 3 caractères.'),
  creationDate: z
    .string()
    .refine(
      date => !isNaN(Date.parse(date)) && /^\d{4}-\d{2}-\d{2}$/.test(date),
      {
        message: 'Date de création invalide. Utilisez le format AAAA-MM-JJ.',
      }
    ),
  presidentId: z.number().min(1, 'Le président est requis.'),
})

interface CooperativeFormProps {
  isEditMode?: boolean
  cooperativeIdToEdit?: number
  onSuccess?: () => void // Callback pour succès (utilisé par la Dialog)
  onCancel?: () => void // Callback pour annulation (utilisé par la Dialog)
}

export const CooperativeForm: React.FC<CooperativeFormProps> = ({
  isEditMode,
  cooperativeIdToEdit,
  onSuccess,
  onCancel,
}) => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const cooperativeId =
    isEditMode && cooperativeIdToEdit ? cooperativeIdToEdit : undefined

  const usersQuery = useQuery(listUsersOptions())

  const cooperativeQuery = useQuery({
    ...getCooperativeOptions({ path: { id: cooperativeId! } }),
    enabled: !!cooperativeId && isEditMode,
  })

  const form = useAppForm({
    defaultValues: {
      name: '',
      creationDate: new Date().toISOString().split('T')[0],
      presidentId: 0,
    },
    onSubmit: async ({ value }) => {
      const payload: CooperativeUpdateDto = {
        name: value.name,
        creationDate: new Date(value.creationDate).toISOString(),
        presidentId: value.presidentId,
      }

      if (isEditMode && cooperativeId) {
        await updateMutation.mutateAsync({
          path: { id: cooperativeId },
          body: payload,
        })
      } else {
        await createMutation.mutateAsync({ body: payload })
      }
    },
    validators: {
      onChange: cooperativeSchema,
    },
  })

  React.useEffect(() => {
    if (isEditMode && cooperativeQuery.data) {
      form.reset({
        name: cooperativeQuery.data.name,
        creationDate: new Date(cooperativeQuery.data.creationDate)
          .toISOString()
          .split('T')[0],
        presidentId: cooperativeQuery.data.presidentId,
      })
    } else if (!isEditMode) {
      form.reset({
        name: '',
        creationDate: new Date().toISOString().split('T')[0],
        presidentId: 0,
      })
    }
  }, [isEditMode, cooperativeQuery.data, form])

  const createMutationHook = useMutation({
    ...createCooperativeMutation(),
    onSuccess: () => {
      toast.success(t('admin.cooperative_management.toasts.created_success'))
      queryClient.invalidateQueries({
        queryKey: listCooperativesOptions().queryKey,
      })
      if (onSuccess) {
        onSuccess()
      } else {
        navigate({ to: '/admin/cooperatives' })
      }
    },
    onError: (error: any) => {
      toast.error(
        t('admin.cooperative_management.toasts.created_error_title'),
        {
          description:
            error.message ||
            t('admin.cooperative_management.toasts.created_error_description'),
        }
      )
    },
  })
  const updateMutationHook = useMutation({
    ...updateCooperativeMutation(),
    onSuccess: () => {
      toast.success(t('admin.cooperative_management.toasts.updated_success'))
      queryClient.invalidateQueries({
        queryKey: listCooperativesOptions().queryKey,
      })
      if (cooperativeId) {
        queryClient.invalidateQueries({
          queryKey: getCooperativeOptions({ path: { id: cooperativeId } })
            .queryKey,
        })
      }
      if (onSuccess) {
        onSuccess()
      }
    },
    onError: (error: any) => {
      toast.error(
        t('admin.cooperative_management.toasts.updated_error_title'),
        {
          description:
            error.message ||
            t('admin.cooperative_management.toasts.updated_error_description'),
        }
      )
    },
  })

  const createMutation = createMutationHook
  const updateMutation = updateMutationHook

  const isLoadingSubmit =
    form.state.isSubmitting ||
    createMutation.isPending ||
    updateMutation.isPending
  const isLoadingData =
    (isEditMode && cooperativeQuery.isLoading) || usersQuery.isLoading

  if (isLoadingData) {
    return (
      <div className="flex min-h-[200px] items-center justify-center">
        <Loader2 className="text-primary h-8 w-8 animate-spin" />
      </div>
    )
  }

  const presidentOptions = usersQuery.data
    ? usersQuery.data.map(user => ({
        id: user.id,
        label: `${user.firstName} ${user.lastName} (${user.email}) - Type: ${user.type}`,
      }))
    : []

  return (
    <div className={onSuccess || onCancel ? '' : 'm-4'}>
      {' '}
      <Card className={onSuccess || onCancel ? 'border-0 shadow-none' : ''}>
        {' '}
        {!onSuccess && !onCancel && (
          <CardHeader>
            <CardTitle>
              {isEditMode
                ? t('admin.cooperative_management.form.edit_title')
                : t('admin.cooperative_management.form.add_title')}
            </CardTitle>
          </CardHeader>
        )}
        <CardContent className={onSuccess || onCancel ? 'p-0' : ''}>
          {usersQuery.isError && (
            <Alert variant="destructive" className="mb-4">
              <AlertCircle className="h-4 w-4" />
              <AlertTitle>{t('common.error_loading_users_title')}</AlertTitle>
              <AlertDescription>
                {usersQuery.error?.message || t('common.unknown_error')}
                <Button
                  variant="link"
                  size="sm"
                  onClick={() => usersQuery.refetch()}
                  className="ml-2 h-auto p-0"
                >
                  {t('buttons.retry')}
                </Button>
              </AlertDescription>
            </Alert>
          )}
          {isEditMode && cooperativeQuery.isError && (
            <Alert variant="destructive" className="mb-4">
              <AlertCircle className="h-4 w-4" />
              <AlertTitle>
                {t(
                  'admin.cooperative_management.form.error_loading_cooperative_title'
                )}
              </AlertTitle>
              <AlertDescription>
                {cooperativeQuery.error?.message ||
                  t(
                    'admin.cooperative_management.form.error_cooperative_not_found'
                  )}
              </AlertDescription>
            </Alert>
          )}

          <form
            onSubmit={e => {
              e.preventDefault()
              e.stopPropagation()
              form.handleSubmit()
            }}
            className="space-y-6"
          >
            <form.AppField name="name">
              {f => (
                <f.TextField
                  label={t('form.cooperative_name')}
                  placeholder={t('form.placeholder.cooperative_name')}
                  disabled={isLoadingSubmit}
                />
              )}
            </form.AppField>

            <form.AppField name="creationDate">
              {f => (
                <f.TextField
                  label={t('form.creation_date')}
                  type="date"
                  disabled={true}
                />
              )}
            </form.AppField>

            <form.AppField name="presidentId">
              {f => (
                <f.VirtualizedSelectField
                  label={t('form.president')}
                  placeholder={t('form.placeholder.select_president')}
                  options={
                    presidentOptions.length > 0
                      ? presidentOptions
                      : [
                          {
                            id: 0,
                            label: t('form.placeholder.no_users_available'),
                          },
                        ]
                  }
                  disabled={
                    isLoadingSubmit || usersQuery.isLoading || !usersQuery.data
                  }
                />
              )}
            </form.AppField>

            <CardFooter
              className={`flex justify-end gap-2 ${onSuccess || onCancel ? 'p-0 pt-4' : 'p-0 pt-6'}`}
            >
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  if (onCancel) {
                    onCancel()
                  } else {
                    navigate({
                      to: '/admin/cooperatives',
                    })
                  }
                }}
                disabled={isLoadingSubmit}
              >
                {onCancel ? t('buttons.cancel') : t('buttons.back_to_list')}
              </Button>
              <Button
                type="submit"
                disabled={isLoadingSubmit || !form.state.canSubmit}
              >
                {isLoadingSubmit ? (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : isEditMode ? (
                  t('buttons.save_changes')
                ) : (
                  t('buttons.add')
                )}
              </Button>
            </CardFooter>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
