// -----------------------------------------------------------------------------
// Lazy-loaded cities / regions with localStorage cache
// -----------------------------------------------------------------------------
// Simple JSON import (no async fetch) -----------------------------------------
import cityNamesJson from '@/data/cities.json'
import regionNamesJson from '@/data/regions.json'
import { type ClassValue, clsx } from 'clsx'
import { decimalToSexagesimal } from 'geolib'
import { twMerge } from 'tailwind-merge'

// Tailwind helper

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

// -----------------------------------------------------------------------------
// Date & coords formatters
// -----------------------------------------------------------------------------
export const formatDate = (dateString: string | undefined): string => {
  if (!dateString) return '—'
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
    /^POINT\s*\(\s*([+-]?\d+(?:\.\d+)?)\s+([+-]?\d+(?:\.\d+)?)\s*\)$/
  )
  if (!match) return wktString
  const lon = parseFloat(match[1])
  const lat = parseFloat(match[2])
  if (isNaN(lon) || isNaN(lat)) return wktString
  const latDMS = decimalToSexagesimal(lat)
  const lonDMS = decimalToSexagesimal(lon)
  return `${latDMS} ${lat >= 0 ? 'N' : 'S'} ${lonDMS} ${lon >= 0 ? 'E' : 'W'}`
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

export enum TradeStatus {
  OPEN = 'Ouvert',
  EXPIRED = 'Expiré',
  ACCEPTED = 'Accepté',
  REJECTED = 'Refusé',
}

// -----------------------------------------------------------------------------

// -----------------------------------------------------------------------------

export const cityNames = cityNamesJson as string[]
export const regionNames = regionNamesJson as string[]

// helpers retained for legacy async API ---------------------------------------
export const getCities = async (): Promise<string[]> => cityNames
export const getRegions = async (): Promise<string[]> => regionNames
