import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated')({
  beforeLoad: async ({ location, context }) => {
    if (!context.user) {
      throw redirect({
        to: '/connexion',
        search: {
          // Use the current location to power a redirect after login
          redirect: location.href,
        },
      })
    }
  },
})
