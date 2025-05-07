import { useFieldContext } from '@/components/form'
import { FieldErrors } from '@/components/form/field-errors'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { cn } from '@/lib/utils'
import { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'

interface FileUploadButtonProps {
  label: string
  onFileSelected: (file: File) => void
  disabled?: boolean
  required?: boolean
}

export function FileUploadButton({
  label,
  onFileSelected,
  disabled = false,
  required = false,
}: FileUploadButtonProps) {
  const field = useFieldContext<File>()
  const inputRef = useRef<HTMLInputElement>(null)
  const [fileName, setFileName] = useState<string>('')
  const { t } = useTranslation()

  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  const handleClick = () => {
    inputRef.current?.click()
    field.handleBlur()
  }

  return (
    <div className="space-y-2">
      <div className="space-y-1">
        <Label htmlFor={field.name}>
          {label}
          {required && <span className="text-red-500">*</span>}
        </Label>
        <div className="flex items-center space-x-2">
          <Input
            id={`${field.name}-display`}
            readOnly
            placeholder={t('form.noFile')}
            value={fileName}
            disabled={disabled}
            className={cn(hasError && '!border-red-500')}
          />
          <Button
            type="button"
            variant="default"
            onClick={handleClick}
            disabled={disabled}
            className={cn(hasError && '!border-red-500')}
          >
            {t('buttons.parcourir')}
          </Button>
        </div>
        <Input
          id={field.name}
          type="file"
          className="hidden"
          ref={inputRef}
          onChange={e => {
            const file = e.target.files?.[0]
            if (file) {
              setFileName(file.name)
              onFileSelected(file)
              field.handleChange(file)
            }
          }}
          disabled={disabled}
          required={required}
        />
      </div>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
