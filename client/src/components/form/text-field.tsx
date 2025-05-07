import { useFieldContext } from '.'
import { Input } from '../ui/input'
import { Label } from '../ui/label'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import type { LucideIcon } from 'lucide-react'
import React from 'react'

type TextFieldProps = {
  startIcon?: LucideIcon
  endIcon?: LucideIcon
  label: string
  required?: boolean
} & React.InputHTMLAttributes<HTMLInputElement>

export const TextField = ({
  label,
  startIcon,
  endIcon,
  className,
  required = true,
  ...restProps
}: TextFieldProps) => {
  const field = useFieldContext<string>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={field.name}>
          {label}
          {required && <span className="text-red-500">*</span>}
        </Label>
        <Input
          id={field.name}
          value={field.state.value}
          onChange={e => field.handleChange(e.target.value)}
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
