import type { AnyFieldMeta } from '@tanstack/react-form'

interface Props {
  fieldName: string
  meta: AnyFieldMeta
}

/**
 * Composant pour afficher les erreurs de validation d'un champ de formulaire.
 * Utilisé pour le débogage, il affiche les informations de validation du champ,
 * y compris les erreurs, les cartes d'erreur et les sources d'erreur.
 */
export function DebugFieldErrors({ fieldName, meta }: Props) {
  const {
    isTouched,
    isBlurred,
    isDirty,
    isValid,
    errors = [],
    errorMap,
    errorSourceMap,
  } = meta

  const hasErrors = errors.length > 0

  return (
    <div
      style={{
        marginTop: 6,
        padding: 6,
        fontSize: 12,
        border: `1px solid ${hasErrors ? 'red' : 'green'}`,
        color: hasErrors ? 'red' : 'green',
      }}
    >
      <p>
        <strong>[{fieldName}]</strong>&nbsp; Touched:{' '}
        <span style={{ fontWeight: 600 }}>{isTouched ? 'Yes' : 'No'}</span>,{' '}
        Blurred:{' '}
        <span style={{ fontWeight: 600 }}>{isBlurred ? 'Yes' : 'No'}</span>,{' '}
        Dirty: <span style={{ fontWeight: 600 }}>{isDirty ? 'Yes' : 'No'}</span>
        , Valid:{' '}
        <span style={{ fontWeight: 600 }}>{isValid ? 'Yes' : 'No'}</span>
      </p>

      {hasErrors && (
        <>
          <p>Errors&nbsp;({errors.length}):</p>
          <ul>
            {errors.map((err, i) => (
              <li key={i}>{String(err.message ?? err)}</li>
            ))}
          </ul>
        </>
      )}

      {/* Affiche la cartographie si la lib la fournit */}
      {errorMap && Object.keys(errorMap).length > 0 && (
        <>
          <p>errorMap:</p>
          <pre>{JSON.stringify(errorMap, null, 2)}</pre>
        </>
      )}
      {errorSourceMap && Object.keys(errorSourceMap).length > 0 && (
        <>
          <p>errorSourceMap:</p>
          <pre>{JSON.stringify(errorSourceMap, null, 2)}</pre>
        </>
      )}

      {!hasErrors && <p>Aucune erreur.</p>}
    </div>
  )
}
