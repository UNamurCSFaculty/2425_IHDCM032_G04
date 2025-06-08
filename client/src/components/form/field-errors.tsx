import type { AnyFieldMeta } from '@tanstack/react-form'
import type { TFunction } from 'i18next'
import { useTranslation } from 'react-i18next'
import type { $ZodRawIssue } from 'zod/v4/core'

interface FieldErrorsProps {
  meta: AnyFieldMeta
  always?: boolean
}

/**
 * Composant pour afficher les erreurs de validation d'un champ de formulaire.
 * Il affiche les messages d'erreur associés au champ, en tenant compte de l'état de touche et de flou.
 * Si `always` est vrai, les erreurs sont toujours affichées, même si le champ n'a pas été touché ou flou.
 * Sinon, les erreurs ne sont affichées que si le champ a été touché ou flou.
 */
export const FieldErrors = ({ meta, always = false }: FieldErrorsProps) => {
  const { t } = useTranslation()

  if (!meta || !meta.errors?.length) return null

  if (!always && !meta.isTouched && !meta.isBlurred) return null

  const getMessage = (issue: $ZodRawIssue | string): string => {
    if (typeof issue === 'string') return issue
    return translateIssue(issue, t)
  }

  return (
    <>
      {meta.errors.map((err, idx) => (
        <p key={idx} className="text-destructive text-xs font-medium">
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

      if (issue.origin === 'number') {
        return t('validation.number_min', { count: min })
      }
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
      if (issue.message) {
        if (issue.path && issue.path[0] === 'password') {
          // cas particulier pour le mot de passe
          if (issue.message === 'validation.minLength') {
            return t('validation.minLength', { count: 8 })
          }
        }
        return t(`${issue.message}`)
      }
      // clé custom selon le champ
      const pathKey = issue.path?.join('.') ?? ''
      return t(`validation.${pathKey}`)
    }
    default:
      return t('validation.default')
  }
}
