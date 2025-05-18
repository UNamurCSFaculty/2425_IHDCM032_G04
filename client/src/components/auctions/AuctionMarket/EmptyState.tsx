import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Package } from 'lucide-react'
import React from 'react'

const EmptyState: React.FC<{ onReset: () => void }> = ({ onReset }) => (
  <Card className="min-h-[300px] flex flex-col items-center justify-center text-center bg-background">
    <CardHeader>
      <Package className="size-16 text-muted-foreground mb-2" />
      <CardTitle>Aucune enchère trouvée</CardTitle>
    </CardHeader>
    <CardContent>
      <p className="text-muted-foreground max-w-xs">
        Essayez d’ajuster vos filtres ou
        <Button variant="link" className="p-0 h-auto inline" onClick={onReset}>
          {' '}
          réinitialiser la recherche
        </Button>
        .
      </p>
    </CardContent>
  </Card>
)

export default EmptyState
