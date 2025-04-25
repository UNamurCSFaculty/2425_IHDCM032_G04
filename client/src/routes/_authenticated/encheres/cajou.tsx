import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/encheres/cajou')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/_authenticated/enchere/cajou"!</div>
}
