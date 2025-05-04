import { z } from 'zod'
import { useNavigate, Link, useRouter, useSearch } from '@tanstack/react-router'
import { useMutation } from '@tanstack/react-query'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { LockIcon, UserIcon } from 'lucide-react'
import logo from '@/assets/logo.svg'
import { useAppForm } from './form'
import { Route as LoginRoute } from '@/routes/login'
import { useUserStore } from '@/store/userStore'

import { authenticateUserMutation } from '@/api/generated/@tanstack/react-query.gen'
import type { LoginRequest } from '@/api/generated/index'
import { useStore } from '@tanstack/react-form'
import { useTranslation } from 'react-i18next'

const LoginSchema = z.object({
  username: z.email('Adresse e-mail invalide'),
  password: z
    .string()
    .min(8, 'Le mot de passe doit faire au moins 8 caractères'),
})

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<'div'>) {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const router = useRouter()
  const setUser = useUserStore(s => s.setUser)
  const { redirect: redirectParam } = useSearch({ from: LoginRoute.id })

  const loginMutation = useMutation({
    ...authenticateUserMutation(),
    onSuccess(user) {
      setUser(user)
      if (redirectParam) {
        router.history.replace(decodeURIComponent(redirectParam))
      } else {
        navigate({ to: '/', replace: true })
      }
    },
  })

  const form = useAppForm({
    defaultValues: { username: '', password: '' },
    validators: { onChange: LoginSchema },
    onSubmit({ value }) {
      loginMutation.mutate({ body: value as LoginRequest })
    },
  })
  const canSubmit = useStore(form.store, state => state.canSubmit)

  const isPending = loginMutation.isPending

  return (
    <div className={cn('flex flex-col gap-6', className)} {...props}>
      <Card className="overflow-hidden">
        <CardContent>
          <form
            className="p-6 md:p-8"
            onSubmit={e => {
              e.preventDefault()
              form.handleSubmit()
            }}
          >
            <div className="flex flex-col gap-6">
              {/* Header */}
              <div className="flex flex-col items-center text-center">
                <img src={logo} alt="Logo e-Anacarde" className="h-20" />
                <p className="text-muted-foreground text-balance">
                  Connectez-vous à votre compte e-Anacarde
                </p>
              </div>

              {/* Email */}
              <div className="grid gap-2">
                <form.AppField
                  name="username"
                  children={field => (
                    <field.TextField
                      label="Adresse e-mail"
                      type="email"
                      endIcon={UserIcon}
                      placeholder="m@example.com"
                      disabled={isPending}
                    />
                  )}
                />
              </div>

              {/* Password */}
              <div className="grid gap-2">
                <form.AppField
                  name="password"
                  children={field => (
                    <field.TextField
                      label="Mot de passe"
                      type="password"
                      endIcon={LockIcon}
                      placeholder="••••••••••"
                      disabled={isPending}
                    />
                  )}
                />
                <div className="flex items-center">
                  <Link
                    to="/recover-password"
                    className="ml-auto text-sm underline-offset-2 hover:underline"
                  >
                    Mot de passe oublié ?
                  </Link>
                </div>
              </div>

              {/* Erreur */}
              {loginMutation.error && (
                <p className="text-sm text-red-600">
                  {t('errors.' + loginMutation.error.code)}
                </p>
              )}
              {/* Bouton submit */}
              <form.AppForm>
                <form.SubmitButton
                  className="w-full"
                  disabled={isPending || !canSubmit}
                >
                  {isPending ? 'Connexion…' : 'Se connecter'}
                </form.SubmitButton>
              </form.AppForm>

              {/* Séparateur */}
              <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                <span className="bg-background text-muted-foreground relative z-10 px-2">
                  Ou continuer avec
                </span>
              </div>

              {/* Social buttons */}
              <div className="grid grid-cols-3 gap-4">
                <Button variant="outline" className="w-full" disabled>
                  <span className="sr-only">Connexion avec Apple</span>
                </Button>
                <Button variant="outline" className="w-full" disabled>
                  <span className="sr-only">Connexion avec Google</span>
                </Button>
                <Button variant="outline" className="w-full" disabled>
                  <span className="sr-only">Connexion avec Meta</span>
                </Button>
              </div>

              {/* Inscription */}
              <div className="text-center text-sm">
                Vous n’avez pas de compte ?{' '}
                <Link to="/register" className="underline underline-offset-4">
                  Inscrivez-vous
                </Link>
              </div>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Mentions légales */}
      <div className="text-muted-foreground hover:[&_a]:text-primary text-center text-xs text-balance [&_a]:underline [&_a]:underline-offset-4">
        En cliquant sur Continuer, vous acceptez nos{' '}
        <Link to="/terms" className="underline underline-offset-4">
          Conditions d’utilisation
        </Link>{' '}
        et notre{' '}
        <Link to="/privacy" className="underline underline-offset-4">
          Politique de confidentialité
        </Link>
        .
      </div>
    </div>
  )
}
