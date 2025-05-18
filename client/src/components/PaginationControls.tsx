import { Button } from './ui/button'
import React from 'react'

interface PaginationControlsProps {
  current: number
  total: number
  onChange: (page: number) => void
}

const PaginationControls: React.FC<PaginationControlsProps> = ({
  current,
  total,
  onChange,
}) => (
  <div className="flex justify-center gap-1 pt-4">
    <Button
      size="sm"
      variant="outline"
      disabled={current === 1}
      onClick={() => onChange(current - 1)}
    >
      Prev
    </Button>
    {Array.from({ length: total }).map((_, i) => (
      <Button
        key={i + 1}
        size="sm"
        variant={current === i + 1 ? 'default' : 'outline'}
        className="w-8 h-8 p-0"
        onClick={() => onChange(i + 1)}
      >
        {i + 1}
      </Button>
    ))}
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

export default PaginationControls
