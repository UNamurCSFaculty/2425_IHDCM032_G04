import { useAppForm } from './form'
import { type AuctionDto, type BidDto, type QualityDto } from '@/api/generated'
import {
  createContractOfferMutation,
  listQualitiesOptions,
} from '@/api/generated/@tanstack/react-query.gen.ts'
import avatar from '@/assets/avatar.webp'
import { ContratSchema } from '@/schemas/form-schemas'
import * as Dialog from '@radix-ui/react-dialog'
import { useMutation, useSuspenseQuery } from '@tanstack/react-query'
import React from 'react'
import { useTranslation } from 'react-i18next'

interface ContractModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: () => void
  auction: AuctionDto
  acceptedBid: BidDto
}

const listQualitiesQueryOptions = () => ({
  ...listQualitiesOptions(),
})

export const ContractModal: React.FC<ContractModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  auction,
  acceptedBid,
}) => {
  const { t } = useTranslation()
  const [endDateDisplay, setEndDateDisplay] = React.useState<string>('')
  const { data } = useSuspenseQuery(listQualitiesQueryOptions())
  const qualities = data as QualityDto[]

  const mutation = useMutation(createContractOfferMutation())

  const defaultValues = React.useMemo(
    () => ({
      quality: auction.product.qualityControl.quality.id.toString(),
      price: acceptedBid.amount,
      quantity: auction.productQuantity,
      lastingYear: 0,
      lastingMonth: 6,
    }),
    [auction, acceptedBid]
  )

  const form = useAppForm({
    defaultValues,
    onSubmit: async ({ value }) => {
      onSubmit()

      const creationDate = new Date()
      const endDate = new Date()
      endDate.setMonth(creationDate.getMonth() + value.lastingMonth)
      endDate.setFullYear(creationDate.getFullYear() + value.lastingYear)

      mutation.mutate({
        body: {
          status: 'Waiting',
          pricePerKg: value.price,
          endDate: endDate.toISOString(),
          qualityId: Number(value.quality),
          sellerId: auction.trader.id,
          buyerId: acceptedBid.trader.id,
          creationDate: creationDate.toISOString(),
        },
      })

      onClose()
    },
    validators: {
      onChange: ContratSchema,
    },
  })

  const updateEndDate = React.useCallback(() => {
    const lastingYear = form.state.values.lastingYear
    const lastingMonth = form.state.values.lastingMonth
    if (typeof lastingYear === 'number' && typeof lastingMonth === 'number') {
      const now = new Date()
      const end = new Date(now)
      end.setFullYear(end.getFullYear() + lastingYear)
      end.setMonth(end.getMonth() + lastingMonth)
      setEndDateDisplay(end.toLocaleDateString('fr-FR'))
    }
  }, [form.state.values.lastingYear, form.state.values.lastingMonth])

  React.useEffect(() => {
    updateEndDate()
  }, [updateEndDate])

  return (
    <Dialog.Root open={isOpen} onOpenChange={open => !open && onClose()}>
      <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 bg-black/40" />
        <Dialog.Content className="fixed top-1/2 left-1/2 z-[50] max-h-[90vh] w-full max-w-md -translate-x-1/2 -translate-y-1/2 overflow-y-auto rounded-xl bg-white p-4 shadow-lg">
          <Dialog.Title className="mb-4 text-2xl font-bold">
            {t('contract.modal_title')}
          </Dialog.Title>
          <Dialog.Close asChild>
            <button className="absolute top-4 right-4 text-gray-500 hover:text-gray-700">
              ✕
            </button>
          </Dialog.Close>

          <div className="mb-4 flex items-center gap-4">
            <img
              className="h-10 w-10 rounded-full object-cover"
              src={avatar}
              alt={t('contract.avatar_alt')}
            />
            <div className="text-sm text-gray-700">
              <p>
                <strong>{t('form.location')}:</strong>{' '}
                {auction.product.store.name}
              </p>
              <p>
                <strong>{t('auction.seller_label')}:</strong>{' '}
                {auction.trader.firstName + ' ' + auction.trader.lastName}
              </p>
            </div>
          </div>

          <form
            onSubmit={e => {
              e.preventDefault()
              form.handleSubmit()
            }}
            className="space-y-4"
          >
            <form.AppField
              name="quality"
              children={field => (
                <field.SelectField
                  label={t('product.quality_label')}
                  placeholder={t('form.select.placeholder')}
                  options={qualities.map(q => ({
                    value: q.id.toString(),
                    label: `${q.name}`,
                  }))}
                />
              )}
            />

            <form.AppField
              name="price"
              children={field => (
                <field.NumberField
                  label={t('contract.guaranteed_price_cfa_kg_label')}
                />
              )}
            />

            <form.AppField
              name="quantity"
              children={field => (
                <field.NumberField
                  label={t('contract.minimum_quantity_kg_label')}
                />
              )}
            />

            <span className="mb-1 text-sm font-semibold">
              {t('contract.duration_label')}
            </span>
            <div className="flex items-end gap-2">
              <form.AppField
                name="lastingYear"
                children={field => (
                  <field.NumberField
                    label={t('contract.duration_years_label')}
                    onChange={e => {
                      const value = Number(e.target.value)
                      field.handleChange(value)
                      updateEndDate()
                    }}
                  />
                )}
              />
              <form.AppField
                name="lastingMonth"
                children={field => (
                  <field.NumberField
                    label={t('contract.duration_months_label')}
                    onChange={e => {
                      const value = Number(e.target.value)
                      field.handleChange(value)
                      updateEndDate()
                    }}
                  />
                )}
              />
            </div>

            <p className="mt-1 text-xs text-gray-500">
              {t('contract.ends_on_date_label', { date: endDateDisplay })}
            </p>

            <div className="mt-6 flex justify-end gap-2">
              <button
                type="button"
                onClick={onClose}
                className="rounded border px-4 py-2 text-sm"
              >
                {t('buttons.cancel')}
              </button>
              <form.AppForm>
                <form.SubmitButton>{t('buttons.submit')}</form.SubmitButton>
              </form.AppForm>
            </div>
          </form>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}
