import { useEffect, useState } from 'react'

const MOBILE_BREAKPOINT = 768

/**
 * Hook générique pour évaluer une media query.
 * @param query Une chaîne de caractères valide pour window.matchMedia, ex. '(min-width: 1024px)'
 * @returns Un booléen indiquant si la media query correspond à la taille d’écran actuelle.
 */
export function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState<boolean>(() => {
    // initialiser sans effet de bord (SSR safe)
    if (typeof window === 'undefined') return false
    return window.matchMedia(query).matches
  })

  useEffect(() => {
    const mql = window.matchMedia(query)
    const handler = (event: MediaQueryListEvent) => {
      setMatches(event.matches)
    }

    mql.addEventListener('change', handler)
    // mettre à jour immédiatement
    setMatches(mql.matches)

    return () => {
      mql.removeEventListener('change', handler)
    }
  }, [query])

  return matches
}

export function useIsMobile() {
  return useMediaQuery(`(max-width: ${MOBILE_BREAKPOINT - 1}px)`)
}
