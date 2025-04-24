import React from 'react'
import { useFieldContext } from '.'
import { Label } from '../ui/label'
import { FieldErrors } from './field-errors'
import { Textarea } from '../ui/textarea'
import { cn } from '@/lib/utils'

type TextAreaProps = {
  label: string
} & React.TextareaHTMLAttributes<HTMLTextAreaElement>

export const TextAreaField = ({
  label,
  className,
  ...restProps
}: TextAreaProps) => {
  const field = useFieldContext<string>()

  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={field.name}>{label}</Label>
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
