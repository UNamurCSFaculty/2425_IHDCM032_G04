import { updateUserMutation } from '@/api/generated/@tanstack/react-query.gen'
import { zAddressDto } from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import type { AppUpdateUserDto, AppUserDetailDto } from '@/schemas/api-schemas'
import { zAppUpdateUser } from '@/schemas/api-schemas'
import { useAppData } from '@/store/appStore'
import { useUserStore } from '@/store/userStore'
import { useMutation } from '@tanstack/react-query'
import { AlertCircle, Loader2 } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'

interface UserProfileFormProps {
  currentUser: AppUserDetailDto
  onUpdateSuccess: () => void
  onCancel: () => void
}

export function UserProfileForm({
  currentUser,
  onUpdateSuccess,
  onCancel,
}: UserProfileFormProps) {
  const { t } = useTranslation()
  const appData = useAppData()
  const { setUser } = useUserStore()

  const mutation = useMutation({
    ...updateUserMutation(),
    onSuccess: updatedUserData => {
      toast.success(t('profile.update_success_toast'))
      setUser(updatedUserData)
      onUpdateSuccess()
    },
    onError: (error: any) => {
      const errorMessage =
        error?.errors?.[0]?.message || t('errors.generic_error_message')
      toast.error(t('profile.update_error_toast'), {
        description: errorMessage,
      })
    },
  })

  const form = useAppForm({
    validators: { onChange: zAppUpdateUser },
    defaultValues: {
      type: currentUser.type,
      firstName: currentUser.firstName ?? '',
      lastName: currentUser.lastName ?? '',
      phone: currentUser.phone ?? '',
      languageId: currentUser.language?.id ?? appData.languages[0]?.id,
      address: {
        street: currentUser.address?.street ?? '',
        cityId: currentUser.address?.cityId ?? undefined,
        location: currentUser.address?.location ?? undefined,
      },
      ...(currentUser.type === 'producer' && {
        agriculturalIdentifier: currentUser.agriculturalIdentifier ?? '',
        cooperativeId: currentUser.cooperative?.id ?? undefined,
      }),
      ...(currentUser.type === 'carrier' && {
        pricePerKm: currentUser.pricePerKm ?? undefined,
        radius: currentUser.radius ?? undefined,
      }),
    } as AppUpdateUserDto,
    onSubmit: async ({ value }) => {
      await mutation.mutateAsync({
        body: value,
        path: {
          id: currentUser.id,
        },
      })
    },
  })

  return (
    <form
      onSubmit={e => {
        e.preventDefault()
        form.handleSubmit()
      }}
      className="space-y-8"
    >
      <div>
        <h3 className="text-lg leading-6 font-medium text-gray-900 dark:text-white">
          {t('profile.personal_details_title')}
        </h3>
        <div className="mt-6 grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-6">
          <div className="sm:col-span-3">
            <form.AppField name="firstName">
              {f => <f.TextField label={t('form.first_name')} required />}
            </form.AppField>
          </div>
          <div className="sm:col-span-3">
            <form.AppField name="lastName">
              {f => <f.TextField label={t('form.last_name')} required />}
            </form.AppField>
          </div>
          <div className="sm:col-span-3">
            <form.AppField name="phone">
              {f => <f.PhoneField label={t('form.phone')} required />}
            </form.AppField>
          </div>
          <div className="sm:col-span-3">
            <form.AppField name="languageId">
              {f => (
                <f.VirtualizedSelectField
                  label={t('form.language')}
                  options={appData.languages.map(l => ({
                    id: l.id,
                    label: t('languages.' + l.code),
                  }))}
                  required
                />
              )}
            </form.AppField>
          </div>
        </div>
      </div>

      <Separator />

      <div>
        <h3 className="text-lg leading-6 font-medium text-gray-900 dark:text-white">
          {t('form.address')}
        </h3>
        <div className="mt-6 grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-6">
          <div className="sm:col-span-6">
            <form.AppField
              name="address.cityId"
              validators={{ onChange: zAddressDto.shape.cityId }}
            >
              {f => (
                <f.CityField
                  label={t('form.city')}
                  tooltip={t('form.tooltip.city')}
                  required
                />
              )}
            </form.AppField>
          </div>
          <div className="sm:col-span-6">
            <form.AppField name="address.street">
              {f => (
                <f.TextField
                  label={t('form.street_quarter')}
                  placeholder={t('form.placeholder.street_example')}
                />
              )}
            </form.AppField>
          </div>
          <div className="sm:col-span-6">
            <form.AppField
              name="address.location"
              validators={{ onChange: zAddressDto.shape.location }}
            >
              {f => (
                <f.LocationField
                  label={t('form.location')}
                  mapHeight="250px"
                  tooltip={t('form.tooltip.location')}
                  radius={
                    currentUser.type === 'carrier' &&
                    form.getFieldValue('radius')
                      ? (form.getFieldValue('radius') as number) * 1000
                      : 0
                  }
                  required
                />
              )}
            </form.AppField>
          </div>
        </div>
      </div>

      {currentUser.type === 'producer' && (
        <>
          <Separator />
          <div>
            <h3 className="text-lg leading-6 font-medium text-gray-900 dark:text-white">
              {t('types.producer_details')}
            </h3>
            <div className="mt-6 grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-6">
              <div className="sm:col-span-3">
                <form.AppField name="agriculturalIdentifier">
                  {f => (
                    <f.TextField
                      label={t('form.agricultural_identifier')}
                      required
                    />
                  )}
                </form.AppField>
              </div>
            </div>
          </div>
        </>
      )}

      {currentUser.type === 'carrier' && (
        <>
          <Separator />
          <div>
            <h3 className="text-lg leading-6 font-medium text-gray-900 dark:text-white">
              {t('types.carrier_details')}
            </h3>
            <div className="mt-6 grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-6">
              <div className="sm:col-span-3">
                <form.AppField name="pricePerKm">
                  {f => (
                    <f.NumberField label={t('form.price_per_km')} required />
                  )}
                </form.AppField>
              </div>
              <div className="sm:col-span-3">
                <form.AppField name="radius">
                  {f => <f.NumberField label={t('form.radius')} required />}
                </form.AppField>
              </div>
            </div>
          </div>
        </>
      )}

      {mutation.isError && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertTitle>{t('errors.error_occurred')}</AlertTitle>
          <AlertDescription>
            {mutation.error?.errors?.[0]?.message ||
              t('errors.generic_update_error')}
          </AlertDescription>
        </Alert>
      )}

      <div className="flex justify-end space-x-3 pt-6">
        <Button type="button" variant="outline" onClick={onCancel}>
          {t('buttons.cancel')}
        </Button>
        <Button
          type="submit"
          disabled={mutation.isPending || !form.state.canSubmit}
        >
          {mutation.isPending && (
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
          )}
          {t('buttons.save_changes')}
        </Button>
      </div>
    </form>
  )
}
