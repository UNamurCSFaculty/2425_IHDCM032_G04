import type { ErrorResponse, ValidationErrorResponse } from '@/api/generated'

export const displayErrorMessage = (
  error: ValidationErrorResponse | ErrorResponse
): string => {
  if (error.status === 400) {
    // ici on est sûr que c'est un ValidationErrorResponse
    const ve = error as ValidationErrorResponse
    if (ve.errors) {
      // on affiche les messages d'erreur
      return Object.values(ve.errors).join(', ')
    }
    return 'Erreur de validation'
  } else {
    // ici on est dans le cas ErrorResponse (401, 500, etc.)
    const er = error as ErrorResponse
    return er.error ?? `Erreur inconnue (${error.status})`
  }
}
