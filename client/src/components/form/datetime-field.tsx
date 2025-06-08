import { useFieldContext } from '.'
import { Label } from '../ui/label'
import { FieldErrors } from './field-errors'
import {
  DateTimePicker,
  type DateTimePickerProps,
} from '@/components/ui/datetime-picker'
import { cn } from '@/lib/utils'
import { enUS, fr } from 'date-fns/locale'
import React from 'react'
import { useTranslation } from 'react-i18next'
import SimpleTooltip from '../SimpleTooltip'

type DateTimePickerFieldProps = {
  label: string
  tooltip?: string
  required?: boolean
} & Omit<DateTimePickerProps, 'value' | 'onChange'>

/**
 *
 * Composant de champ de sélection de date et heure.
 */
export const DateTimePickerField = ({
  label,
  tooltip,
  required = true,
  className,
  ...restProps
}: DateTimePickerFieldProps) => {
  const { t, i18n } = useTranslation()
  const field = useFieldContext<string>()

  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  const dateValue = React.useMemo<Date | undefined>(() => {
    return field.state.value ? new Date(field.state.value) : undefined
  }, [field.state.value])

  const handleChange = (date?: Date) => {
    if (date) {
      const isoLocal = date.toISOString()
      field.handleChange(isoLocal)
    } else {
      field.handleChange('')
    }
    field.handleBlur()
  }

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={field.name}>
          {label}
          {required && <span className="text-red-500">*</span>}
          {tooltip && <SimpleTooltip content={tooltip} />}
        </Label>
        <DateTimePicker
          value={dateValue}
          onChange={handleChange}
          hourCycle={24}
          locale={i18n.language === 'fr-FR' ? fr : enUS}
          className={cn(className, hasError && '!border-red-500')}
          placeholder={t('form.placeholder.date')}
          {...restProps}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
