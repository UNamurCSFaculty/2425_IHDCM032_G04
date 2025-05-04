import {createFileRoute, redirect} from '@tanstack/react-router'
import {SignupForm} from "@/components/SignupForm.tsx";

export const Route = createFileRoute('/signup')({
  component: RouteComponent,

  beforeLoad: async ({ location, context }) => {
    if (context.user !== null) {
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
