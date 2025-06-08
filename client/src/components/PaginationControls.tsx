import { Button } from './ui/button'
import React from 'react'

interface PaginationControlsProps {
  current: number
  total: number
  onChange: (page: number) => void
  maxButtons?: number
}

/**
 * Composant de contrôle de pagination.
 * Permet de naviguer entre les pages d'une liste ou d'un ensemble de données.
 */
const PaginationControls: React.FC<PaginationControlsProps> = ({
  current,
  total,
  onChange,
  maxButtons = 5,
}) => {
  // Génère une liste mixte de nombres et de '...' pour la pagination
  const getPages = (): (number | '...')[] => {
    if (total <= maxButtons) {
      // si pas trop de pages, on affiche tout
      return Array.from({ length: total }, (_, i) => i + 1)
    }

    const pages: (number | '...')[] = []
    const half = Math.floor(maxButtons / 2)

    const start = Math.max(2, current - half)
    const end = Math.min(total - 1, current + half)

    pages.push(1)

    if (start > 2) {
      pages.push('...')
    }

    for (let p = start; p <= end; p++) {
      pages.push(p)
    }

    if (end < total - 1) {
      pages.push('...')
    }

    pages.push(total)

    return pages
  }

  const pages = getPages()

  return (
    <div className="flex w-full flex-wrap items-center justify-center gap-1 pt-4">
      <Button
        size="sm"
        variant="outline"
        disabled={current === 1}
        onClick={() => onChange(current - 1)}
      >
        Prev
      </Button>

      {pages.map((p, idx) =>
        p === '...' ? (
          <span
            key={`ell-${idx}`}
            className="text-muted-foreground inline-flex h-8 w-8 items-center justify-center text-sm"
          >
            …
          </span>
        ) : (
          <Button
            key={p}
            size="sm"
            variant={current === p ? 'default' : 'outline'}
            className="h-8 w-8 p-0"
            onClick={() => onChange(p)}
          >
            {p}
          </Button>
        )
      )}

      <Button
        size="sm"
        variant="outline"
        disabled={current === total}
        onClick={() => onChange(current + 1)}
      >
        Next
      </Button>
    </div>
  )
}

export default PaginationControls
