import { useFieldContext } from '.'
import {
  SelectorMap,
  type NominatimAddress,
} from '@/components/map/SelectorMap'
import { Label } from '../ui/label'
import { FieldErrors } from './field-errors'
import cityNamesJson from '@/data/cities.json'

const cityOptions = cityNamesJson.map((n, i) => ({ id: i + 1, label: n }))

interface LocationFieldProps {
  label: string
  required?: boolean
  mapHeight?: string
  radius?: number
}

export function LocationField({
  label,
  required = true,
  mapHeight = '300px',
  radius = 0,
}: LocationFieldProps) {
  const field = useFieldContext<string>() // Le champ contient la coordonnée POINT(...)

  const onPositionChange = (point: string, addr?: NominatimAddress) => {
    // Si l'adresse de la carte contient une ville, on met à jour le champ cityId
    if (addr?.city) {
      const match = cityOptions.find(
        c =>
          c.label.localeCompare(addr.city!, undefined, {
            sensitivity: 'base',
          }) === 0
      )
      if (match) {
        field.form.setFieldValue('address.cityId', match.id)
      }
    }
    field.handleChange(point)
    field.handleBlur()
  }

  return (
    <div className="space-y-2">
      <Label>
        {label}
        {required && <span className="text-red-500">*</span>}
      </Label>
      <SelectorMap
        mapHeight={mapHeight}
        initialPosition={field.state.value}
        onPositionChange={onPositionChange}
        radius={radius}
      />
      {field.state.value && (
        <p className="text-xs text-gray-500">
          Coordonnée : {field.state.value}
        </p>
      )}
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
