import { useAppForm } from './form'
import { type AuctionDto, type BidDto, type QualityDto } from '@/api/generated'
import {
  createContractOfferMutation,
  listQualitiesOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import avatar from '@/assets/avatar.webp'
import { ContratSchema } from '@/schemas/form-schemas'
import * as Dialog from '@radix-ui/react-dialog'
import { useMutation, useSuspenseQuery } from '@tanstack/react-query'
import React from 'react'

interface ContractModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: () => void
  auction: AuctionDto
  acceptedBid: BidDto
}

// const defaultValues = {
//   quality: '1',
//   price: 5100,
//   quantity: 500,
//   lastingYear: 0,
//   lastingMonth: 6,
// }

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
          status: 'Init',
          pricePerKg: value.price,
          endDate: endDate.toISOString(),
          qualityId: Number(value.quality),
          sellerId: acceptedBid.trader.id,
          buyerId: auction.trader.id,
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
        <Dialog.Content className="fixed top-1/2 left-1/2 max-h-[90vh] w-full max-w-md -translate-x-1/2 -translate-y-1/2 overflow-y-auto rounded-xl bg-white p-4 shadow-lg">
          <Dialog.Title className="mb-4 text-2xl font-bold">
            Proposition de contrat
          </Dialog.Title>
          <Dialog.Close asChild>
            <button className="absolute right-4 top-4 text-gray-500 hover:text-gray-700">
              ✕
            </button>
          </Dialog.Close>

          <div className="mb-4 flex items-center gap-4">
            <img
              className="h-10 w-10 rounded-full object-cover"
              src={avatar}
              alt="Avatar"
            />
            <div className="text-sm text-gray-700">
              <p>
                <strong>Lieu:</strong> {auction.product.store.name}
              </p>
              <p>
                <strong>Vendeur:</strong>{' '}
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
                  label="Qualité"
                  placeholder="Choisissez une qualité"
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
                <field.NumberField label="Prix garanti (CFA/kg)" />
              )}
            />

            <form.AppField
              name="quantity"
              children={field => (
                <field.NumberField label="Quantité minimum (kg)" />
              )}
            />

            <span className="mb-1 text-sm font-semibold">Durée du contact</span>
            <div className="flex items-end gap-2">
              <form.AppField
                name="lastingYear"
                children={field => (
                  <field.NumberField
                    label="(Années)"
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
                    label="(Mois)"
                    onChange={e => {
                      const value = Number(e.target.value)
                      field.handleChange(value)
                      updateEndDate()
                    }}
                  />
                )}
              />
            </div>

            <p className="text-xs text-gray-500 mt-1">
              Se termine le {endDateDisplay}
            </p>

            <div className="mt-6 flex justify-end gap-2">
              <button
                type="button"
                onClick={onClose}
                className="rounded border px-4 py-2 text-sm"
              >
                Annuler
              </button>
              <form.AppForm>
                <form.SubmitButton>Envoyer</form.SubmitButton>
              </form.AppForm>
            </div>
          </form>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}
