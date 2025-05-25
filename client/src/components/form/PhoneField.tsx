/* PhoneField.tsx --------------------------------------------------------- */
import { useFieldContext } from '.'
import { BeninPhoneInput } from '../BeninPhoneInput'
// le context créé par createFormHook
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import React from 'react'

type PhoneFieldProps = Omit<
  React.ComponentProps<typeof BeninPhoneInput>,
  'value' | 'onChange' | 'id'
> & { id?: string }

export function PhoneField({ id, className, ...rest }: PhoneFieldProps) {
  const field = useFieldContext<string>() // valeur string dans le store
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <BeninPhoneInput
          /* value & onChange pilotés par TanStack Form */
          id={id ?? field.name}
          value={field.state.value ?? ''}
          onChange={v => field.handleChange(v)}
          onBlur={field.handleBlur}
          /* styling d’erreur éventuel */
          className={cn(className, hasError && 'ring-destructive ring-1')}
          {...rest}
        />
        <FieldErrors meta={field.state.meta} />
      </div>
    </div>
  )
}
