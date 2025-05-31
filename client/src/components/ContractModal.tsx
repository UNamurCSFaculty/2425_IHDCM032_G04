import { useAppForm } from './form'
import { type AuctionDto, type BidDto, type QualityDto } from '@/api/generated'
import {
  createContractOfferMutation,
  listQualitiesOptions,
} from '@/api/generated/@tanstack/react-query.gen.ts'
import { ContratSchema } from '@/schemas/form-schemas'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from '@/components/ui/dialog' // Import ShadCN UI Dialog components
import { useMutation, useSuspenseQuery } from '@tanstack/react-query'
import React from 'react'
import { useTranslation } from 'react-i18next'
import { Button } from './ui/button'
import { AvatarFallback } from '@radix-ui/react-avatar'

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
    <Dialog open={isOpen} onOpenChange={open => !open && onClose()}>
      <DialogContent className="max-h-[90vh] w-full max-w-md overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="mb-4 text-2xl font-bold">
            {t('contract.modal_title')}
          </DialogTitle>
        </DialogHeader>
        <DialogClose asChild>
          <button className="ring-offset-background focus:ring-ring data-[state=open]:bg-accent data-[state=open]:text-muted-foreground absolute top-4 right-4 rounded-sm opacity-70 transition-opacity hover:opacity-100 focus:ring-2 focus:ring-offset-2 focus:outline-none disabled:pointer-events-none">
            ✕<span className="sr-only">Close</span>
          </button>
        </DialogClose>

        <div className="mb-4 flex items-center gap-4">
          <AvatarFallback className="h-10 w-10 rounded-full object-cover" />
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
            e.stopPropagation()
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

          <span className="mb-1 block text-sm font-semibold">
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
                  }}
                />
              )}
            />
          </div>

          <p className="mt-1 text-xs text-gray-500">
            {t('contract.ends_on_date_label', { date: endDateDisplay })}
          </p>

          <DialogFooter className="mt-6 gap-2">
            <Button type="button" variant="outline" onClick={onClose}>
              {t('buttons.cancel')}
            </Button>
            <form.SubmitButton
              disabled={mutation.isPending || form.state.isSubmitting}
            >
              {mutation.isPending || form.state.isSubmitting
                ? t('buttons.submitting')
                : t('buttons.submit')}
            </form.SubmitButton>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
