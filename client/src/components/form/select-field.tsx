import { useFieldContext } from '.'
import { Label } from '../ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select'
import { FieldErrors } from './field-errors'
import SimpleTooltip from '../SimpleTooltip'
import { LoaderCircle } from 'lucide-react'

type SelectOption<T> = {
  value: T
  label: string
}

type SelectFieldProps<T extends string | number> = {
  label: string
  hint?: string
  options: SelectOption<T>[]
  placeholder?: string
  tooltip?: string
  className?: string
  required?: boolean
  disabled?: boolean
  loading?: boolean
  /**
   * Comment transformer la string reçue du composant en T.
   * Par défaut, identity pour string et Number(v) pour number.
   */
  parseValue?: (v: string) => T
  onChange?: (value: T) => void
}

export function SelectField<T extends string | number>({
  label,
  hint,
  options,
  placeholder,
  className = 'w-full',
  tooltip,
  required = true,
  disabled = false,
  loading = false,
  parseValue,
  onChange,
}: SelectFieldProps<T>) {
  const field = useFieldContext<T>()

  // la fonction par défaut qui transforme "val" en T
  const _parse =
    parseValue ??
    ((v: string) =>
      // si on attend un number, transforme ; sinon renvoie la string
      typeof options[0].value === 'number'
        ? (Number(v) as unknown as T)
        : (v as unknown as T))

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <div className="flex items-center justify-between">
          <Label htmlFor={field.name}>
            {label}
            {loading && <LoaderCircle className="animate-spin" />}
            {required && <span className="text-red-500">*</span>}
            {tooltip && <SimpleTooltip content={tooltip} />}
          </Label>
          {hint && (
            <span className="text-xs font-semibold text-gray-500">{hint}</span>
          )}
        </div>
        <Select
          // on passe toujours une string au <Select>
          disabled={disabled}
          value={String(field.state.value ?? '')}
          onValueChange={v => {
            const newVal = _parse(v)
            field.handleChange(newVal)
            field.handleBlur()
            onChange?.(newVal)
          }}
        >
          <SelectTrigger
            className={className}
            id={field.name}
            onBlur={field.handleBlur}
          >
            <SelectValue placeholder={placeholder} />
          </SelectTrigger>
          <SelectContent>
            {options.map(opt => (
              <SelectItem key={String(opt.value)} value={String(opt.value)}>
                {opt.label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
