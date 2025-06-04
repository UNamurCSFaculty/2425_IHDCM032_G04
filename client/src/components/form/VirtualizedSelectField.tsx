import { useFieldContext } from '.'
import { FieldErrors } from './field-errors'
import VirtualizedSelect from '@/components/VirtualizedSelect'
import React from 'react'

type VirtualizedSelectFieldProps = Omit<
  React.ComponentProps<typeof VirtualizedSelect>,
  'value' | 'onChange' | 'id'
> & {
  /** Nom visible et accessible (sera passé tel quel au composant) */
  label: string
  tooltip?: string
  hint?: string
  required?: boolean
  modal?: boolean
}

export const VirtualizedSelectField: React.FC<VirtualizedSelectFieldProps> = ({
  label,
  options,
  tooltip,
  hint,
  required = false,
  placeholder,
  modal = false,
  ...rest
}) => {
  const field = useFieldContext<number | null>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <>
      <VirtualizedSelect
        {...rest}
        id={field.name}
        label={label}
        options={options}
        tooltip={tooltip}
        hint={hint}
        placeholder={placeholder}
        required={required}
        value={field.state.value}
        modal={modal}
        onChange={val => field.handleChange(val)}
      />
      {hasError && <FieldErrors meta={field.state.meta} />}
    </>
  )
}
