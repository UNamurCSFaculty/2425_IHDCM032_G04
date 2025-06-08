import { useEffect, useState } from 'react'

/**
 * Hook personnalisé pour retarder la mise à jour d'une valeur
 * Utile pour optimiser les performances lors de saisie utilisateur (recherche, filtres, etc.)
 * @param value - La valeur à retarder
 * @param delay - Délai en millisecondes avant mise à jour (défaut: 500ms)
 * @returns La valeur retardée
 */
export function useDebounce<T>(value: T, delay = 500): T {
  const [debounced, setDebounced] = useState<T>(value)

  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay)
    return () => clearTimeout(timer)
  }, [value, delay])

  return debounced
}
