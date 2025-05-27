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
import { zAddressDto, zUserCreateDto } from '@/api/generated/zod.gen'
import { FileText, Download, Loader2 } from 'lucide-react'
import { toast } from 'sonner'
import { downloadDocument } from '@/api/generated'
import {
  zAppUpdateUser,
  type AppUpdateUserDto,
  type AppUserDetailDto,
} from '@/schemas/api-schemas'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateUserMutation } from '@/api/generated/@tanstack/react-query.gen'

interface UserFormProps {
  existingUser: AppUserDetailDto
  onSubmitSuccess: () => void
  onCancel: () => void
  formTitle: string
  formDescription: string
  submitButtonText: string
}

export const UserForm: React.FC<UserFormProps> = ({
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

  const defaultValues: DeepPartial<AppUpdateUserDto> = {
    firstName: existingUser.firstName,
    lastName: existingUser.lastName,
    email: existingUser.email,
    phone: existingUser.phone,
    languageId: existingUser.language.id,
    enabled: existingUser.enabled,
    address: existingUser.address
      ? {
          cityId: existingUser.address.cityId,
          street: existingUser.address.street,
          location: existingUser.address.location,
        }
      : undefined,
    type: existingUser.type,

    ...(existingUser.type === 'producer' && {
      agriculturalIdentifier: (existingUser as any).agriculturalIdentifier,
      cooperative: (existingUser as any).cooperative,
    }),
    ...(existingUser.type === 'carrier' && {
      pricePerKm: (existingUser as any).pricePerKm,
      radius: (existingUser as any).radius,
    }),
  }

  const mutationUpdate = useMutation({
    ...updateUserMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['listUsers'] })
      queryClient.invalidateQueries({
        queryKey: ['getUser', { path: { id: existingUser.id } }],
      })
      toast.success(t('admin.user_management.toasts.user_updated_success'))
      onSubmitSuccess()
    },
    onError: (error: any) => {
      toast.error(t('common.error'), {
        description:
          error.message || t('admin.user_management.toasts.user_updated_error'),
      })
    },
  })

  const form = useAppForm({
    validators: {
      onChange: zAppUpdateUser,
    },
    defaultValues,
    onSubmit: async ({ value }) => {
      const body: AppUpdateUserDto = value as AppUpdateUserDto
      await mutationUpdate.mutateAsync({
        path: { id: existingUser.id },
        body,
      })
    },
  })

  // const currentFormValues  = useStore(form.store, s => s.values)

  useEffect(() => {
    form.reset()
  }, [existingUser, form])

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
        description: t('common.download_error') || t('common.unknown_error'),
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
          <form.AppField name="lastName">
            {f => <f.TextField label={t('form.last_name')} />}
          </form.AppField>
          <form.AppField name="firstName">
            {f => <f.TextField label={t('form.first_name')} />}
          </form.AppField>
          <form.AppField
            name="email"
            // Assuming zAppUpdateUser has email validation, or use zGeneratedUserUpdateDto.shape.email
            validators={{ onChange: zUserCreateDto.shape.email.optional() }}
          >
            {f => <f.TextField type="email" label={t('form.mail')} />}
          </form.AppField>
          <form.AppField
            name="phone"
            validators={{
              onChange: zUserCreateDto.shape.phone.optional(),
            }}
          >
            {f => <f.PhoneField label={t('form.phone')} required={false} />}
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
        </fieldset>

        {/* Type Specific Fields - Render based on existingUser.type */}
        {/* Ensure field names match AppUpdateUserDto structure */}
        {existingUser.type === 'producer' && (
          <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4">
            <legend className="text-muted-foreground px-1 text-sm font-medium">
              {t('types.producer')}
            </legend>
            <form.AppField name="agriculturalIdentifier">
              {f => <f.TextField label={t('form.agricultural_identifier')} />}
            </form.AppField>
            {/* Add cooperative field if it's part of AppUpdateUserDto for producer */}
            {/* <form.AppField name="cooperative.id"> or similar based on zCooperativeDto structure */}
          </fieldset>
        )}

        {existingUser.type === 'carrier' && (
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
            // Assuming zAppUpdateUser has address validation, or use zAddressDto.shape.cityId
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
                  existingUser.type === 'carrier' &&
                  typeof existingUser.radius === 'number'
                    ? existingUser.radius * 1000
                    : 0
                }
                required={false}
              />
            )}
          </form.AppField>
        </fieldset>

        {/* Password Change - Optional */}
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

        <DialogFooter className="pt-4">
          <DialogClose asChild>
            <Button type="button" variant="outline" onClick={onCancel}>
              {t('buttons.cancel')}
            </Button>
          </DialogClose>
          <Button type="submit" disabled={mutationUpdate.isPending}>
            {mutationUpdate.isPending
              ? t('buttons.submitting')
              : submitButtonText}
          </Button>
        </DialogFooter>
      </form>

      {/* Documents Section - if any */}
      {existingUser.documents && existingUser.documents.length > 0 && (
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
