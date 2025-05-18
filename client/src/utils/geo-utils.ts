import cities from '@/data/cities.json'
import regions from '@/data/regions.json'

export type GeoPointString = `POINT(${number} ${number})`

export function parseGeoPoint(
  geoPoint: GeoPointString | string | null | undefined
): { lat: number; lng: number } | null {
  if (!geoPoint || !geoPoint.startsWith('POINT(') || !geoPoint.endsWith(')')) {
    return null
  }
  const coords = geoPoint.substring(6, geoPoint.length - 1).split(' ')
  if (coords.length !== 2) {
    return null
  }
  const lng = parseFloat(coords[0])
  const lat = parseFloat(coords[1])
  if (isNaN(lat) || isNaN(lng)) {
    return null
  }
  return { lat, lng }
}

export function getCityIdByName(name: string): number | null {
  const idx = cities.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? idx + 1 : null // +1 pour ton id
}

export function getCityIdByNameAccentInsensitive(name: string): number | null {
  const idx = cities.findIndex(
    e => e.localeCompare(name, undefined, { sensitivity: 'base' }) === 0
  )
  return idx !== -1 ? idx + 1 : null
}

export function getCityByName(
  name: string
): { id: number; name: string } | null {
  const idx = cities.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? { id: idx + 1, name: cities[idx] } : null // +1 pour ton id
}

export function getRegionIdByName(name: string): number | null {
  const idx = regions.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? idx + 1 : null // +1 pour ton id
}
export function getRegionByName(
  name: string
): { id: number; name: string } | null {
  const idx = regions.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? { id: idx + 1, name: regions[idx] } : null // +1 pour ton id
}
