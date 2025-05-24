import { useFieldContext } from '..'
import { CityField } from './ciity-field'
import { LocationField } from './location-field'
import { StreetField } from './street-field'
import type { AddressReactForm } from './types'
import type { AddressDto } from '@/api/generated'
import { Label } from '@/components/ui/label'

interface Props {
  label: string
  required?: boolean
  withMap?: boolean
  mapHeight?: string
}

export function AddressField({
  label,
  required = true,
  withMap = false,
  mapHeight = '300px',
}: Props) {
  const parent = useFieldContext<AddressDto>() // FieldApi<AddressDto>
  const form = parent.form as AddressReactForm

  return (
    <div className="space-y-3 rounded-md border p-4">
      <Label className="text-lg font-semibold">{label}</Label>

      <CityField form={form} required={required} />

      <StreetField form={form} required={false} />
      {withMap && (
        <LocationField form={form} required={required} mapHeight={mapHeight} />
      )}
    </div>
  )
}
