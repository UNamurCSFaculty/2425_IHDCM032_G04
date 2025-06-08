import { LoaderCircle } from 'lucide-react'
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
  selectAllLabel?: string
  searchLabel?: string
  closeLabel?: string
  clearLabel?: string
  emptySearchLabel?: string
  className?: string
  required?: boolean
  disabled?: boolean
  maxCount?: number
  loading?: boolean
  parseValue?: (v: string) => T
  onChange?: (value: (string | number)[]) => void
}

/**
 * Composant de champ de sélection multiple.
 * Permet de sélectionner plusieurs options à partir d'une liste déroulante.
 */
export function MultiSelectField<T extends string | number>({
  label,
  hint,
  options,
  placeholder,
  selectAllLabel,
  searchLabel,
  closeLabel,
  clearLabel,
  emptySearchLabel,
  className = 'w-full',
  required = true,
  maxCount = 3,
  loading = false,
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
            {loading && <LoaderCircle className="animate-spin" />}
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
            onChange?.(newValues)
          }}
          placeholder={placeholder}
          selectAllLabel={selectAllLabel}
          searchLabel={searchLabel}
          closeLabel={closeLabel}
          clearLabel={clearLabel}
          emptySearchLabel={emptySearchLabel}
          variant="inverted"
          maxCount={maxCount}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
