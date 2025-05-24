import { zAddressDto } from '@/api/generated/zod.gen'
import { FieldErrors } from '../field-errors'
import type { AddressReactForm } from './types'
import {
  type NominatimAddress,
  SelectorMap,
} from '@/components/map/SelectorMap'
import { Label } from '@/components/ui/label'
import cityNamesJson from '@/data/cities.json'

const cityOptions = cityNamesJson.map((n, i) => ({ id: i + 1, label: n }))

interface Props {
  form: AddressReactForm
  required: boolean
  mapHeight: string
}

const locationSchema = zAddressDto.pick({ location: true }).shape.location

export const LocationField = ({ form, required, mapHeight }: Props) => (
  <form.Field
    name="address.location"
    validators={{
      onChange: locationSchema,
    }}
  >
    {field => (
      <div className="space-y-1">
        <Label>
          Localisation {required && <span className="text-red-500">*</span>}
        </Label>

        <SelectorMap
          mapHeight={mapHeight}
          initialPosition={field.state.value}
          onPositionChange={(point: string, addr?: NominatimAddress) => {
            if (addr?.city) {
              const match = cityOptions.find(
                c =>
                  c.label.localeCompare(addr.city!, undefined, {
                    sensitivity: 'base',
                  }) === 0
              )
              if (match) form.setFieldValue('address.cityId', match.id)
            }
            field.handleChange(point)
          }}
        />

        {field.state.value && (
          <p className="text-xs text-gray-500">
            Coordonnée : {field.state.value}
          </p>
        )}

        <FieldErrors meta={field.state.meta} />
      </div>
    )}
  </form.Field>
)
