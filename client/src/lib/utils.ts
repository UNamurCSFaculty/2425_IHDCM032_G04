import { type ClassValue, clsx } from 'clsx'
import { decimalToSexagesimal } from 'geolib'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const formatDate = (dateString: string | undefined): string => {
  if (!dateString) {
    return '—'
  }

  return new Date(dateString).toLocaleString('fr-FR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export const formatCoordinates = (wktString: string): string => {
  const match = wktString.match(
    /^POINT\s*\(\s*([+-]?\d+(\.\d+)?)\s+([+-]?\d+(\.\d+)?)\s*\)$/
  )
  if (!match) return wktString

  const longitude = parseFloat(match[1])
  const latitude = parseFloat(match[3])
  if (isNaN(longitude) || isNaN(latitude)) return wktString

  const latDMS = decimalToSexagesimal(latitude)
  const lonDMS = decimalToSexagesimal(longitude)

  const coordinates = `${latDMS} ${latitude >= 0 ? 'N' : 'S'} ${lonDMS} ${longitude >= 0 ? 'E' : 'W'}`
  return coordinates
}

export enum TradeStatus {
  OPEN = 'Ouvert',
  EXPIRED = 'Expiré',
  ACCEPTED = 'Accepté',
  REJECTED = 'Refusé',
}

export enum ProductType {
  HARVEST = 'harvest',
  TRANSFORMED = 'transformed',
}
