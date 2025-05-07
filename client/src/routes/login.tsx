import { LoginForm } from '@/components/LoginForm'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useUserStore } from '@/store/userStore'
import { createFileRoute, useNavigate } from '@tanstack/react-router'
import z from 'zod'

export const Route = createFileRoute('/login')({
  component: RouteComponent,
  validateSearch: z.object({
    redirect: z.string().optional(),
  }),
})

function RouteComponent() {
  const navigate = useNavigate()
  const logout = useUserStore(state => state.logout)
  const user = useUserStore(state => state.user)

  const handleLogout = () => {
    logout()
    navigate({ to: '/', replace: true })
  }

  // Si l'utilisateur est connecté, on affiche un message et le bouton de déconnexion
  if (user) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-neutral-300">
        <Card className="w-full max-w-sm shadow-xl">
          <CardHeader />
          <CardContent className="text-center">
            <CardTitle>
              Vous êtes connecté en tant que&nbsp;
              <span className="font-semibold">{user.email}</span>
            </CardTitle>
            <Button
              variant="destructive"
              onClick={handleLogout}
              className="mt-6 w-full"
            >
              Déconnexion
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  // Sinon, on affiche le formulaire de connexion
  return (
    <div className="bg-muted flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm md:max-w-xl">
        <LoginForm />
      </div>
    </div>
  )
}
