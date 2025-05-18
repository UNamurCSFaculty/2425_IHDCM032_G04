'use client'

import { cn } from '@/lib/utils'
import { FileIcon, FileText, Upload, X } from 'lucide-react'
import type React from 'react'
import { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'

export interface FileUploadProps {
  maxFiles?: number
  maxSize?: number // in MB
  accept?: string
  onChange?: (files: File[]) => void
  className?: string
}

export function FileUpload({
  maxFiles = 5,
  maxSize = 5, // 5MB default
  accept = '*/*',
  onChange,
  className,
}: FileUploadProps) {
  const { t } = useTranslation()
  const [files, setFiles] = useState<File[]>([])
  const [error, setError] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleClick = () => {
    fileInputRef.current?.click()
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault()
      handleClick()
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(e.target.files || [])
    setError(null)

    // Check if adding these files would exceed the max files limit
    if (files.length + selectedFiles.length > maxFiles) {
      setError(`You can only upload a maximum of ${maxFiles} files`)
      return
    }

    // Check file sizes
    const oversizedFiles = selectedFiles.filter(
      file => file.size > maxSize * 1024 * 1024
    )

    if (oversizedFiles.length > 0) {
      setError(`Some files exceed the maximum size of ${maxSize}MB`)
      return
    }

    const updatedFiles = [...files, ...selectedFiles]
    setFiles(updatedFiles)
    onChange?.(updatedFiles)

    // Reset the file input
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  const removeFile = (index: number) => {
    const updatedFiles = files.filter((_, i) => i !== index)
    setFiles(updatedFiles)
    onChange?.(updatedFiles)
  }

  const getFileExtension = (filename: string) => {
    return filename.split('.').pop()?.toLowerCase() || ''
  }

  const getFileIcon = (file: File) => {
    const ext = getFileExtension(file.name)

    if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'].includes(ext)) {
      return <FileIcon className="h-5 w-5 text-blue-500" />
    } else if (['pdf'].includes(ext)) {
      return <FileIcon className="h-5 w-5 text-red-500" />
    } else if (['doc', 'docx'].includes(ext)) {
      return <FileIcon className="h-5 w-5 text-blue-700" />
    } else if (['xls', 'xlsx'].includes(ext)) {
      return <FileIcon className="h-5 w-5 text-green-600" />
    } else {
      return <FileText className="h-5 w-5 text-gray-500" />
    }
  }

  return (
    <div className={cn('w-full', className)}>
      <div className="mb-4">
        <div
          role="button"
          tabIndex={0}
          onClick={handleClick}
          onKeyDown={handleKeyDown}
          className={cn(
            'border-2 border-dashed rounded-lg p-6 flex flex-col items-center justify-center cursor-pointer',
            error
              ? 'border-destructive'
              : 'border-border hover:border-primary/50'
          )}
        >
          <Upload className="h-10 w-10 text-muted-foreground mb-2" />
          <p className="text-sm font-medium mb-1">
            Click to upload or drag and drop
            {t('form.upload_component')}
          </p>
          <p className="text-xs text-muted-foreground mb-2">
            {accept.replace(/\*/g, 'All')} (Max: {maxSize}MB)
          </p>
          <p className="text-xs text-muted-foreground">
            {files.length}/{maxFiles} files
          </p>
        </div>

        <input
          type="file"
          ref={fileInputRef}
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
          {files.map((file, index) => (
            <li
              key={`${file.name}-${index}`}
              className="flex items-center justify-between p-3 bg-muted rounded-md"
            >
              <div className="flex items-center space-x-2 truncate">
                {getFileIcon(file)}
                <span className="text-sm truncate max-w-[200px]">
                  {file.name}
                </span>
                <span className="text-xs text-muted-foreground">
                  {(file.size / (1024 * 1024)).toFixed(2)}MB
                </span>
              </div>
              <button
                type="button"
                onClick={() => removeFile(index)}
                className="text-muted-foreground hover:text-destructive"
                aria-label={`Remove ${file.name}`}
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
