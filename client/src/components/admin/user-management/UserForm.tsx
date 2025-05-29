import { useAppForm } from '@/components/form'
import { Button } from '@/components/ui/button'
import {
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogClose,
} from '@/components/ui/dialog'
import { formatFileSize, type DeepPartial } from '@/lib/utils'
import { useAppData } from '@/store/appStore'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import {
  zAddressDto,
  zUserCreateDto as zApiUserCreateDto,
} from '@/api/generated/zod.gen'
import { FileText, Download, Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { downloadDocument } from '@/api/generated'
import {
  zAppUpdateUser,
  type AppUpdateUserDto,
  type AppUserDetailDto,
  zAppCreateUser,
} from '@/schemas/api-schemas'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  updateUserMutation,
  createUserMutation,
  listUsersQueryKey,
} from '@/api/generated/@tanstack/react-query.gen'
import z from 'zod/v4'
import { useStore } from '@tanstack/react-form'

const zAdminUpdateUser = zAppUpdateUser
const zAdminCreateUser = zAppCreateUser.and(
  z.object({
    password: z.string().min(8),
    documents: z.array(z.file()).optional(),
  })
)

interface UserFormProps {
  mode: 'create' | 'edit'
  existingUser?: AppUserDetailDto
  onSubmitSuccess: () => void
  onCancel: () => void
  formTitle: string
  formDescription: string
  submitButtonText: string
}

