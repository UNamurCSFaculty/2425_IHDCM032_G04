import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_authenticated/encheres/autres')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/_authenticated/enchere/autres"!</div>
}
