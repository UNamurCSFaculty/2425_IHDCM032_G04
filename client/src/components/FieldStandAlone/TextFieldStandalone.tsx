import { Input } from '../ui/input'
import { Label } from '../ui/label'
import { cn } from '@/lib/utils'
import type { LucideIcon } from 'lucide-react'
import React, { useState } from 'react'

type TextFieldStandaloneProps = {
  startIcon?: LucideIcon
  endIcon?: LucideIcon
  label: string
  required?: boolean
  onChange?: (value: string) => void
} & React.InputHTMLAttributes<HTMLInputElement>

/**
 * Composant de champ de texte autonome.
 * Permet de saisir du texte avec des icônes facultatives au début et à la fin.
 * Gère la validation de champ requis et l'affichage d'erreurs.
 */
export const TextFieldStandalone = ({
  label,
  startIcon,
  endIcon,
  className,
  required = true,
  onChange,
  ...restProps
}: TextFieldStandaloneProps) => {
  const [value, setValue] = useState('')
  const [touched, setTouched] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValue(e.target.value)
    if (onChange) onChange(e.target.value)
    if (required && touched && e.target.value.trim() === '') {
      setError('Ce champ est requis')
    } else {
      setError(null)
    }
  }

  const handleBlur = () => {
    setTouched(true)
    if (required && value.trim() === '') {
      setError('Ce champ est requis')
    } else {
      setError(null)
    }
  }

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={restProps.id || label}>
          {label}
          {required && <span className="text-red-500">*</span>}
        </Label>
        <Input
          id={restProps.id || label}
          value={value}
          onChange={handleChange}
          onBlur={handleBlur}
          startIcon={startIcon}
          endIcon={endIcon}
          className={cn(className, error && '!border-red-500')}
          {...restProps}
        />
      </div>
      {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  )
}