export const UserForm: React.FC<UserFormProps> = ({
  mode,
  existingUser,
  onSubmitSuccess,
  onCancel,
  formTitle,
  formDescription,
  submitButtonText,
}) => {
  const { t } = useTranslation()
  const appData = useAppData()
  const queryClient = useQueryClient()
  const [downloadingDocIds, setDownloadingDocIds] = useState<
    Record<string, boolean>
  >({})

  const isEditMode = mode === 'edit'

  const defaultValues: DeepPartial<AppUpdateUserDto> =
    isEditMode && existingUser
      ? {
          firstName: existingUser.firstName,
          lastName: existingUser.lastName,
          email: existingUser.email,
          phone: existingUser.phone,
          languageId: existingUser.language.id,
          enabled: existingUser.enabled,
          type: existingUser.type,
          address: existingUser.address
            ? {
                cityId: existingUser.address.cityId,
                street: existingUser.address.street,
                location: existingUser.address.location,
              }
            : {},
          agriculturalIdentifier: (existingUser as any).agriculturalIdentifier,
          pricePerKm: (existingUser as any).pricePerKm,
          radius: (existingUser as any).radius,
        }
      : {
          // Defaults for create mode
          firstName: '',
          lastName: '',
          email: '',
          phone: '',
          languageId: appData.languages[0]?.id,
          enabled: true,
          type: undefined,
          address: {},
          password: '',
          agriculturalIdentifier: '',
          pricePerKm: 0,
          radius: 0,
        }

  const mutationUpdate = useMutation({
    ...updateUserMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listUsersQueryKey() })
      if (existingUser) {
        queryClient.invalidateQueries({
          queryKey: ['getUser', { path: { id: existingUser.id } }],
        })
      }
      toast.success(t('admin.user_management.toasts.user_updated_success'))
      onSubmitSuccess()
    },
    onError: error => {
      toast.error(t('common.error_general'), {
        description:
          error.errors.map(e => e.message).join(' ,') ||
          t('admin.user_management.toasts.user_updated_error'),
      })
    },
  })

  const mutationCreate = useMutation({
    ...createUserMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listUsersQueryKey() })
      toast.success(t('admin.user_management.toasts.user_created_success'))
      onSubmitSuccess()
    },
    onError: error => {
      toast.error(t('common.error_general'), {
        description:
          error.errors.map(e => e.message).join(' ,') ||
          t('admin.user_management.toasts.user_created_error'),
      })
    },
  })

  // Choose schema based on mode
  const currentSchema = isEditMode ? zAdminUpdateUser : zAdminCreateUser

  const form = useAppForm({
    validators: {
      onChange: currentSchema,
    },
    defaultValues,
    onSubmit: async ({ value }) => {
      if (isEditMode && existingUser) {
        const body: AppUpdateUserDto = value as AppUpdateUserDto
        await mutationUpdate.mutateAsync({
          path: { id: existingUser.id },
          body,
        })
      } else {
        const userCreatePayload = value as z.infer<typeof zAdminCreateUser>
        await mutationCreate.mutateAsync({
          body: { user: userCreatePayload, documents: [] },
        })
      }
    },
  })

  const values = useStore(form.store, s => s.values)

  useEffect(() => {
    form.reset()
  }, [existingUser, mode, form])

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

  const handleDownloadDocument = async (docId: number, docName: string) => {
    setDownloadingDocIds(prev => ({ ...prev, [docId]: true }))
    try {
      const blobResponse = await downloadDocument({ path: { id: docId } })
      if (blobResponse instanceof Blob) {
        triggerBlobDownload(blobResponse, docName)
      } else {
        console.error('Expected a Blob response, but received:', blobResponse)
        toast.error(t('common.download_error'), {
          description: 'Invalid file format received.',
        })
      }
    } catch (error) {
      console.error('Download error:', error)
      toast.error(t('common.download_error'), {
        description: (error as Error).message || t('common.unknown_error'),
      })
    } finally {
      setDownloadingDocIds(prev => ({ ...prev, [docId]: false }))
    }
  }

  return (
    <>
      <DialogHeader>
        <DialogTitle>{formTitle}</DialogTitle>
        <DialogDescription>{formDescription}</DialogDescription>
      </DialogHeader>
      <form
        onSubmit={e => {
          e.preventDefault()
          form.handleSubmit()
        }}
        className="max-h-[70vh] space-y-4 overflow-y-auto px-1 py-4"
      >
        {/* Basic Info */}
        <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4 md:grid-cols-2">
          <legend className="text-muted-foreground px-1 text-sm font-medium">
            {t('form.sections.basic_info')}
          </legend>
          <form.AppField name="type">
            {f => (
              <f.RadioGroupField
                className="md:col-span-2"
                direction="row"
                choices={[
                  { value: 'producer', label: t('types.producer') },
                  { value: 'transformer', label: t('types.transformer') },
                  { value: 'exporter', label: t('types.exporter') },
                  { value: 'carrier', label: t('types.carrier') },
                  { value: 'admin', label: t('types.admin') },
                  {
                    value: 'quality_inspector',
                    label: t('types.quality_inspector'),
                  },
                ]}
                label={t('form.type')}
                readonly={isEditMode}
              />
            )}
          </form.AppField>
          <form.AppField name="lastName">
            {f => <f.TextField label={t('form.last_name')} />}
          </form.AppField>
          <form.AppField name="firstName">
            {f => <f.TextField label={t('form.first_name')} />}
          </form.AppField>
          <form.AppField
            name="email"
            validators={{ onChange: zApiUserCreateDto.shape.email.optional() }} // Use optional for edit, create schema handles requirement
          >
            {f => <f.TextField type="email" label={t('form.mail')} />}
          </form.AppField>
          <form.AppField
            name="phone"
            validators={{
              onChange: zApiUserCreateDto.shape.phone.optional(),
            }}
          >
            {f => (
              <f.PhoneField label={t('form.phone')} required={!isEditMode} />
            )}
          </form.AppField>
          <form.AppField name="languageId">
            {f => (
              <f.VirtualizedSelectField
                placeholder={t('form.placeholder.language')}
                options={appData.languages.map(l => ({
                  id: l.id,
                  label: t('languages.' + l.code),
                }))}
                label={t('form.language')}
              />
            )}
          </form.AppField>

          <form.AppField name="enabled">
            {f => (
              <f.CheckboxField
                className="md:col-span-2"
                label={t('form.enabled.label')}
                tooltip={t('form.enabled.description')}
              />
            )}
          </form.AppField>
          {isEditMode && existingUser?.type === 'producer' && (
            <div className="flex items-center text-sm font-medium">
              {t('form.cooperative')} : {existingUser.cooperative?.name || '/'}
            </div>
          )}
        </fieldset>

        {/* Password Fields for Create Mode */}
        {!isEditMode && (
          <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4 md:grid-cols-2">
            <legend className="text-muted-foreground px-1 text-sm font-medium">
              {t('form.sections.password')}
            </legend>
            <form.AppField name="password">
              {f => (
                <f.TextField
                  type="password"
                  label={t('form.password')}
                  required={!isEditMode}
                />
              )}
            </form.AppField>
            {/* Add confirm password if needed by your schema/logic */}
          </fieldset>
        )}

        {/* User Type Specific Fields */}
        {values.type === 'producer' && (
          <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4">
            <legend className="text-muted-foreground px-1 text-sm font-medium">
              {t('types.producer')}
            </legend>
            <form.AppField name="agriculturalIdentifier">
              {f => <f.TextField label={t('form.agricultural_identifier')} />}
            </form.AppField>
          </fieldset>
        )}

        {values.type === 'carrier' && (
          <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4 md:grid-cols-2">
            <legend className="text-muted-foreground px-1 text-sm font-medium">
              {t('types.carrier')}
            </legend>
            <form.AppField name="pricePerKm">
              {f => (
                <f.TextField<number>
                  label={t('form.price_per_km')}
                  fieldType="number"
                  type="number"
                />
              )}
            </form.AppField>
            <form.AppField name="radius">
              {f => (
                <f.TextField<number>
                  label={t('form.radius')}
                  fieldType="number"
                  type="number"
                />
              )}
            </form.AppField>
          </fieldset>
        )}

        {/* Address */}
        <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4">
          <legend className="text-muted-foreground px-1 text-sm font-medium">
            {t('form.address')}
          </legend>
          <form.AppField
            name="address.cityId"
            validators={{ onChange: zAddressDto.shape.cityId.optional() }}
          >
            {f => <f.CityField label={t('form.city')} required={false} />}
          </form.AppField>
          <form.AppField name="address.street">
            {f => (
              <f.TextField label={t('form.street_quarter')} required={false} />
            )}
          </form.AppField>
          <form.AppField
            name="address.location"
            validators={{
              onChange: zAddressDto.shape.location.optional(),
            }}
          >
            {f => (
              <f.LocationField
                label={t('form.location')}
                mapHeight="200px"
                radius={
                  values.type === 'carrier' &&
                  typeof form.getFieldValue('radius' as any) === 'number'
                    ? (form.getFieldValue('radius' as any) || 0) * 1000
                    : 0
                }
                required={false}
              />
            )}
          </form.AppField>
        </fieldset>

        {/* Password Change - Optional for Edit Mode */}
        {isEditMode && (
          <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4">
            <legend className="text-muted-foreground px-1 text-sm font-medium">
              {t('form.sections.change_password')}
            </legend>
            <form.AppField name="password">
              {f => (
                <f.TextField
                  type="password"
                  label={t('form.new_password')}
                  placeholder={t('form.placeholder.leave_blank_password')}
                />
              )}
            </form.AppField>
          </fieldset>
        )}

        <DialogFooter className="pt-4">
          <DialogClose asChild>
            <Button type="button" variant="outline" onClick={onCancel}>
              {t('buttons.cancel')}
            </Button>
          </DialogClose>
          <Button
            type="submit"
            disabled={mutationUpdate.isPending || mutationCreate.isPending}
          >
            {mutationUpdate.isPending || mutationCreate.isPending
              ? t('buttons.submitting')
              : submitButtonText}
          </Button>
        </DialogFooter>
      </form>

      {/* Documents Section - if any (Only for edit mode and if user has documents) */}
      {isEditMode &&
        existingUser?.documents &&
        existingUser.documents.length > 0 && (
          <fieldset className="mt-4 rounded-md border p-4">
            <legend className="text-muted-foreground px-1 text-sm font-medium">
              {t('form.documents')}
            </legend>
            <div className="max-h-[200px] space-y-2 overflow-y-auto py-2 pr-1">
              {existingUser.documents.map(doc => (
                <div
                  key={doc.id}
                  className="bg-background flex items-center justify-between rounded-md border p-3 shadow-sm"
                >
                  <div className="flex flex-grow items-center space-x-3 overflow-hidden">
                    <FileText className="text-muted-foreground h-5 w-5 flex-shrink-0" />
                    <div className="flex-grow overflow-hidden">
                      <p
                        className="truncate text-sm font-medium"
                        title={doc.originalFilename || `document-${doc.id}`}
                      >
                        {doc.originalFilename || `document-${doc.id}`}
                      </p>
                      <p className="text-muted-foreground text-xs">
                        {formatFileSize(doc.size as any as number)}
                      </p>
                    </div>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    className="ml-2 flex-shrink-0"
                    onClick={() =>
                      handleDownloadDocument(
                        doc.id,
                        doc.originalFilename || `document-${doc.id}`
                      )
                    }
                    disabled={downloadingDocIds[doc.id]}
                  >
                    {downloadingDocIds[doc.id] ? (
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    ) : (
                      <Download className="mr-2 h-4 w-4" />
                    )}
                    {downloadingDocIds[doc.id]
                      ? t('buttons.downloading')
                      : t('buttons.download')}
                  </Button>
                </div>
              ))}
            </div>
          </fieldset>
        )}
    </>
  )
}
