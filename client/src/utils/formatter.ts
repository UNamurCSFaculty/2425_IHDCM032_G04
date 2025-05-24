import { decimalToSexagesimal } from 'geolib'

/**
 * Formate un nombre en notation compacte (ex : 1,2 M, 3,4 k)
 * en utilisant la locale fr-BJ.
 */
export const formatPrice = new Intl.NumberFormat('fr-BJ', {
  notation: 'compact', // compacte → k, M…
  compactDisplay: 'long', // affichage court (“k”)
  maximumFractionDigits: 2, // 1 chiffre après la virgule si besoin
  style: 'currency', // on reste en “currency” pour XOF
  currency: 'CFA', // ISO 4217 pour CFA BCEAO
  currencyDisplay: 'code', // “XOF” plutôt que le symbole “F”
})

/**
 * Formate un poids (en kg) en kg / t / kt selon la grandeur.
 *
 * @param {number} weightKg — le poids en kilogrammes
 * @param {string} [locale='fr-BJ'] — locale pour le formattage numérique
 * @param {number} [maxFractionDigits=1] — décimales maximales pour t et kt
 * @returns {string} poids formaté (ex : "850 kg", "2,3 t", "1,2 kt")
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
    // en kilogrammes
    return `${fmt(weightKg)} kg`
  }

  if (weightKg < 1_000_000) {
    // en tonnes (1 t = 1 000 kg)
    const tonnes = weightKg / 1_000
    return `${fmt(tonnes)} t`
  }

  // au-delà, en kilotonnes (1 kt = 1 000 t = 1 000 000 kg)
  const kilotonnes = weightKg / 1_000_000
  return `${fmt(kilotonnes)} kt`
}

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
