import { Input } from './ui/input'
import { Label } from '@/components/ui/label'
import { cn } from '@/lib/utils'
import type React from 'react'
import { useEffect, useRef, useState } from 'react'

interface BeninPhoneInputProps {
  value?: string
  onChange?: (value: string) => void
  onBlur?: (value: string) => void
  label?: string
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
  required = false,
  id = 'phone',
}: BeninPhoneInputProps) {
  // Initialize with just the digits after the prefix, if any
  const initialDigits = value ? value.replace(/^\+?22901/, '') : ''
  const [digits, setDigits] = useState(initialDigits)
  const inputRef = useRef<HTMLInputElement>(null)

  // Update internal state when external value changes
  useEffect(() => {
    if (value !== undefined) {
      const newDigits = value.replace(/^\+?22901/, '')
      if (newDigits !== digits) {
        setDigits(newDigits)
      }
    }
  }, [value, digits])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value

    // Only allow digits and limit to 8 characters
    const digitsOnly = newValue.replace(/\D/g, '').slice(0, 8)
    setDigits(digitsOnly)

    // Call the onChange with the complete phone number
    if (onChange) {
      onChange(`+22901${digitsOnly}`)
    }
  }

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const newValue = e.target.value

    // Only allow digits and limit to 8 characters
    const digitsOnly = newValue.replace(/\D/g, '').slice(0, 8)
    setDigits(digitsOnly)

    if (onBlur) {
      onBlur(e.target.value)
    }
  }

  // Format digits with spaces for display: XX XX XX XX
  const formatWithSpaces = (input: string) => {
    let result = ''
    for (let i = 0; i < input.length; i++) {
      if (i > 0 && i % 2 === 0) {
        result += ' '
      }
      result += input[i]
    }
    return result
  }

  // Focus the input when clicking on the container
  const handleContainerClick = () => {
    if (inputRef.current) {
      inputRef.current.focus()
    }
  }

  return (
    <div>
      {label && (
        <div className="mb-1">
          <Label htmlFor={id}>
            {label} {required && <span className="text-red-500">*</span>}
          </Label>
        </div>
      )}

      <div
        className={cn(
          'focus-within:ring-ring bg-background flex items-stretch rounded-md border',
          'focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]',
          'aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive',
          className
        )}
        onClick={handleContainerClick}
        onKeyDown={e => {
          if (e.key === 'Enter' || e.key === ' ') {
            handleContainerClick()
          }
        }}
        role="button"
        tabIndex={-1}
      >
        {/* Non-editable prefix */}
        <div className="bg-muted flex items-center justify-center rounded-l-md border-r px-2 text-xs font-medium whitespace-nowrap text-neutral-700">
          (+229) 01
        </div>

        {/* Input for the 8 remaining digits */}
        <Input
          tabIndex={0}
          ref={inputRef}
          id={id}
          type="text"
          inputMode="numeric"
          value={formatWithSpaces(digits)}
          onChange={handleChange}
          onBlur={handleBlur}
          className="border-0 bg-transparent outline-none"
          style={{ boxShadow: 'none' }}
          placeholder="XX XX XX XX"
          required={required}
        />
      </div>

      {/*<p className="text-xs text-gray-500">Enter 8 digits after (+229) 01</p>*/}
    </div>
  )
}
