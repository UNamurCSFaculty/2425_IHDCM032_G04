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
import type { LanguageDtoReadable } from '@/api/generated'
import { useAppData } from '@/store/appStore'
import React from 'react'
import { useTranslation } from 'react-i18next'

type SelectLanguageFieldProps = {
  disabled: boolean
  required?: boolean
} & React.InputHTMLAttributes<HTMLInputElement>

export function SelectLanguageField({
  disabled,
  required = true,
}: SelectLanguageFieldProps) {
  const field = useFieldContext<LanguageDtoReadable>()
  const appData = useAppData()
  const { t } = useTranslation()
  const currentId = field.state.value.id?.toString()

  return (
    <div className="space-y-1">
      <Label htmlFor={field.name}>
        {t('language')}
        {required && <span className="text-red-500">*</span>}
      </Label>
      <Select
        value={currentId}
        onValueChange={val => {
          const id = Number(val)
          const langObj = appData.languages.find(l => l.id === id)!
          field.handleChange(langObj)
        }}
      >
        <SelectTrigger
          className="w-full"
          id={field.name}
          onBlur={field.handleBlur}
        >
          <SelectValue placeholder="Choisissez une langue" />
        </SelectTrigger>
        <SelectContent>
          {appData.languages.map(l => (
            <SelectItem key={l.id} value={l.id.toString()} disabled={disabled}>
              {t('languages.' + l.code)}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
