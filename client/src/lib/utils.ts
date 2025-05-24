import { type ClassValue, clsx } from 'clsx'
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

// -----------------------------------------------------------------------------
// Database enum types
// -----------------------------------------------------------------------------
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

export const productTypes: ProductType[] = [
  ProductType.HARVEST,
  ProductType.TRANSFORMED,
]
