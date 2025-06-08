import { useFieldContext } from '.'
import SimpleTooltip from '../SimpleTooltip'
import { Input } from '../ui/input'
import { Label } from '../ui/label'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import { type LucideIcon } from 'lucide-react'
import React from 'react'

type NumberFieldProps = {
  startIcon?: LucideIcon
  endIcon?: LucideIcon
  label: string
  tooltip?: string
} & React.InputHTMLAttributes<HTMLInputElement>

/**
 * Composant de champ de saisie numérique.
 * Permet de saisir un nombre avec des icônes optionnelles au début et à la fin.
 */
export const NumberField = ({
  label,
  startIcon,
  tooltip,
  endIcon,
  className,
  ...restProps
}: NumberFieldProps) => {
  const field = useFieldContext<number>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={field.name}>
          {label}
          {tooltip && <SimpleTooltip content={tooltip} />}
        </Label>
        <Input
          type="number"
          id={field.name}
          value={field.state.value}
          onChange={e => {
            const value = e.target.value
            field.handleChange(value === '' ? 0 : Number(value))
          }}
          onBlur={field.handleBlur}
          startIcon={startIcon}
          endIcon={endIcon}
          className={cn(className, hasError && '!border-red-500')}
          {...restProps}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
