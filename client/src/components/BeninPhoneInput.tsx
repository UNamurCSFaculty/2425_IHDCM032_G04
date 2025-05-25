import { Input } from './ui/input'
import { Label } from '@/components/ui/label'
import { cn } from '@/lib/utils'
import React, { useEffect, useRef, useState } from 'react'
import SimpleTooltip from './SimpleTooltip'

interface BeninPhoneInputProps {
  value?: string
  onChange?: (value: string) => void
  onBlur?: (value: string) => void
  label?: string
  tooltip?: string
  required?: boolean
  id?: string
  className?: string
}

export function BeninPhoneInput({
  value,
  onChange,
  onBlur,
  className,
  label = 'Phone Number',
  tooltip,
  required = false,
  id = 'phone',
}: BeninPhoneInputProps) {
  const initialDigits = value ? value.replace(/^\+?22901/, '') : ''
  const [digits, setDigits] = useState(initialDigits)
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (value !== undefined) {
      const newDigits = value.replace(/^\+?22901/, '')
      if (newDigits !== digits) setDigits(newDigits)
    }
  }, [value, digits])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const only = e.target.value.replace(/\D/g, '').slice(0, 8)
    setDigits(only)
    onChange?.(`+22901${only}`)
  }

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const only = e.target.value.replace(/\D/g, '').slice(0, 8)
    setDigits(only)
    onBlur?.(`+22901${only}`)
  }

  const formatWithSpaces = (s: string) => s.replace(/(\d{2})(?=\d)/g, '$1 ')

  const focusInput = () => inputRef.current?.focus()

  return (
    <div>
      {label && (
        <Label htmlFor={id} className="mb-1 block">
          {label} {required && <span className="text-red-500">*</span>}
          {tooltip && <SimpleTooltip content={tooltip} />}
        </Label>
      )}

      <div
        onClick={focusInput}
        onKeyDown={e => {
          if (e.key === 'Enter' || e.key === ' ') focusInput()
        }}
        role="button"
        tabIndex={-1}
        className={cn(
          'focus-within:ring-ring bg-background relative flex items-stretch rounded-md border',
          'focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]',
          'aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive',
          className
        )}
      >
        {/* Préfixe non éditable */}
        <div className="bg-muted flex items-center justify-center rounded-l-md border-r px-2 text-xs font-medium whitespace-nowrap text-neutral-700">
          (+229) 01
        </div>

        {/* Wrapper relatif */}
        <div className="relative flex-1">
          {/* Mask en monospace, même letter-spacing */}
          <div className="pointer-events-none absolute inset-y-0 top-1 left-0 flex items-center pl-2 font-mono text-base tracking-widest text-neutral-300 select-none md:text-sm">
            __ __ __ __
          </div>

          {/* Input transparent, même monospace + tracking */}
          <Input
            id={id}
            ref={inputRef}
            type="text"
            inputMode="numeric"
            value={formatWithSpaces(digits)}
            onChange={handleChange}
            onBlur={handleBlur}
            className="relative z-10 w-full border-0 bg-transparent px-2 py-1 font-mono text-base tracking-widest outline-none"
            style={{ boxShadow: 'none' }}
            required={required}
          />
        </div>
      </div>
    </div>
  )
}
