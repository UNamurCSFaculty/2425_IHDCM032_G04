import { type ClassValue, clsx } from 'clsx'
import type { TFunction } from 'i18next'
import { FileIcon, FileText } from 'lucide-react'
import { twMerge } from 'tailwind-merge'

/**
 * Utilitaire pour combiner les classes CSS avec Tailwind et clsx
 * @param inputs - Classes CSS à combiner
 * @returns String de classes CSS optimisées
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

/**
 * Convertit une chaîne WKT POINT en coordonnées latitude/longitude
 * @param wkt - Chaîne WKT au format POINT(longitude latitude)
 * @returns Tableau [latitude, longitude] ou null si invalide
 */
export function wktToLatLon(wkt?: string): [number, number] | null {
  if (!wkt) return null
  const m = wkt.match(/^POINT\s*\(\s*([+-]?[\d.]+)\s+([+-]?[\d.]+)\s*\)$/)
  if (!m) return null
  const lon = parseFloat(m[1])
  const lat = parseFloat(m[2])
  return isNaN(lat) || isNaN(lon) ? null : [lat, lon]
}

/**
 * Retourne l'icône appropriée selon l'extension du fichier
 * @param file - Objet File
 * @returns Composant React d'icône avec couleur appropriée
 */
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

/**
 * Formate la taille d'un fichier en unités lisibles
 * @param bytes - Taille en octets
 * @returns Chaîne formatée (ex: "1.5 MB")
 */
export function formatFileSize(bytes?: number) {
  if (!bytes || bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Number.parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * Convertit les types MIME acceptés en labels lisibles
 * @param accept - Chaîne des types MIME acceptés
 * @param t - Fonction de traduction i18next
 * @returns Tableau de labels traduits
 */
export function acceptedFileTypes(accept: string, t: TFunction): string[] {
  if (!accept) return []
  return accept
    .split(',')
    .map(type => type.trim())
    .map(type => _acceptedFileTypeToLabel(type, t))
    .filter(label => label !== '')
}

/**
 * Convertit un type MIME en label traduit
 * @param type - Type MIME
 * @param t - Fonction de traduction
 * @returns Label traduit ou type original
 */
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

/**
 * Énumération des statuts de transaction commerciale
 */
export enum TradeStatus {
  OPEN = 'Ouvert',
  EXPIRED = 'Expiré',
  ACCEPTED = 'Accepté',
  REJECTED = 'Refusé',
}

/**
 * Type utilitaire pour rendre toutes les propriétés d'un objet optionnelles de manière récursive
 * Préserve les fonctions et traite spécifiquement les tableaux
 */
export type DeepPartial<T> = {
  [K in keyof T]?: T[K] extends (...args: any[]) => any
    ? T[K]
    : T[K] extends Array<infer U>
      ? Array<DeepPartial<U>>
      : T[K] extends object
        ? DeepPartial<T[K]>
        : T[K]
}

/**
 * Met en majuscule la première lettre d'une chaîne
 * @param str - Chaîne à traiter
 * @returns Chaîne avec première lettre en majuscule
 */
export function capitalizeFirstLetter(str: string): string {
  if (!str) return str
  return str.charAt(0).toUpperCase() + str.slice(1)
}

/**
 * Calcule la force d'un mot de passe selon plusieurs critères
 * @param password - Mot de passe à évaluer
 * @returns Score de 0 à 5 (longueur, majuscule, minuscule, chiffre, caractère spécial)
 */
export function calculatePasswordStrength(password: string): number {
  if (!password || password.length === 0) {
    return 0
  }

  let score = 0

  if (password.length >= 8) {
    score += 1
  }

  if (/[A-Z]/.test(password)) {
    score += 1
  }

  if (/[a-z]/.test(password)) {
    score += 1
  }

  if (/[0-9]/.test(password)) {
    score += 1
  }

  if (/[^A-Za-z0-9]/.test(password)) {
    score += 1
  }

  return score
}

/**
 * Retourne une classe CSS de dégradé selon le nom de catégorie
 * @param categoryName - Nom de la catégorie
 * @returns Classe CSS de dégradé Tailwind
 */
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

/**
 * Retourne la couleur de texte pour les catégories (toujours blanc)
 * @returns Classe CSS de couleur de texte
 */
export const getCategoryTextColor = (): string => {
  return 'text-white'
}

/**
 * Calcule le prix par kilogramme
 * @param price - Prix total
 * @param quantity - Quantité en kg
 * @returns Prix par kg arrondi à l'entier inférieur
 */
export const getPricePerKg = (price: number, quantity: number) => {
  if (quantity <= 0) return 0
  return Math.floor(price / quantity)
}
