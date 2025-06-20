import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { cn } from '@/lib/utils'
import { LoaderCircle } from 'lucide-react'
import React from 'react'
import { useTranslation } from 'react-i18next'

interface LoadingStateProps {
  className?: string
}

/**
 * Composant React pour afficher un état de chargement.
 * Utilisé pour indiquer que des données sont en cours de chargement.
 * Affiche un spinner et un message de chargement.
 */
const LoadingState: React.FC<LoadingStateProps> = ({ className }) => {
  const { t } = useTranslation()

  return (
    <Card
      className={cn(
        'bg-background flex min-h-[300px] flex-col items-center justify-center text-center',
        className
      )}
    >
      <CardContent>
        <div className="mb-4 flex flex-col items-center justify-center">
          <LoaderCircle className="text-muted-foreground mb-2 size-16 animate-spin" />
          <CardTitle>{t('loading_state.title')}</CardTitle>
        </div>

        <p className="text-muted-foreground max-w-xs">
          {t('loading_state.message')}
        </p>
      </CardContent>
    </Card>
  )
}

export default LoadingState
