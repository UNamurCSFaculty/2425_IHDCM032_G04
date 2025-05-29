import { useFieldContext } from '.'
import { FileUpload, type FileUploadProps } from '@/components/FileUpload'
import { FieldErrors } from './field-errors'
import { cn } from '@/lib/utils'
import SimpleTooltip from '../SimpleTooltip'
import { Label } from '../ui/label'

type FileUploadFieldProps = {
  /** Libellé affiché au-dessus du champ */
  label: string
  tooltip?: string
  className?: string
  required?: boolean
} & Omit<FileUploadProps, 'onChange' | 'className'>

export function FileUploadField({
  label,
  tooltip,
  className = '',
  required = true,
  ...uploadProps
}: FileUploadFieldProps) {
  const field = useFieldContext<File[]>()

  return (
    <div className={cn('space-y-2', className)}>
      <Label className="block text-sm font-medium">
        {label}

        {tooltip && <SimpleTooltip content={tooltip} />}
      </Label>

      <FileUpload
        {...uploadProps}
        className={
          field.state.meta.isTouched && field.state.meta.errors.length
            ? 'border-destructive'
            : undefined
        }
        onChange={files => field.handleChange(files)}
      />

      {required && <FieldErrors meta={field.state.meta} />}
    </div>
  )
}
