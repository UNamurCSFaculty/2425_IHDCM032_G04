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
import React from "react";

type Language = {
  id: number
  langue: string
}

const languages: Language[] = [
  { id: 1352, langue: 'fr'},
  { id: 2, langue: 'en'},
]

type SelectLanguageFieldProps = {
    disabled: boolean
} & React.InputHTMLAttributes<HTMLInputElement>

export function SelectLanguageField({
    disabled}: SelectLanguageFieldProps) {
  const field = useFieldContext<Language>()
  console.log(field.state.value)
  const currentId = field.state.value.id.toString()

  return (
    <div className="space-y-1">
      <Label htmlFor={field.name}>Langue</Label>
      <Select
        value={currentId}
        onValueChange={val => {
          const id = Number(val)
          const langObj = languages.find(l => l.id === id)!
          field.handleChange(langObj)
        }}
      >
        <SelectTrigger className="w-full" id={field.name} onBlur={field.handleBlur}>
          <SelectValue placeholder="Choisissez une langue" />
        </SelectTrigger>
        <SelectContent>
          {languages.map(l => (
            <SelectItem key={l.id} value={l.id.toString()} disabled={disabled}>
              {l.langue}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
