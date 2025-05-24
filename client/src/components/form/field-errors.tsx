// src/components/form/FieldErrors.tsx
import type { AnyFieldMeta } from '@tanstack/react-form'
import type { TFunction } from 'i18next'
import { useTranslation } from 'react-i18next'
import type { $ZodRawIssue } from 'zod/v4/core'

interface FieldErrorsProps {
  meta: AnyFieldMeta
  /**
   * Par défaut on n’affiche qu’après touche ou blur.
   * Passez `always` à true pour forcer l’affichage.
   */
  always?: boolean
}

export const FieldErrors = ({ meta, always = false }: FieldErrorsProps) => {
  const { t } = useTranslation()

  // Pas de meta ou pas d’erreurs → rien
  if (!meta || !meta.errors?.length) return null

  // On n’affiche qu’une fois le champ touché (sauf always)
  if (!always && !meta.isTouched && !meta.isBlurred) return null

  // Helper pour extraire un message quel que soit le format
  const getMessage = (issue: $ZodRawIssue | string): string => {
    if (typeof issue === 'string') return issue // message brut (champ multiple comme addresse)
    return translateIssue(issue, t)
  }

  return (
    <>
      {meta.errors.map((err, idx) => (
        <p key={idx} className="text-destructive text-sm font-medium">
          {getMessage(err)}
        </p>
      ))}
    </>
  )
}

function translateIssue(issue: $ZodRawIssue, t: TFunction): string {
  console.debug(issue, issue.message, issue.code)
  switch (issue.code) {
    case 'too_small': {
      const min = issue.minimum as number
      // si c'est un minLength à 1 → champ requis
      return min === 1
        ? t('validation.required')
        : t('validation.minLength', { count: min })
    }
    case 'too_big': {
      const max = issue.maximum as number
      return t('validation.maxLength', { count: max })
    }
    case 'invalid_type': {
      if (issue.message?.endsWith('received undefined')) {
        return t('validation.required')
      }
      return t('validation.invalidType')
    }
    case 'invalid_format': {
      // pour la validation regex ou autre format string
      if (issue.format === 'regex') {
        // chemin de l'erreur (ex: 'postalCode')
        if (issue.pattern === '/^(?:\\+229)?01\\d{8}$/') {
          return t('validation.phone')
        }
      }
      return t(`validation.${issue.format}`)
    }
    case 'custom': {
      // clé custom selon le champ
      const pathKey = issue.path?.join('.') ?? ''
      return t(`validation.${pathKey}`)
    }
    default:
      return t('validation.default')
  }
}
