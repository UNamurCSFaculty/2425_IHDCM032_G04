import { LoaderIcon } from 'lucide-react'
import { useFieldContext } from '.'
import { Label } from '../ui/label'
import MultipleSelector, { type Option } from '../ui/multiple-selector'
import { FieldErrors } from './field-errors'

type MultipleSelectorOption = {
  value: string
  label: string
}

type MultipleSelectorFieldProps = {
  label: string
  hint?: string
  options: MultipleSelectorOption[]
  placeholder?: string
  className?: string
  required?: boolean
  disabled?: boolean
  loading?: boolean
  onChange?: ((options: Option[]) => void) | undefined
}

export function MultipleSelectorField({
  label,
  hint,
  options,
  placeholder,
  className = 'w-full',
  loading = false,
  onChange,
}: MultipleSelectorFieldProps) {
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
        <MultipleSelector
          className={className}
          options={options.map(opt => ({
            label: opt.label,
            value: opt.value,
          }))}
          placeholder={placeholder}
          onChange={onChange}
          emptyIndicator={
            <p className="text-center text-lg leading-10 text-gray-600 dark:text-gray-400">
              No data.
            </p>
          }
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
