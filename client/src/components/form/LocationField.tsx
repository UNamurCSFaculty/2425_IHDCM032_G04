import { useFieldContext } from '.'
import {
  SelectorMap,
  type NominatimAddress,
} from '@/components/map/SelectorMap'
import { Label } from '../ui/label'
import { FieldErrors } from './field-errors'
import cityNamesJson from '@/data/cities.json'
import SimpleTooltip from '../SimpleTooltip'
import { useTranslation } from 'react-i18next'
import { formatCoordinates } from '@/utils/formatter'

const cityOptions = cityNamesJson.map((n, i) => ({ id: i + 1, label: n }))

interface LocationFieldProps {
  label: string
  required?: boolean
  mapHeight?: string
  tooltip?: string
  radius?: number
}

export function LocationField({
  label,
  required = true,
  tooltip,
  mapHeight = '300px',
  radius = 0,
}: LocationFieldProps) {
  const field = useFieldContext<string>()
  const { t } = useTranslation()

  const onPositionChange = (point: string, addr?: NominatimAddress) => {
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
        {tooltip && <SimpleTooltip content={tooltip} />}
      </Label>
      <SelectorMap
        mapHeight={mapHeight}
        initialPosition={field.state.value}
        onPositionChange={onPositionChange}
        radius={radius}
      />
      {field.state.value && (
        <p className="text-xs text-gray-500">
          {t('form.coordinates_label')} {formatCoordinates(field.state.value)}
        </p>
      )}
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
