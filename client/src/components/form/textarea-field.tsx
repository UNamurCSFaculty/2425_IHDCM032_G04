import { useFieldContext } from '.'
import { Label } from '../ui/label'
import { Textarea } from '../ui/textarea'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import React from 'react'
import SimpleTooltip from '../SimpleTooltip'

type TextAreaProps = {
  label: string
  required?: boolean
  tooltip?: string
} & React.TextareaHTMLAttributes<HTMLTextAreaElement>

/**
 * Composant de champ de saisie de texte multilignes.
 */
export const TextAreaField = ({
  label,
  className,
  tooltip,
  required = true,
  ...restProps
}: TextAreaProps) => {
  const field = useFieldContext<string>()

  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={field.name}>
          {label}
          {required && <span className="text-red-500">*</span>}
          {tooltip && <SimpleTooltip content={tooltip} />}
        </Label>
        <Textarea
          id={field.name}
          name={field.name}
          value={field.state.value}
          onChange={e => field.handleChange(e.target.value)}
          onBlur={field.handleBlur}
          className={cn(
            className,
            'h-32 resize-none',
            hasError && '!border-red-500'
          )}
          {...restProps}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
