import React from 'react'
import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
interface PasswordStrengthIndicatorProps
  extends React.HTMLAttributes<HTMLDivElement> {
  strength: number // 0-5 scale
}

export function PasswordStrengthIndicator({
  strength,
  className,
  ...rest
}: PasswordStrengthIndicatorProps) {
  const { t } = useTranslation()

  const levels = [
    {
      label: t('password_indicator.very_weak'),
      color: 'bg-red-500',
      textColor: 'text-red-600',
    },
    {
      label: t('password_indicator.weak'),
      color: 'bg-orange-500',
      textColor: 'text-orange-600',
    },
    {
      label: t('password_indicator.fair'),
      color: 'bg-yellow-500',
      textColor: 'text-yellow-600',
    },
    {
      label: t('password_indicator.good'),
      color: 'bg-lime-500',
      textColor: 'text-lime-600',
    },
    {
      label: t('password_indicator.strong'),
      color: 'bg-green-500',
      textColor: 'text-green-600',
    },
    {
      label: t('password_indicator.very_strong'),
      color: 'bg-emerald-500',
      textColor: 'text-emerald-600',
    },
  ]

  // Get current level based on strength
  const currentLevel = levels[Math.min(strength, 5)]

  return (
    <div
      className={cn('mt-1 space-y-2', className)} // Fusionne les classes
      {...rest} // Applique les autres props HTML
    >
      <div className="flex h-2 w-full overflow-hidden rounded-full bg-slate-200 dark:bg-slate-600">
        {strength > 0 && (
          <div
            className={`${currentLevel.color} transition-all duration-300`}
            style={{ width: `${(strength / 5) * 100}%` }}
          />
        )}
      </div>
      {strength > 0 && (
        <p className={`text-muted-foreground text-xs`}>
          {t('password_indicator.strength')}:{' '}
          <span className={`font-semibold ${currentLevel.textColor}`}>
            {currentLevel.label}
          </span>
        </p>
      )}
    </div>
  )
}
