// src/components/form/FieldErrors.tsx
import type { AnyFieldMeta } from '@tanstack/react-form'

interface FieldErrorsProps {
  meta: AnyFieldMeta
  /**
   * Par défaut on n’affiche qu’après touche ou blur.
   * Passez `always` à true pour forcer l’affichage.
   */
  always?: boolean
}

export const FieldErrors = ({ meta, always = false }: FieldErrorsProps) => {
  console.log('FIELD ERRORS', meta)
  // Pas de meta ou pas d’erreurs → rien
  if (!meta || !meta.errors?.length) return null

  // On n’affiche qu’une fois le champ touché (sauf always)
  if (!always && !meta.isTouched && !meta.isBlurred) return null

  // Helper pour extraire un message quel que soit le format
  const getMessage = (err: unknown): string => {
    if (typeof err === 'string') return err // message brut
    if (err && typeof err === 'object' && 'message' in err) {
      return String((err as { message?: unknown }).message) // ZodIssue ou objet custom
    }
    return JSON.stringify(err) // fallback
  }

  console.log(meta.errors)

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
