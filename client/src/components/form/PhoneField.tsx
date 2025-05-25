import { useFieldContext } from '.'
import { BeninPhoneInput } from '../BeninPhoneInput'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import React from 'react'

type PhoneFieldProps = Omit<
  React.ComponentProps<typeof BeninPhoneInput>,
  'value' | 'onChange' | 'id'
> & { id?: string; tooltip?: string }

export function PhoneField({
  id,
  tooltip,
  className,
  ...rest
}: PhoneFieldProps) {
  const field = useFieldContext<string>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <BeninPhoneInput
          id={id ?? field.name}
          value={field.state.value ?? ''}
          onChange={v => field.handleChange(v)}
          tooltip={tooltip}
          onBlur={field.handleBlur}
          className={cn(className, hasError && 'ring-destructive ring-1')}
          {...rest}
        />
        <FieldErrors meta={field.state.meta} />
      </div>
    </div>
  )
}
