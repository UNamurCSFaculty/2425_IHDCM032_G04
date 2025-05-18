import type { AnyFieldMeta } from '@tanstack/react-form'

interface Props {
  fieldName: string
  meta: AnyFieldMeta
}

export function DebugFieldErrors({ fieldName, meta }: Props) {
  /* ---------------- état général ---------------- */
  const {
    isTouched,
    isBlurred,
    isDirty,
    isValid,
    errors = [],
    errorMap, // { onChange: [...], onBlur: [...] }  si dispo
    errorSourceMap, // { onChange: 'form' }                si dispo
  } = meta

  const hasErrors = errors.length > 0

  /* -------------- rendu -------------------------- */
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
