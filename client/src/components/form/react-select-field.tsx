import { LoaderIcon } from 'lucide-react'
import { useFieldContext } from '.'
import { Label } from '../ui/label'
import Select, { createFilter } from 'react-select'
import { FieldErrors } from './field-errors'

type SelectOption = {
  value: string
  label: string
}

type SelectFieldProps = {
  label: string
  hint?: string
  options: SelectOption[]
  placeholder?: string
  className?: string
  required?: boolean
  disabled?: boolean
  loading?: boolean
  onChange?: ((options: string[]) => void) | undefined
}

/**
 * Composant de champ de sélection avec React Select.
 * Permet de sélectionner plusieurs options à partir d'une liste déroulante.
 */
export function ReactSelectField({
  label,
  hint,
  options,
  placeholder,
  className = 'w-full',
  loading = false,
  onChange,
}: SelectFieldProps) {
  const field = useFieldContext()

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <div className="flex items-center justify-between">
          <Label htmlFor={field.name}>
            {label}
            {loading && <LoaderIcon className="animate-spin" />}
          </Label>
          {hint && (
            <span className="text-xs font-semibold text-gray-500">{hint}</span>
          )}
        </div>
        <Select
          className={className}
          options={options.map(opt => ({
            label: opt.label,
            value: opt.value,
          }))}
          placeholder={placeholder}
          onChange={options => {
            const values = options.map(o => o.value)
            onChange?.(values)
          }}
          isMulti={true}
          isSearchable={true}
          filterOption={createFilter({ ignoreAccents: false })}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
