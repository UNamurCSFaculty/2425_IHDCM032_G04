import { FieldErrors } from '../field-errors'
import type { AddressReactForm } from './types'
import { zAddressDto } from '@/api/generated/zod.gen'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useTranslation } from 'react-i18next'

const streetSchema = zAddressDto.pick({ street: true }).shape.street

interface Props {
  form: AddressReactForm
  required: boolean
}

export const StreetField = ({ form, required }: Props) => {
  const { t } = useTranslation()

  return (
    <form.Field
      name="address.street"
      validators={{
        onChange: ({ value }) =>
          streetSchema.safeParse(value ?? '').success
            ? undefined
            : t('validation.required'),
      }}
    >
      {field => (
        <div className="space-y-1">
          <Label htmlFor={field.name}>
            Rue / Quartier {required && <span className="text-red-500">*</span>}
          </Label>
          <Input
            id={field.name}
            value={field.state.value ?? ''}
            onChange={e => field.handleChange(e.target.value)}
            onBlur={field.handleBlur}
            placeholder="Ex. : Rue de la soif"
          />
          <FieldErrors meta={field.state.meta} />
        </div>
      )}
    </form.Field>
  )
}
