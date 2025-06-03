import { useAppForm } from '@/components/form'
import { Button } from '@/components/ui/button'
import {
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogClose,
} from '@/components/ui/dialog'
import { useTranslation } from 'react-i18next'
import type {
  StoreDetailDto,
  StoreUpdateDto,
  UserListDto,
} from '@/api/generated'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  createStoreMutation,
  updateStoreMutation,
  listStoresQueryKey,
} from '@/api/generated/@tanstack/react-query.gen'
import { useEffect, useMemo } from 'react'
import { toast } from 'sonner'
import { zStoreUpdateDto } from '@/api/generated/zod.gen'

interface StoreFormProps {
  mode: 'create' | 'edit'
  existingStore?: StoreDetailDto | null
  users: UserListDto[]
  onSubmitSuccess: () => void
  onCancel: () => void
}

export const StoreForm: React.FC<StoreFormProps> = ({
  mode,
  existingStore,
  users,
  onSubmitSuccess,
  onCancel,
}) => {
  const { t } = useTranslation()
  const queryClient = useQueryClient()

  const defaultValues: Partial<StoreUpdateDto> = useMemo(() => {
    return (
      (mode === 'edit' &&
        existingStore && {
          id: existingStore.id,
          name: existingStore.name,
          userId: existingStore.userId,
          address: {
            street: existingStore.address.street || '',
            cityId: existingStore.address.cityId,
            location: existingStore.address.location,
            regionId: existingStore.address.regionId,
          },
        }) || {
        id: 0,
        name: '',
        userId: undefined,
        address: {
          street: '',
          location: '',
          cityId: 0,
          regionId: 0,
        },
      }
    )
  }, [mode, existingStore])

  const form = useAppForm({
    validators: {
      onChange: zStoreUpdateDto,
    },
    defaultValues,
    onSubmit: async ({ value }) => {
      const payload = value as StoreUpdateDto

      if (mode === 'edit' && existingStore) {
        await updateMutation.mutateAsync({
          path: { id: existingStore.id! },
          body: payload,
        })
      } else {
        await createMutation.mutateAsync({ body: payload })
      }
    },
  })

  useEffect(() => {
    form.reset(defaultValues)
  }, [form, defaultValues])

  const createMutation = useMutation({
    ...createStoreMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listStoresQueryKey() })
      toast.success(t('admin.store_management.create_success', 'Magasin créé.'))
      onSubmitSuccess()
    },
    onError: (error: any) => {
      const errorMessage =
        error.errors?.[0]?.message || error.message || t('common.error_unknown')
      toast.error(
        t('admin.store_management.submit_error', 'Erreur: ') + errorMessage
      )
    },
  })

  const updateMutation = useMutation({
    ...updateStoreMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: listStoresQueryKey() })
      toast.success(
        t('admin.store_management.update_success', 'Magasin mis à jour.')
      )
      onSubmitSuccess()
    },
    onError: (error: any) => {
      const errorMessage =
        error.errors?.[0]?.message || error.message || t('common.error_unknown')
      toast.error(
        t('admin.store_management.submit_error', 'Erreur: ') + errorMessage
      )
    },
  })

  const userOptions = users.map(user => ({
    id: user.id,
    label: `${user.firstName} ${user.lastName} (${user.email})`,
  }))

  return (
    <>
      <DialogHeader>
        <DialogTitle>
          {mode === 'edit'
            ? t('admin.store_management.edit_title', 'Modifier le Magasin')
            : t('admin.store_management.add_title', 'Ajouter un Magasin')}
        </DialogTitle>
        <DialogDescription>
          {t(
            'admin.store_management.form_description',
            'Remplissez les détails du magasin.'
          )}
        </DialogDescription>
      </DialogHeader>
      <form
        onSubmit={e => {
          e.preventDefault()
          e.stopPropagation()
          form.handleSubmit()
        }}
        className="space-y-4 py-4"
      >
        <form.AppField name="name">
          {f => (
            <f.TextField
              label={t('admin.store_management.name_label', 'Nom du magasin')}
              required
            />
          )}
        </form.AppField>

        <form.AppField name="userId">
          {f => (
            <f.VirtualizedSelectField
              label={t(
                'admin.store_management.user_label',
                'Utilisateur associé'
              )}
              options={userOptions}
              placeholder={t(
                'admin.store_management.select_user_placeholder',
                'Sélectionner un utilisateur'
              )}
              required
              modal={true}
            />
          )}
        </form.AppField>

        <fieldset className="grid grid-cols-1 gap-4 rounded-md border p-4">
          <legend className="text-muted-foreground px-1 text-sm font-medium">
            {t('form.address', 'Adresse')}
          </legend>
          <form.AppField name="address.street">
            {f => (
              <f.TextField
                label={t(
                  'admin.store_management.address_street_label',
                  'Rue / Quartier'
                )}
              />
            )}
          </form.AppField>

          <form.AppField name="address.cityId">
            {f => (
              <f.CityField
                label={t('admin.store_management.address_city_label', 'Ville')}
                required
                modal={true}
              />
            )}
          </form.AppField>

          <form.AppField name="address.location">
            {f => (
              <f.LocationField
                label={t(
                  'admin.store_management.address_location_label',
                  'Coordonnées (WKT)'
                )}
                mapHeight="200px"
                required
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
          <Button
            type="submit"
            disabled={
              createMutation.isPending ||
              updateMutation.isPending ||
              form.state.isSubmitting
            }
          >
            {createMutation.isPending ||
            updateMutation.isPending ||
            form.state.isSubmitting
              ? t('buttons.saving')
              : t('buttons.save')}
          </Button>
        </DialogFooter>
      </form>
    </>
  )
}
