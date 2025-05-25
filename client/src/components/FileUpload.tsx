import { acceptedFileTypes, cn, getFileIcon } from '@/lib/utils'
import { Upload, X } from 'lucide-react'
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

  /** Traduit la chaîne accept en tableau de patterns */
  const parseAccept = (str: string) =>
    str
      .split(',')
      .map(s => s.trim())
      .filter(Boolean)

  /** Vérifie si un fichier correspond à un pattern accept */
  const matchesAccept = (file: File, patterns: string[]) => {
    // pattern ".ext"
    const name = file.name.toLowerCase()
    const type = file.type.toLowerCase()
    return patterns.some(pat => {
      pat = pat.toLowerCase()
      if (pat === '*/*') return true
      if (pat.startsWith('.')) {
        return name.endsWith(pat)
      }
      if (pat.endsWith('/*')) {
        return type.startsWith(pat.replace('*', ''))
      }
      return type === pat
    })
  }

  /* ------------------------------------------------------------------ */
  /* ajout de fichiers avec validation de type, taille et nombre         */
  /* ------------------------------------------------------------------ */
  const addFiles = (incoming: File[]) => {
    setError(null)

    // 1. Vérifier le type
    const patterns = parseAccept(accept)
    const invalidType = incoming.filter(f => !matchesAccept(f, patterns))
    if (invalidType.length) {
      setError(
        t('form.upload_invalid_type', {
          types: patterns.join(', '),
        })
      )
      return
    }

    // 2. Vérifier le nombre max
    if (files.length + incoming.length > maxFiles) {
      setError(t('form.upload_limit', { max: maxFiles }))
      return
    }

    // 3. Vérifier la taille max
    const oversized = incoming.filter(f => f.size > maxSize * 1024 * 1024)
    if (oversized.length) {
      setError(t('form.upload_too_big', { max: maxSize }))
      return
    }

    // 4. Tout est valide : on ajoute
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
    // réinitialiser pour permettre de re-sélectionner le même fichier
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
            'flex cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed p-6 transition-colors',
            borderClass
          )}
        >
          <Upload className="text-muted-foreground mb-2 h-10 w-10" />
          <p className="mb-1 text-sm font-medium">
            {t('form.upload_click_or_drag')}
          </p>
          <p className="text-muted-foreground mb-2 text-xs">
            {t('form.upload_accepted')} :{' '}
            {acceptedFileTypes(accept, t).join(', ')} ({maxSize}
            MB max)
          </p>
          <p className="text-muted-foreground text-xs">
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

        {error && <p className="text-destructive mt-2 text-sm">{error}</p>}
      </div>

      {files.length > 0 && (
        <ul className="mt-4 space-y-2">
          {files.map((file, idx) => (
            <li
              key={`${file.name}-${idx}`}
              className="bg-muted flex items-center justify-between rounded-md p-3"
            >
              <div className="flex items-center space-x-2 truncate">
                {getFileIcon(file)}
                <span className="max-w-[200px] truncate text-sm">
                  {file.name}
                </span>
                <span className="text-muted-foreground text-xs">
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
}
