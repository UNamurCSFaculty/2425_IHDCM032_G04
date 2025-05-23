import { Button } from '@/components/ui/button'
import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { cn } from '@/lib/utils'
import { SearchX } from 'lucide-react'
import React from 'react'

interface EmptyStateProps {
  onReset: () => void
  className?: string
}

const EmptyState: React.FC<EmptyStateProps> = ({ onReset, className }) => (
  <Card
    className={cn(
      'min-h-[300px] flex flex-col items-center justify-center text-center bg-background',
      className
    )}
  >
    <CardContent>
      <div className="flex flex-col items-center justify-center mb-4">
        <SearchX className="size-16 text-muted-foreground mb-2" />
        <CardTitle>Aucune enchère trouvée</CardTitle>
      </div>

      <p className="text-muted-foreground max-w-xs">
        Ajustez vos filtres ou
        <Button variant="link" className="p-0 h-auto inline" onClick={onReset}>
          réinitialisez la recherche
        </Button>
        .
      </p>
    </CardContent>
  </Card>
)

export default EmptyState
