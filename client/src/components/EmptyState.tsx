import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { cn } from '@/lib/utils'
import { SearchX } from 'lucide-react'
import React from 'react'
import { useTranslation } from 'react-i18next'

interface EmptyStateProps {
  className?: string
}

const EmptyState: React.FC<EmptyStateProps> = ({ className }) => {
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
          <SearchX className="text-muted-foreground mb-2 size-16" />
          <CardTitle>{t('empty_state.title')}</CardTitle>
        </div>

        <p className="text-muted-foreground max-w-xs">
          {t('empty_state.message')}
        </p>
      </CardContent>
    </Card>
  )
}

export default EmptyState
