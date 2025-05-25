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
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
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
