// Direction de tri possible
export type SortDirection = 'asc' | 'desc'

// Configuration pour le tri des données
export interface SortConfig<T> {
  sortKey: keyof T | null
  sortDirection: SortDirection
  dateColumns?: (keyof T)[] // Colonnes contenant des dates sous forme de chaînes
}

/**
 * Trie un tableau de données selon la configuration fournie
 * @param data - Tableau de données à trier
 * @param config - Configuration du tri (clé, direction, colonnes de dates)
 * @returns Nouveau tableau trié
 */
export function sortData<T extends Record<string, any>>(
  data: T[],
  config: SortConfig<T>
): T[] {
  const { sortKey, sortDirection, dateColumns = [] } = config

  if (!sortKey) {
    return data
  }

  return [...data].sort((a, b) => {
    const valA = a[sortKey]
    const valB = b[sortKey]

    let comparison = 0

    // Gestion des valeurs nulles ou undefined
    if (valA === null || valA === undefined) {
      comparison = -1
    } else if (valB === null || valB === undefined) {
      comparison = 1
    }
    // Gestion des dates (colonnes spécifiées ou détection automatique)
    else if (
      dateColumns.includes(sortKey) ||
      (typeof valA === 'string' &&
        typeof valB === 'string' &&
        (sortKey as string).toLowerCase().includes('date'))
    ) {
      const timeA_val = valA ? new Date(valA as string).getTime() : 0
      const timeB_val = valB ? new Date(valB as string).getTime() : 0

      const resolvedTimeA = isNaN(timeA_val)
        ? valA
          ? -Infinity
          : 0
        : timeA_val
      const resolvedTimeB = isNaN(timeB_val)
        ? valB
          ? -Infinity
          : 0
        : timeB_val
      comparison = resolvedTimeA - resolvedTimeB
    }
    // Tri alphabétique pour les chaînes
    else if (typeof valA === 'string' && typeof valB === 'string') {
      comparison = valA.localeCompare(valB)
    }
    // Tri des booléens (true avant false)
    else if (typeof valA === 'boolean' && typeof valB === 'boolean') {
      comparison = valA === valB ? 0 : valA ? -1 : 1
    }
    // Tri numérique
    else if (typeof valA === 'number' && typeof valB === 'number') {
      comparison = valA - valB
    }

    // Application de la direction de tri
    return sortDirection === 'asc' ? comparison : comparison * -1
  })
}
