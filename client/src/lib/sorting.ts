export type SortDirection = 'asc' | 'desc'

export interface SortConfig<T> {
  sortKey: keyof T | null
  sortDirection: SortDirection
  dateColumns?: (keyof T)[] // Optional: specify columns that are date strings
}

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

    if (valA === null || valA === undefined) {
      comparison = -1
    } else if (valB === null || valB === undefined) {
      comparison = 1
    }
    // Handle date strings if the column is specified in dateColumns
    // or if the sortKey itself implies it's a date (e.g., 'registrationDate')
    else if (
      dateColumns.includes(sortKey) ||
      (typeof valA === 'string' &&
        typeof valB === 'string' &&
        (sortKey as string).toLowerCase().includes('date')) // Heuristic for date columns
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
    } else if (typeof valA === 'string' && typeof valB === 'string') {
      comparison = valA.localeCompare(valB)
    } else if (typeof valA === 'boolean' && typeof valB === 'boolean') {
      comparison = valA === valB ? 0 : valA ? -1 : 1 // true sorts before false
    } else if (typeof valA === 'number' && typeof valB === 'number') {
      comparison = valA - valB
    }
    // Add more type checks if needed (e.g., for complex objects)

    return sortDirection === 'asc' ? comparison : comparison * -1
  })
}
