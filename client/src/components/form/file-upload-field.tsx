import { useFieldContext } from '.'
import { FileUpload, type FileUploadProps } from '@/components/FileUpload'

type FileUploadFieldProps = {
  /** Libellé affiché au-dessus du champ */
  label: string
} & Omit<FileUploadProps, 'onChange' | 'className'>

export function FileUploadField({
  label,
  ...uploadProps
}: FileUploadFieldProps) {
  const field = useFieldContext<File[]>()

  return (
    <div className="space-y-2">
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

      {/* gestion d’erreurs standard */}
      {field.state.meta.isTouched &&
        field.state.meta.errors.map((err, i) => (
          <p key={i} className="text-destructive text-sm">
            {err.message}
          </p>
        ))}
    </div>
  )
}
