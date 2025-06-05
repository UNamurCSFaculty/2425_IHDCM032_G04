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

// Type pour une miette de pain individuelle
type CrumbBase = {
  href?: string
  icon?: LucideIcon
}
type CrumbWithLabelKey = CrumbBase & {
  labelKey: string
  label?: never
}
type CrumbWithLabel = CrumbBase & {
  label: string
  labelKey?: never
}
export type Crumb = CrumbWithLabelKey | CrumbWithLabel

// Props pour BreadcrumbSection
type BreadcrumbSectionPropsBase = {
  subtitleKey?: string
  breadcrumbs?: Crumb[]
  className?: string
}

type BreadcrumbSectionPropsWithTitleKey = BreadcrumbSectionPropsBase & {
  titleKey: string
  titleElement?: never
}

type BreadcrumbSectionPropsWithTitleElement = BreadcrumbSectionPropsBase & {
  titleElement: React.ReactNode
  titleKey?: never
}

export type BreadcrumbSectionProps =
  | BreadcrumbSectionPropsWithTitleKey
  | BreadcrumbSectionPropsWithTitleElement

export function BreadcrumbSection({
  titleKey,
  titleElement,
  subtitleKey,
  breadcrumbs = [],
  className,
}: BreadcrumbSectionProps) {
  const { t } = useTranslation()

  // On ajoute systématiquement Home comme premier crumb
  const homeCrumb: Crumb = {
    labelKey: 'breadcrumb.home',
    href: '/',
    icon: Home,
  }
  const items: Crumb[] = [homeCrumb, ...breadcrumbs]

  return (
    <section
      className={cn(
        'bg-gradient-to-br from-green-50 to-yellow-50 px-6 py-12 dark:from-gray-800 dark:to-gray-900', // Ajout de couleurs pour le mode sombre
        className
      )}
    >
      <Breadcrumb className="mb-6">
        <BreadcrumbList className="flex items-center justify-center space-x-2">
          {items.map((crumb, idx) => {
            const crumbContent =
              crumb.label || (crumb.labelKey ? t(crumb.labelKey) : '')
            return (
              <React.Fragment key={idx}>
                <BreadcrumbItem>
                  {crumb.href ? (
                    <BreadcrumbLink asChild>
                      <Link
                        to={crumb.href}
                        className="flex items-center gap-1 text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-50"
                      >
                        {crumb.icon && (
                          <crumb.icon className="inline-block h-4 w-4" />
                        )}
                        <span>{crumbContent}</span>
                      </Link>
                    </BreadcrumbLink>
                  ) : (
                    <BreadcrumbPage className="font-medium text-gray-700 dark:text-gray-300">
                      {crumb.icon && (
                        <crumb.icon className="mr-1 inline-block h-4 w-4" />
                      )}
                      {crumbContent}
                    </BreadcrumbPage>
                  )}
                </BreadcrumbItem>
                {idx < items.length - 1 && <BreadcrumbSeparator />}
              </React.Fragment>
            )
          })}
        </BreadcrumbList>
      </Breadcrumb>
      <div className="container mx-auto px-6 text-center">
        {titleElement ? (
          titleElement
        ) : titleKey ? (
          <h1 className="mb-2 text-xl font-semibold text-gray-900 sm:text-4xl dark:text-white">
            {t(titleKey)}
          </h1>
        ) : null}
        {subtitleKey && (
          <p className="mx-auto mt-4 max-w-2xl text-lg text-gray-700 dark:text-gray-300">
            {t(subtitleKey)}
          </p>
        )}
      </div>
    </section>
  )
}
