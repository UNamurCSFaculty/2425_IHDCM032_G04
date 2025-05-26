import type { GoogleRegistration } from '@/schemas/api-schemas'
import { useMutation } from '@tanstack/react-query'

interface Payload {
  user: GoogleRegistration
  documents?: File[]
}

interface GoogleAuthResponse {
  token: string
}

export function useGoogleRegistrationMutation() {
  return useMutation<GoogleAuthResponse, any, Payload>({
    mutationFn: async ({ user, documents }) => {
      const form = new FormData()
      // partie JSON
      form.append(
        'user',
        new Blob([JSON.stringify(user)], { type: 'application/json' })
      )
      // fichiers
      documents?.forEach(file => form.append('documents', file))

      const res = await fetch('/api/users/google', {
        method: 'POST',
        body: form,
      })
      if (!res.ok) {
        throw await res.json()
      }
      return (await res.json()) as GoogleAuthResponse
    },
  })
}
