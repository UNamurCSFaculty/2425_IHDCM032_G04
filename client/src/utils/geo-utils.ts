import cities from '@/data/cities.json'
import regions from '@/data/regions.json'

export type GeoPointString = `POINT(${number} ${number})`

/**
 * Parse une chaîne de géopoint WKT en coordonnées latitude/longitude
 * @param geoPoint - Chaîne au format "POINT(longitude latitude)"
 * @returns Objet avec lat/lng ou null si format invalide
 */
export function parseGeoPoint(
  geoPoint: GeoPointString | string | null | undefined
): { lat: number; lng: number } | null {
  // Vérification du format POINT(...)
  if (!geoPoint || !geoPoint.startsWith('POINT(') || !geoPoint.endsWith(')')) {
    return null
  }

  // Extraction des coordonnées entre parenthèses
  const coords = geoPoint.substring(6, geoPoint.length - 1).split(' ')
  if (coords.length !== 2) {
    return null
  }

  // Conversion en nombres (longitude puis latitude dans WKT)
  const lng = parseFloat(coords[0])
  const lat = parseFloat(coords[1])
  if (isNaN(lat) || isNaN(lng)) {
    return null
  }

  return { lat, lng }
}

/**
 * Trouve l'ID d'une ville par son nom (sensible à la casse)
 * @param name - Nom de la ville à rechercher
 * @returns ID de la ville ou null si non trouvée
 */
export function getCityIdByName(name: string): number | null {
  const idx = cities.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? idx + 1 : null
}

/**
 * Trouve l'ID d'une ville par son nom (insensible aux accents)
 * @param name - Nom de la ville à rechercher
 * @returns ID de la ville ou null si non trouvée
 */
export function getCityIdByNameAccentInsensitive(name: string): number | null {
  const idx = cities.findIndex(
    e => e.localeCompare(name, undefined, { sensitivity: 'base' }) === 0
  )
  return idx !== -1 ? idx + 1 : null
}

/**
 * Trouve une ville complète par son nom
 * @param name - Nom de la ville à rechercher
 * @returns Objet ville avec id et nom ou null si non trouvée
 */
export function getCityByName(
  name: string
): { id: number; name: string } | null {
  const idx = cities.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? { id: idx + 1, name: cities[idx] } : null
}

/**
 * Trouve l'ID d'une région par son nom
 * @param name - Nom de la région à rechercher
 * @returns ID de la région ou null si non trouvée
 */
export function getRegionIdByName(name: string): number | null {
  const idx = regions.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? idx + 1 : null
}

/**
 * Trouve une région complète par son nom
 * @param name - Nom de la région à rechercher
 * @returns Objet région avec id et nom ou null si non trouvée
 */
export function getRegionByName(
  name: string
): { id: number; name: string } | null {
  const idx = regions.findIndex(e => e.toLowerCase() === name.toLowerCase())
  return idx !== -1 ? { id: idx + 1, name: regions[idx] } : null
}
