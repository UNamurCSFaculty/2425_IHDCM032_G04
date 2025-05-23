import { Label } from '../ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select'

type SelectOption<T> = {
  value: T
  label: string
}

type SelectFieldStandaloneProps<T extends string | number> = {
  label: string
  name: string
  value: T | null | undefined
  onChange: (value: T) => void
  options: SelectOption<T>[]
  placeholder?: string
  className?: string
  required?: boolean
  disabled?: boolean
  parseValue?: (v: string) => T
}

export function SelectFieldStandalone<T extends string | number>({
  label,
  name,
  value,
  onChange,
  options,
  placeholder,
  className = 'w-full',
  required = true,
  disabled = false,
  parseValue,
}: SelectFieldStandaloneProps<T>) {
  const _parse =
    parseValue ??
    ((v: string) =>
      typeof options[0].value === 'number'
        ? (Number(v) as unknown as T)
        : (v as unknown as T))

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={name}>
          {label}
          {required && <span className="text-red-500">*</span>}
        </Label>
        <Select
          disabled={disabled}
          value={value != null ? String(value) : ''}
          onValueChange={v => {
            const newVal = _parse(v)
            onChange(newVal)
          }}
        >
          <SelectTrigger className={className} id={name}>
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
    </div>
  )
}
