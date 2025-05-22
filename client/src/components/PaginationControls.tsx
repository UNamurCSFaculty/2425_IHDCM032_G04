'use client'

import { Button } from './ui/button'
import React from 'react'

interface PaginationControlsProps {
  current: number
  total: number
  onChange: (page: number) => void
  maxButtons?: number // nombre max de boutons de page à afficher (hors Prev/Next)
}

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

    pages.push(1) // première page

    if (start > 2) {
      pages.push('...') // ellipse après le 1
    }

    for (let p = start; p <= end; p++) {
      pages.push(p)
    }

    if (end < total - 1) {
      pages.push('...') // ellipse avant la dernière
    }

    pages.push(total) // dernière page

    return pages
  }

  const pages = getPages()

  return (
    <div className="flex justify-center items-center gap-1 pt-4 flex-wrap w-full">
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
            className="inline-flex w-8 h-8 items-center justify-center text-sm text-muted-foreground"
          >
            …
          </span>
        ) : (
          <Button
            key={p}
            size="sm"
            variant={current === p ? 'default' : 'outline'}
            className="w-8 h-8 p-0"
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
