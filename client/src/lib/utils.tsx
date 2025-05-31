import { type ClassValue, clsx } from 'clsx'
import type { TFunction } from 'i18next'
import { FileIcon, FileText } from 'lucide-react'
import { twMerge } from 'tailwind-merge'

// Tailwind helper
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

// -----------------------------------------------------------------------------
// WKT helpers
// -----------------------------------------------------------------------------
export function wktToLatLon(wkt?: string): [number, number] | null {
  if (!wkt) return null
  const m = wkt.match(/^POINT\s*\(\s*([+-]?[\d.]+)\s+([+-]?[\d.]+)\s*\)$/)
  if (!m) return null
  const lon = parseFloat(m[1])
  const lat = parseFloat(m[2])
  return isNaN(lat) || isNaN(lon) ? null : [lat, lon]
}

export function getFileIcon(file: File) {
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

export function formatFileSize(bytes?: number) {
  if (!bytes || bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Number.parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export function acceptedFileTypes(accept: string, t: TFunction): string[] {
  if (!accept) return []
  return accept
    .split(',')
    .map(type => type.trim())
    .map(type => _acceptedFileTypeToLabel(type, t))
    .filter(label => label !== '')
}

function _acceptedFileTypeToLabel(type: string, t: TFunction): string {
  if (type === 'image/*') return t('form.file_type.image')
  if (type === 'application/pdf') return t('form.file_type.pdf')
  if (type.startsWith('application/vnd.openxmlformats-officedocument')) {
    return t('form.file_type.office_document')
  }
  if (type.startsWith('application/msword')) {
    return t('form.file_type.word_document')
  }
  if (type.startsWith('application/vnd.ms-excel')) {
    return t('form.file_type.excel_spreadsheet')
  }
  return type
}

export enum TradeStatus {
  OPEN = 'Ouvert',
  EXPIRED = 'Expiré',
  ACCEPTED = 'Accepté',
  REJECTED = 'Refusé',
}

//export type DeepPartial<T> = {
//  [K in keyof T]?: T[K] extends object ? DeepPartial<T[K]> : T[K]
//}

export type DeepPartial<T> = {
  [K in keyof T]?: T[K] extends (...args: any[]) => any // Si c'est une fonction, garde-la telle quelle
    ? T[K]
    : // Si c'est un tableau, transforme-le en tableau d'éléments draftés
      T[K] extends Array<infer U>
      ? Array<DeepPartial<U>>
      : // Si c'est un objet (autre qu'un tableau ou une fonction), descends dedans récursivement
        T[K] extends object
        ? DeepPartial<T[K]>
        : // Sinon (primitif), garde le type d'origine
          T[K]
}

export function capitalizeFirstLetter(str: string): string {
  if (!str) return str
  return str.charAt(0).toUpperCase() + str.slice(1)
}

export const calculatePasswordStrength = (password: string): number => {
  if (!password) return 0

  let strength = 0

  // Length check
  if (password.length >= 8) strength += 1
  if (password.length >= 12) strength += 1

  // Character type checks
  if (/[A-Z]/.test(password)) strength += 1
  if (/[a-z]/.test(password)) strength += 1
  if (/[0-9]/.test(password)) strength += 1
  if (/[^A-Za-z0-9]/.test(password)) strength += 1

  return Math.min(5, strength)
}

export const getCategoryBackground = (categoryName?: string): string => {
  if (!categoryName) {
    return 'bg-gradient-to-br from-gray-500 to-gray-700'
  }

  const normalizedCategory = categoryName.toLowerCase().trim()

  switch (normalizedCategory) {
    case 'alertes terrain':
      return 'bg-gradient-to-br from-red-500 via-orange-500 to-red-600'

    case 'marché & prix':
    case 'marche & prix':
      return 'bg-gradient-to-br from-green-500 via-emerald-500 to-green-600'

    case 'recherche & innovations':
      return 'bg-gradient-to-br from-blue-500 via-indigo-500 to-purple-600'

    case 'formation & ressources':
      return 'bg-gradient-to-br from-yellow-500 via-amber-500 to-orange-500'

    default: {
      const hash = categoryName
        .split('')
        .reduce((acc, char) => acc + char.charCodeAt(0), 0)
      const gradients = [
        'bg-gradient-to-br from-pink-500 to-rose-600',
        'bg-gradient-to-br from-purple-500 to-indigo-600',
        'bg-gradient-to-br from-cyan-500 to-blue-600',
        'bg-gradient-to-br from-teal-500 to-green-600',
        'bg-gradient-to-br from-orange-500 to-red-600',
      ]
      return gradients[hash % gradients.length]
    }
  }
}

export const getCategoryTextColor = (): string => {
  return 'text-white'
}
