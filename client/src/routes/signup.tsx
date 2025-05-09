import { SignupForm } from '@/components/SignupForm.tsx'
import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/signup')({
  component: RouteComponent,

  beforeLoad: async ({ location, context }) => {
    if (context.user) {
      throw redirect({
        to: '/',
        search: {
          redirect: location.href,
        },
      })
    }
  },
})

function RouteComponent() {
  return <SignupForm />
}
