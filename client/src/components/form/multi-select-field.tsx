import { useFieldContext } from '.'
import { Label } from '../ui/label'
import { MultiSelect } from '../ui/multi-select'
import { FieldErrors } from './field-errors'

type MultiSelectOption<T> = {
  value: T
  label: string
}

type MultiSelectFieldProps<T extends string | number> = {
  label: string
  hint?: string
  options: MultiSelectOption<T>[]
  placeholder?: string
  className?: string
  required?: boolean
  disabled?: boolean
  maxCount?: number
  parseValue?: (v: string) => T
  onChange?: (value: (string | number)[]) => void
}

export function MultiSelectField<T extends string | number>({
  label,
  hint,
  options,
  placeholder,
  className = 'w-full',
  required = true,
  maxCount = 3,
  parseValue,
  onChange,
}: MultiSelectFieldProps<T>) {
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
            {required && <span className="text-red-500">*</span>}
          </Label>
          {hint && (
            <span className="text-xs font-semibold text-gray-500">{hint}</span>
          )}
        </div>
        <MultiSelect
          className={className}
          options={options.map(opt => ({
            label: opt.label,
            value: String(opt.value),
          }))}
          onValueChange={values => {
            const newValues = []
            for (const val of values) {
              const newVal = _parse(val)
              newValues.push(newVal)
            }
            console.log('newValues', newValues)
            // field.handleChange(newValues) TODO TYPE ERROR
            onChange?.(newValues)
          }}
          placeholder={placeholder}
          variant="inverted"
          maxCount={maxCount}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
