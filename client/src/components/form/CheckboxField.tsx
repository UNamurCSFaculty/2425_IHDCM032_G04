import { useFieldContext } from '.'
import { FieldErrors } from './field-errors'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { cn } from '@/lib/utils'
import React from 'react'
import SimpleTooltip from '../SimpleTooltip'

/* -------------------------------------------------------------------------- */
/* Types                                                                      */
/* -------------------------------------------------------------------------- */

export type Choice = { value: string; label: string }

/**
 * Les props “rest” sont celles d’un <div>; elles ne sont plus passées au
 * <fieldset> (uniquement au <div> pour la case unique).
 */
export interface CheckboxFieldProps
  extends Omit<React.HTMLAttributes<HTMLDivElement>, 'children' | 'disabled'> {
  label?: string
  tooltip?: string
  choices?: Choice[]
  direction?: 'row' | 'col'
  multiple?: boolean
  required?: boolean
  disabled?: boolean
}

type FieldValue = boolean | string | string[]

/* -------------------------------------------------------------------------- */
/* Composant                                                                  */
/* -------------------------------------------------------------------------- */

export const CheckboxField: React.FC<CheckboxFieldProps> = ({
  label,
  required,
  tooltip,
  choices,
  direction = 'col',
  multiple = false,
  className,
  disabled,
  ...rest // ⚠️ Propres au <div> seulement
}) => {
  const field = useFieldContext<FieldValue>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  /* ---------------------------------------------------------------------- */
  /* 1.  Case unique                                                        */
  /* ---------------------------------------------------------------------- */
  if (!choices) {
    return (
      <div className={cn('space-y-2', className)} {...rest}>
        <div className="flex items-center gap-2">
          <Checkbox
            id={field.name}
            checked={Boolean(field.state.value)}
            onCheckedChange={v => field.handleChange(Boolean(v))}
            onBlur={() => field.handleBlur()} // ✅
            disabled={disabled}
            className={cn(hasError && '!border-red-500')}
          />
          {label && (
            <Label htmlFor={field.name}>
              {label}
              {required && <span className="text-red-500">*</span>}
              {tooltip && <SimpleTooltip content={tooltip} />}
            </Label>
          )}
        </div>
        <FieldErrors meta={field.state.meta} />
      </div>
    )
  }

  /* ---------------------------------------------------------------------- */
  /* 2.  Groupe de cases                                                    */
  /* ---------------------------------------------------------------------- */

  const current: FieldValue =
    field.state.value ?? (multiple ? ([] as string[]) : '')

  const toggle = (val: string, checked: boolean) => {
    if (multiple) {
      const next = checked
        ? [...(current as string[]), val]
        : (current as string[]).filter(v => v !== val)
      field.handleChange(next)
    } else {
      field.handleChange(checked ? val : '')
    }
  }

  const isChecked = (val: string) =>
    multiple ? (current as string[]).includes(val) : (current as string) === val

  return (
    // ⏩ plus de “…rest” ici !
    <fieldset className={cn('space-y-2', className)}>
      {label && (
        <legend className="mb-1 text-sm font-medium">
          {label}
          {required && <span className="ml-0.5 text-red-500">*</span>}
          {tooltip && <SimpleTooltip content={tooltip} />}
        </legend>
      )}

      <div
        className={cn(
          'flex gap-4',
          direction === 'col' ? 'flex-col' : 'flex-row flex-wrap'
        )}
      >
        {choices.map(c => (
          <label key={c.value} className="flex items-center gap-2 text-sm">
            <Checkbox
              checked={isChecked(c.value)}
              onCheckedChange={v => toggle(c.value, Boolean(v))}
              onBlur={() => field.handleBlur()}
              disabled={disabled}
              value={c.value}
            />
            {c.label}
          </label>
        ))}
      </div>

      <FieldErrors meta={field.state.meta} />
    </fieldset>
  )
}
