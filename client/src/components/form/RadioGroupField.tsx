import { useFieldContext } from '.'
import { FieldErrors } from './field-errors'
import { Label } from '@/components/ui/label'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { cn } from '@/lib/utils'
import React from 'react'
import SimpleTooltip from '../SimpleTooltip'

/* -------------------------------------------------------------------------- */
/* Types                                                                      */
/* -------------------------------------------------------------------------- */

// On peut réutiliser ce type, il est parfait pour notre besoin.
export type Choice = { value: string; label: string }

/**
 * Les props sont basées sur un <fieldset>
 */
export interface RadioGroupFieldProps
  extends Omit<React.HTMLAttributes<HTMLFieldSetElement>, 'onChange'> {
  label: string
  tooltip?: string
  choices: Choice[]
  direction?: 'row' | 'col'
  required?: boolean
  disabled?: boolean
}

// La valeur d'un groupe de radio est toujours une chaîne de caractères
type FieldValue = string

/* -------------------------------------------------------------------------- */
/* Composant                                                                  */
/* -------------------------------------------------------------------------- */

export const RadioGroupField: React.FC<RadioGroupFieldProps> = ({
  label,
  required,
  tooltip,
  choices,
  direction = 'col',
  className,
  disabled,
  ...rest // Props passées au <fieldset>
}) => {
  const field = useFieldContext<FieldValue>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  // La valeur est directement gérée par TanStack Form.
  // Le `onValueChange` du RadioGroup de shadcn/ui correspond parfaitement à `field.handleChange`.
  return (
    <fieldset className={cn('space-y-2', className)} {...rest}>
      <legend className="mb-1 text-sm font-medium">
        {label}
        {required && <span className="ml-0.5 text-red-500">*</span>}
        {tooltip && <SimpleTooltip content={tooltip} />}
      </legend>

      <RadioGroup
        value={field.state.value ?? ''}
        onValueChange={field.handleChange}
        onBlur={field.handleBlur}
        disabled={disabled}
        className={cn(
          'flex gap-4',
          direction === 'col' ? 'flex-col' : 'flex-row flex-wrap',
          hasError && '[&_button]:border-red-500' // Applique une bordure rouge en cas d'erreur
        )}
      >
        {choices.map(choice => (
          <Label
            key={choice.value}
            htmlFor={`${field.name}-${choice.value}`}
            className="flex items-center gap-2 text-sm font-normal"
          >
            <RadioGroupItem
              value={choice.value}
              id={`${field.name}-${choice.value}`}
            />
            {choice.label}
          </Label>
        ))}
      </RadioGroup>

      <FieldErrors meta={field.state.meta} />
    </fieldset>
  )
}
