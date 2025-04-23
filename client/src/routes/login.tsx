import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useUserStore } from '@/store/userStore'
import { LoginForm } from '@/components/LoginForm'

export const Route = createFileRoute('/login')({
  component: RouteComponent,
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
      <div className="bg-base-200 flex min-h-screen items-center justify-center">
        <div className="card w-full max-w-sm shadow-xl">
          <div className="card-body text-center">
            <h2 className="card-title mx-auto">
              Vous êtes connecté en tant que{' '}
              <span className="font-semibold">{user.email}</span>
            </h2>
            <button
              onClick={handleLogout}
              className="btn btn-error mt-6 w-full"
            >
              Déconnexion
            </button>
          </div>
        </div>
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
