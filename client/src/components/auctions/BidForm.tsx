import { createBidMutation } from '@/api/generated/@tanstack/react-query.gen.ts'
import { useAppForm } from '@/components/form'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { zBidUpdateDto } from '@/api/generated/zod.gen'
import React, { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import type { BidUpdateDto } from '@/api/generated'
import { useAuthUser } from '@/store/userStore'

interface BidFormProps {
  auctionId: number;
}

export function BidForm({ auctionId }: BidFormProps): React.ReactElement<'div'> {
  const user = useAuthUser()
  const navigate = useNavigate()
  const { i18n } = useTranslation()

  const createBidRequest = useMutation({
    ...createBidMutation(),
    onSuccess() {
      navigate({ to: '/achats/mes-encheres' })
    },
    onError(error) {
      console.error('Requête invalide : ', error)
    },
  })

  const form = useAppForm({
    validators: { onChange: zBidUpdateDto },

    defaultValues: {
      amount: '',
      traderId: user.id
    },

    onSubmit({ value }) {
      createBidRequest.mutate({ body: value })
    }
  })

  useEffect(() => {
    form.validate('change')
  }, [i18n.language, form])

  return (
      <div className="container mx-auto px-5 py-5">
          <form
            onSubmit={e => {
              e.preventDefault()
              e.stopPropagation()
              form.handleSubmit()
            }}
          >
            <div className="flex flex-col gap-6">
                <form.AppField
                  name="amount"
                  children={field => (
                    <field.TextField
                      label="Votre offre (CFA)"
                      type="amount"
                      placeholder="0.0"
                    />
                  )}
                />

              <form.AppForm>
                <form.SubmitButton>Enchérir</form.SubmitButton>
              </form.AppForm>
            </div>
          </form>
      </div>
  )
}
