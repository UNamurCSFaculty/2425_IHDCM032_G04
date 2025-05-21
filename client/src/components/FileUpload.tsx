import { cn } from '@/lib/utils'
import { FileIcon, FileText, Upload, X } from 'lucide-react'
import React, { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'

export interface FileUploadProps {
  maxFiles?: number
  maxSize?: number // MB
  accept?: string
  onChange?: (files: File[]) => void
  className?: string
}

export function FileUpload({
  maxFiles = 5,
  maxSize = 5,
  accept = '*/*',
  onChange,
  className,
}: FileUploadProps) {
  const { t } = useTranslation()
  const [files, setFiles] = useState<File[]>([])
  const [error, setError] = useState<string | null>(null)
  const [isDragging, setIsDragging] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)

  /* ------------------------------------------------------------------ */
  /* utilitaires partagés                                               */
  /* ------------------------------------------------------------------ */
  const addFiles = (incoming: File[]) => {
    setError(null)

    if (files.length + incoming.length > maxFiles) {
      setError(t('form.upload_limit', { max: maxFiles }))
      return
    }

    const oversized = incoming.filter(f => f.size > maxSize * 1024 * 1024)
    if (oversized.length) {
      setError(t('form.upload_too_big', { size: maxSize }))
      return
    }

    const merged = [...files, ...incoming]
    setFiles(merged)
    onChange?.(merged)
  }

  /* ------------------------------------------------------------------ */
  /* événements bouton / clavier                                         */
  /* ------------------------------------------------------------------ */
  const openFileDialog = () => fileInputRef.current?.click()
  const handleKeyDown: React.KeyboardEventHandler<HTMLDivElement> = e => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault()
      openFileDialog()
    }
  }

  /* ------------------------------------------------------------------ */
  /* sélection via le dialogue natif                                     */
  /* ------------------------------------------------------------------ */
  const handleFileChange: React.ChangeEventHandler<HTMLInputElement> = e => {
    if (e.target.files) addFiles(Array.from(e.target.files))
    // réinitialiser pour permettre de sélectionner à nouveau le même fichier
    if (fileInputRef.current) fileInputRef.current.value = ''
  }

  /* ------------------------------------------------------------------ */
  /* drag & drop                                                         */
  /* ------------------------------------------------------------------ */
  const handleDragOver: React.DragEventHandler<HTMLDivElement> = e => {
    e.preventDefault()
    e.dataTransfer.dropEffect = 'copy'
    setIsDragging(true)
  }
  const handleDragLeave: React.DragEventHandler<HTMLDivElement> = () =>
    setIsDragging(false)

  const handleDrop: React.DragEventHandler<HTMLDivElement> = e => {
    e.preventDefault()
    setIsDragging(false)
    if (e.dataTransfer.files) addFiles(Array.from(e.dataTransfer.files))
  }

  /* ------------------------------------------------------------------ */
  /* rendu                                                               */
  /* ------------------------------------------------------------------ */
  const borderClass = error
    ? 'border-destructive'
    : isDragging
      ? 'border-primary'
      : 'border-border hover:border-primary/50'

  return (
    <div className={cn('w-full', className)}>
      <div className="mb-4">
        <div
          role="button"
          tabIndex={0}
          onClick={openFileDialog}
          onKeyDown={handleKeyDown}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          className={cn(
            'border-2 border-dashed rounded-lg p-6 flex flex-col items-center justify-center cursor-pointer transition-colors',
            borderClass
          )}
        >
          <Upload className="h-10 w-10 text-muted-foreground mb-2" />
          <p className="text-sm font-medium mb-1">
            {t('form.upload_click_or_drag')}
          </p>
          <p className="text-xs text-muted-foreground mb-2">
            {accept.replace(/\*/g, t('form.upload_all'))} – {maxSize}
            MB max
          </p>
          <p className="text-xs text-muted-foreground">
            {files.length}/{maxFiles} {t('form.upload_files')}
          </p>
        </div>

        <input
          ref={fileInputRef}
          type="file"
          onChange={handleFileChange}
          accept={accept}
          multiple={maxFiles > 1}
          className="hidden"
          disabled={files.length >= maxFiles}
        />

        {error && <p className="text-destructive text-sm mt-2">{error}</p>}
      </div>

      {files.length > 0 && (
        <ul className="space-y-2 mt-4">
          {files.map((file, idx) => (
            <li
              key={`${file.name}-${idx}`}
              className="flex items-center justify-between p-3 bg-muted rounded-md"
            >
              <div className="flex items-center space-x-2 truncate">
                {getIcon(file)}
                <span className="text-sm truncate max-w-[200px]">
                  {file.name}
                </span>
                <span className="text-xs text-muted-foreground">
                  {(file.size / 1_048_576).toFixed(2)} MB
                </span>
              </div>
              <button
                type="button"
                onClick={() =>
                  setFiles(c => {
                    const up = c.filter((_, i) => i !== idx)
                    onChange?.(up)
                    return up
                  })
                }
                className="text-muted-foreground hover:text-destructive"
              >
                <X className="h-4 w-4" />
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )

  /* ------------------------------------------------------------------ */
  /* helpers                                                             */
  /* ------------------------------------------------------------------ */
  function getIcon(file: File) {
    const ext = file.name.split('.').pop()?.toLowerCase() ?? ''
    if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'].includes(ext))
      return <FileIcon className="h-5 w-5 text-blue-500" />
    if (ext === 'pdf') return <FileIcon className="h-5 w-5 text-red-500" />
    if (['doc', 'docx'].includes(ext))
      return <FileIcon className="h-5 w-5 text-blue-700" />
    if (['xls', 'xlsx'].includes(ext))
      return <FileIcon className="h-5 w-5 text-green-600" />
    return <FileText className="h-5 w-5 text-gray-500" />
  }
}
