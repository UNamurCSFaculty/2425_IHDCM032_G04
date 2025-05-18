import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { X } from 'lucide-react'
import type React from 'react'
import { useRef, useState } from 'react'

interface AvatarUploadProps {
  className?: string
  onChange?: (file: File | null) => void
}

export function AvatarUpload({ className, onChange }: AvatarUploadProps) {
  const [preview, setPreview] = useState<string | null>(null)
  const [, setFile] = useState<File | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0] || null

    if (selectedFile) {
      setFile(selectedFile)
      const reader = new FileReader()
      reader.onload = () => {
        setPreview(reader.result as string)
      }
      reader.readAsDataURL(selectedFile)
      onChange?.(selectedFile)
    }
  }

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault()
      handleClick()
    }
  }

  const handleRemove = () => {
    setPreview(null)
    setFile(null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
    onChange?.(null)
  }

  const handleClick = () => {
    fileInputRef.current?.click()
  }

  return (
    <div className={cn('flex flex-col items-center gap-4', className)}>
      <div className="relative">
        {preview ? (
          <div className="relative">
            <div className="h-32 w-32 rounded-full overflow-hidden border-2 border-border">
              <img
                src={preview || '/placeholder.svg'}
                alt="Avatar preview"
                className="object-cover"
              />
            </div>
            <button
              type="button"
              onClick={handleRemove}
              className="absolute top-0 right-0 bg-destructive text-destructive-foreground rounded-full p-1 transform translate-x-1/3 -translate-y-1/3 shadow-sm"
              aria-label="Remove image"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        ) : (
          <div
            className="h-32 w-32 rounded-full bg-muted flex items-center justify-center border-2 border-dashed border-border cursor-pointer"
            onClick={handleClick}
            onKeyDown={handleKeyDown}
            role="button"
            tabIndex={0}
          >
            <span className="text-muted-foreground text-sm">Upload avatar</span>
          </div>
        )}
      </div>

      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileChange}
        accept="image/*"
        className="hidden"
        aria-label="Upload avatar"
      />

      {!preview && (
        <Button type="button" variant="outline" size="sm" onClick={handleClick}>
          Select Image
        </Button>
      )}
    </div>
  )
}
