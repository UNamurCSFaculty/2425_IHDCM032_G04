import i18n from '../i18n'
import { useAppForm } from './form'
import { Alert, AlertDescription } from './ui/alert'
import { toast } from 'sonner'
import {
  authenticateUserMutation,
  authenticateWithGoogleMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import logo from '@/assets/logo.svg'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { cn } from '@/lib/utils'
import { Route as LoginRoute } from '@/routes/connexion'
import { LoginSchema } from '@/schemas/login-schemas.ts'
import { useUserStore } from '@/store/userStore'
import { useStore } from '@tanstack/react-form'
import { useMutation } from '@tanstack/react-query'
import { Link, useNavigate, useRouter, useSearch } from '@tanstack/react-router'
import { AlertCircle, LockIcon, UserIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { useCallback, useEffect } from 'react'
import type { CredentialResponse } from '@/types/google'

export function LoginForm() {
  const navigate = useNavigate()
  const router = useRouter()
  const { t } = useTranslation()
  const { redirect: redirectParam } = useSearch({ from: LoginRoute.id })

  const setUser = useUserStore(s => s.setUser)

  /* ---------- MUTATION Google ---------- */
  const googleMutation = useMutation({
    ...authenticateWithGoogleMutation(),
    onSuccess: user => {
      setUser(user)
      toast.success(t('login.success_google'))
      navigate({ to: '/', replace: true })
    },
    onError: error => {
      toast.error(
        t('errors.' + error.code) ||
          (error.errors && error.errors[0]?.message) ||
          t('errors.generic_error_message')
      )
    },
  })

  /* Callback fourni à Google Identity Services */
  const handleGoogleCredential = useCallback(
    (response: CredentialResponse) => {
      if (response.credential) {
        googleMutation.mutate({
          body: response.credential,
        })
      } else {
        toast.error(t('errors.google_credential_missing'))
        console.error('Google credential missing in response')
      }
    },
    [googleMutation, t]
  )

  useEffect(() => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
    if (!clientId) {
      console.error('Google Client ID (VITE_GOOGLE_CLIENT_ID) is not defined.')
      toast.error(t('errors.google_client_id_missing'))
      return
    }

    if (
      typeof window.google !== 'undefined' &&
      window.google.accounts &&
      window.google.accounts.id
    ) {
      try {
        window.google.accounts.id.initialize({
          client_id: clientId,
          callback: handleGoogleCredential,
          ux_mode: 'popup',
        })
      } catch (error) {
        console.error('Error initializing Google Sign-In:', error)
        toast.error(t('errors.google_init_failed'))
      }
    } else {
      console.warn(
        'Google Identity Services script not loaded at the time of useEffect execution. The Google login button might not work until the script is loaded.'
      )
    }
  }, [handleGoogleCredential, t])

  /* ---------- MUTATION login/password ---------- */
  const loginMutation = useMutation({
    ...authenticateUserMutation(),
    retry: 0,
    onSuccess(user) {
      setUser(user)
      if (redirectParam) {
        router.history.replace(decodeURIComponent(redirectParam))
      } else {
        navigate({ to: '/', replace: true })
      }
    },
  })

  /* ---------- Form react-form ---------- */
  const form = useAppForm({
    defaultValues: { username: '', password: '' },
    validators: { onChange: LoginSchema },
    onSubmit({ value }) {
      loginMutation.mutate({ body: value })
    },
  })
  const canSubmit = useStore(form.store, s => s.canSubmit)
  const isPending = loginMutation.isPending

  return (
    <div className={cn('flex flex-col gap-6')}>
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
                <img src={logo} alt={t('header.logo_alt')} className="h-20" />
                <p className="text-muted-foreground text-balance">
                  {t('login.title_description')}
                </p>
              </div>

              {/* Email */}
              <div className="grid gap-2">
                <form.AppField
                  name="username"
                  children={field => (
                    <field.TextField
                      label={t('form.mail_or_phone')}
                      type="text"
                      endIcon={UserIcon}
                      placeholder={t('form.placeholder.email_example')}
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
                      label={t('form.password')}
                      type="password"
                      endIcon={LockIcon}
                      placeholder={t('form.placeholder.password_example')}
                      disabled={isPending}
                    />
                  )}
                />
                <div className="flex items-center">
                  <Link
                    to="/recover-password"
                    className="ml-auto text-sm underline-offset-2 hover:underline"
                  >
                    {t('login.forgot_password')}
                  </Link>
                </div>
              </div>

              {/* Erreur */}
              {loginMutation.error && (
                <Alert
                  variant="destructive"
                  className="mt-4 mb-4 border-red-300 bg-red-50"
                >
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>
                    {t('errors.' + loginMutation.error.code)}
                  </AlertDescription>
                </Alert>
              )}
              {/* Bouton submit */}
              <form.AppForm>
                <form.SubmitButton
                  className="w-full"
                  disabled={isPending || !canSubmit}
                >
                  {isPending
                    ? i18n.t('app.statut.connection')
                    : i18n.t('app.statut.log_in')}
                </form.SubmitButton>
              </form.AppForm>

              {/* Séparateur */}
              <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
                <span className="bg-background text-muted-foreground relative z-10 px-2">
                  {t('login.continue_with_separator')}
                </span>
              </div>

              {/* Social buttons */}
              <div className="grid grid-cols-3 gap-4">
                <Button variant="outline" className="w-full">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <path
                      d="M12.152 6.896c-.948 0-2.415-1.078-3.96-1.04-2.04.027-3.91 1.183-4.961 3.014-2.117 3.675-.546 9.103 1.519 12.09 1.013 1.454 2.208 3.09 3.792 3.039 1.52-.065 2.09-.987 3.935-.987 1.831 0 2.35.987 3.96.948 1.637-.026 2.676-1.48 3.676-2.948 1.156-1.688 1.636-3.325 1.662-3.415-.039-.013-3.182-1.221-3.22-4.857-.026-3.04 2.48-4.494 2.597-4.559-1.429-2.09-3.623-2.324-4.39-2.376-2-.156-3.675 1.09-4.61 1.09zM15.53 3.83c.843-1.012 1.4-2.427 1.245-3.83-1.207.052-2.662.805-3.532 1.818-.78.896-1.454 2.338-1.273 3.714 1.338.104 2.715-.688 3.559-1.701"
                      fill="currentColor"
                    />
                  </svg>
                  <span className="sr-only">{t('login.social.apple_sr')}</span>
                </Button>
                <Button
                  variant="outline"
                  className="w-full"
                  onClick={() => {
                    if (
                      window.google &&
                      window.google.accounts &&
                      window.google.accounts.id
                    ) {
                      try {
                        window.google.accounts.id.prompt()
                      } catch (error) {
                        console.error('Error calling Google prompt:', error)
                        toast.error(t('errors.google_prompt_failed'))
                      }
                    } else {
                      toast.error(t('errors.google_not_ready'))
                      console.error(
                        'Google Identity Services not available for prompt.'
                      )
                    }
                  }}
                  disabled={googleMutation.isPending}
                  asChild
                >
                  <div>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                      <path
                        d="M12.48 10.92v3.28h7.84c-.24 1.84-.853 3.187-1.787 4.133-1.147 1.147-2.933 2.4-6.053 2.4-4.827 0-8.6-3.893-8.6-8.72s3.773-8.72 8.6-8.72c2.6 0 4.507 1.027 5.907 2.347l2.307-2.307C18.747 1.44 16.133 0 12.48 0 5.867 0 .307 5.387.307 12s5.56 12 12.173 12c3.573 0 6.267-1.173 8.373-3.36 2.16-2.16 2.84-5.213 2.84-7.667 0-.76-.053-1.467-.173-2.053H12.48z"
                        fill="currentColor"
                      />
                    </svg>
                    <span className="sr-only">
                      {t('login.social.google_sr')}
                    </span>
                  </div>
                </Button>
                <Button variant="outline" className="w-full">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <path
                      d="M6.915 4.03c-1.968 0-3.683 1.28-4.871 3.113C.704 9.208 0 11.883 0 14.449c0 .706.07 1.369.21 1.973a6.624 6.624 0 0 0 .265.86 5.297 5.297 0 0 0 .371.761c.696 1.159 1.818 1.927 3.593 1.927 1.497 0 2.633-.671 3.965-2.444.76-1.012 1.144-1.626 2.663-4.32l.756-1.339.186-.325c.061.1.121.196.183.3l2.152 3.595c.724 1.21 1.665 2.556 2.47 3.314 1.046.987 1.992 1.22 3.06 1.22 1.075 0 1.876-.355 2.455-.843a3.743 3.743 0 0 0 .81-.973c.542-.939.861-2.127.861-3.745 0-2.72-.681-5.357-2.084-7.45-1.282-1.912-2.957-2.93-4.716-2.93-1.047 0-2.088.467-3.053 1.308-.652.57-1.257 1.29-1.82 2.05-.69-.875-1.335-1.547-1.958-2.056-1.182-.966-2.315-1.303-3.454-1.303zm10.16 2.053c1.147 0 2.188.758 2.992 1.999 1.132 1.748 1.647 4.195 1.647 6.4 0 1.548-.368 2.9-1.839 2.9-.58 0-1.027-.23-1.664-1.004-.496-.601-1.343-1.878-2.832-4.358l-.617-1.028a44.908 44.908 0 0 0-1.255-1.98c.07-.109.141-.224.211-.327 1.12-1.667 2.118-2.602 3.358-2.602zm-10.201.553c1.265 0 2.058.791 2.675 1.446.307.327.737.871 1.234 1.579l-1.02 1.566c-.757 1.163-1.882 3.017-2.837 4.338-1.191 1.649-1.81 1.817-2.486 1.817-.524 0-1.038-.237-1.383-.794-.263-.426-.464-1.13-.464-2.046 0-2.221.63-4.535 1.66-6.088.454-.687.964-1.226 1.533-1.533a2.264 2.264 0 0 1 1.088-.285z"
                      fill="currentColor"
                    />
                  </svg>
                  <span className="sr-only">{t('login.social.meta_sr')}</span>
                </Button>
              </div>

              {/* Inscription */}
              <div className="text-center text-lg">
                {t('login.signup_prompt')}{' '}
                <Link
                  to="/inscription"
                  className="underline underline-offset-4"
                >
                  {t('breadcrumb.signup')}
                </Link>
              </div>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Mentions légales */}
      <div className="text-muted-foreground hover:[&_a]:text-primary text-center text-xs text-balance [&_a]:underline [&_a]:underline-offset-4">
        {t('login.terms_prefix')}
        <Link to="/terms" className="underline underline-offset-4">
          {t('breadcrumb.terms')}
        </Link>
        {t('login.terms_separator')}
        <Link to="/privacy" className="underline underline-offset-4">
          {t('breadcrumb.privacy')}
        </Link>
        {t('login.terms_suffix')}
      </div>
    </div>
  )
}
