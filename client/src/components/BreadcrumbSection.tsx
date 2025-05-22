import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from './ui/breadcrumb'
import { cn } from '@/lib/utils'
import { Link } from '@tanstack/react-router'
import { Home, type LucideIcon } from 'lucide-react'
import React from 'react'
import { useTranslation } from 'react-i18next'

type Crumb = {
  /** Clé de traduction pour le libellé */
  labelKey: string
  /** URL de navigation ou route key pour TanStack Router */
  href?: string
  /** Icône optionnelle à afficher avant le label */
  icon?: LucideIcon
}

type BreadcrumbSectionProps = {
  /** Clé de traduction pour le titre */
  titleKey: string
  /** Clé de traduction pour le sous-titre (optionnel) */
  subtitleKey?: string
  /** Liste des crumbs supplémentaires (sans Home) */
  breadcrumbs?: Crumb[]
  className?: string
}

export function BreadcrumbSection({
  titleKey,
  subtitleKey,
  breadcrumbs = [],
  className,
}: BreadcrumbSectionProps) {
  const { t } = useTranslation()

  // On ajoute systématiquement Home comme premier crumb
  const items: Crumb[] = [
    { labelKey: 'breadcrumb.home', href: '/', icon: Home },
    ...breadcrumbs,
  ]

  return (
    <section
      className={cn(
        'bg-gradient-to-br from-green-50 to-yellow-50 py-5',
        className
      )}
    >
      <Breadcrumb className="mb-6">
        <BreadcrumbList className="flex justify-center items-center space-x-2">
          {items.map((crumb, idx) => (
            <React.Fragment key={idx}>
              <BreadcrumbItem>
                {crumb.href ? (
                  <BreadcrumbLink asChild>
                    <Link to={crumb.href} className="flex items-center gap-1">
                      {crumb.icon && (
                        <crumb.icon className="inline-block h-4 w-4" />
                      )}
                      <span>{t(crumb.labelKey)}</span>
                    </Link>
                  </BreadcrumbLink>
                ) : (
                  <BreadcrumbPage>
                    {crumb.icon && (
                      <crumb.icon className="inline-block h-4 w-4 mr-1" />
                    )}
                    {t(crumb.labelKey)}
                  </BreadcrumbPage>
                )}
              </BreadcrumbItem>
              {idx < items.length - 1 && <BreadcrumbSeparator />}
            </React.Fragment>
          ))}
        </BreadcrumbList>
      </Breadcrumb>
      <div className="container mx-auto px-6 text-center">
        <h1 className="mb-2 text-xl font-semibold text-gray-900 sm:text-4xl">
          {t(titleKey)}
        </h1>
        {subtitleKey && (
          <p className="mx-auto mt-4 max-w-2xl text-lg text-gray-700">
            {t(subtitleKey)}
          </p>
        )}
      </div>
    </section>
  )
}
