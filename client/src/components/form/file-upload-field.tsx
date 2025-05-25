import { useFieldContext } from '.'
import { FileUpload, type FileUploadProps } from '@/components/FileUpload'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'

type FileUploadFieldProps = {
  /** Libellé affiché au-dessus du champ */
  label: string
  className?: string
} & Omit<FileUploadProps, 'onChange' | 'className'>

export function FileUploadField({
  label,
  className = '',
  ...uploadProps
}: FileUploadFieldProps) {
  const field = useFieldContext<File[]>()

  return (
    <div className={cn('space-y-2', className)}>
      <label className="block text-sm font-medium">{label}</label>

      <FileUpload
        {...uploadProps}
        className={
          field.state.meta.isTouched && field.state.meta.errors.length
            ? 'border-destructive'
            : undefined
        }
        onChange={files => field.handleChange(files)}
      />

      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
