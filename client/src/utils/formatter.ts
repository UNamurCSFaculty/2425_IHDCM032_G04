import { decimalToSexagesimal } from 'geolib'

/**
 * Formateur de prix en francs CFA avec notation compacte
 * Utilise la locale fr-BJ pour afficher les montants au format local
 * Exemples: 1 200 → "1,2 k XOF", 1 500 000 → "1,5 M XOF"
 */
export const formatPrice = new Intl.NumberFormat('fr-BJ', {
  notation: 'compact',
  compactDisplay: 'long',
  maximumFractionDigits: 2,
  style: 'currency',
  currency: 'CFA',
  currencyDisplay: 'code',
})

/**
 * Formate un poids en kg/t/kt selon la grandeur
 * Adapte automatiquement l'unité selon la valeur pour une meilleure lisibilité
 * @param weightKg - Le poids en kilogrammes
 * @param locale - Locale pour le formattage numérique
 * @param maxFractionDigits - Décimales maximales pour t et kt
 * @returns Poids formaté avec unité appropriée
 */
export function formatWeight(
  weightKg: number,
  locale = 'fr-BJ',
  maxFractionDigits = 1
) {
  const fmt = (value: number) =>
    value.toLocaleString(locale, {
      maximumFractionDigits: maxFractionDigits,
      minimumFractionDigits: 0,
    })

  if (weightKg < 1_000) {
    return `${fmt(weightKg)} kg`
  }

  if (weightKg < 1_000_000) {
    const tonnes = weightKg / 1_000
    return `${fmt(tonnes)} t`
  }

  const kilotonnes = weightKg / 1_000_000
  return `${fmt(kilotonnes)} kt`
}

/**
 * Formate une date avec heure au format français
 * @param dateString - Chaîne de date ISO ou undefined
 * @returns Date formatée "JJ/MM/AAAA HH:MM" ou "—" si vide
 */
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

/**
 * Formate une date sans heure au format français
 * @param dateString - Chaîne de date ISO ou undefined
 * @returns Date formatée "JJ/MM/AAAA" ou "—" si vide
 */
export const formatDateOnly = (dateString: string | undefined): string => {
  if (!dateString) return '—'
  return new Date(dateString).toLocaleString('fr-FR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

/**
 * Convertit des coordonnées WKT en format degrés-minutes-secondes
 * Transforme un point WKT en coordonnées géographiques lisibles
 * @param wktString - Chaîne WKT au format "POINT(longitude latitude)"
 * @returns Coordonnées formatées en DMS avec directions cardinales
 */
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
