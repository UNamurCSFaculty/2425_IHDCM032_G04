import { useFieldContext } from '.'
import { Input } from '../ui/input'
import { Label } from '../ui/label'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import { Eye, EyeOff, type LucideIcon } from 'lucide-react'
import React, { useState } from 'react'

type TextFieldProps = {
  startIcon?: LucideIcon
  endIcon?: LucideIcon
  label: string
  required?: boolean
  castNumber?: boolean
  fieldType?: 'string' | 'number'
  type?: React.HTMLInputTypeAttribute
} & React.InputHTMLAttributes<HTMLInputElement>

export function TextField<T extends string | number>({
  label,
  startIcon,
  endIcon,
  className,
  required = true,
  fieldType = 'string',
  type = 'text',
  ...restProps
}: TextFieldProps) {
  const field = useFieldContext<T>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0
  const [showPassword, setShowPassword] = useState(false)
  const _parse = (v: string): T => {
    if (fieldType === 'number') {
      const parsed = Number(v)
      return isNaN(parsed) ? (v as T) : (parsed as T)
    }
    return v as T
  }

  const effectiveType = type === 'password' && showPassword ? 'text' : type

  const onEndIconClick = () => {
    if (type === 'password') {
      changePasswordVisibility()
    }
  }

  const changePasswordVisibility = () => {
    setShowPassword(!showPassword)
  }

  if (type === 'password') {
    endIcon = showPassword ? Eye : EyeOff
  }

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
          onChange={e => {
            const parsedVal = _parse(e.target.value)
            field.handleChange(parsedVal as T)
          }}
          onBlur={field.handleBlur}
          startIcon={startIcon}
          endIcon={endIcon}
          onEndIconClick={onEndIconClick}
          className={cn(className, hasError && '!border-red-500')}
          type={effectiveType}
          {...restProps}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
