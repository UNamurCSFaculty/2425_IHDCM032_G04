import { BreadcrumbSection } from './BreadcrumbSection'
import { useAppForm } from './form'
import { Alert, AlertDescription, AlertTitle } from './ui/alert'
import { sendContactMessageMutation } from '@/api/generated/@tanstack/react-query.gen.ts'
import {
  ContactSchema,
  ContactSchemaDefaultValues,
} from '@/schemas/form-schemas'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect } from 'react'
import { useTranslation } from 'react-i18next'

export const ContactForm: React.FC = () => {
  const { t, i18n } = useTranslation()
  const navigate = useNavigate()

  const mutation = useMutation({
    ...sendContactMessageMutation(),
    onSuccess: () => {
      navigate({ to: '/contact/merci' })
    },
    onError: error => {
      console.error('Erreur envoi contact :', error)
    },
  })

  // on initialise le formulaire avec Zod et useAppForm
  const form = useAppForm({
    defaultValues: ContactSchemaDefaultValues,
    validators: { onChange: ContactSchema },
    onSubmit: ({ value }) => {
      mutation.mutate({ body: value })
    },
  })

  useEffect(() => {
    form.validate('change')
  }, [i18n.language, form])

  // états d'affichage
  const { isPending, isError, error } = mutation

  return (
    <section className="body-font relative text-gray-600">
      <BreadcrumbSection
        titleKey="app.contact.title"
        subtitleKey="app.contact.subtitle"
        breadcrumbs={[{ labelKey: 'breadcrumb.contact' }]}
      />

      <div className="container mx-auto px-5 py-24">
        <div className="mx-auto md:w-2/3 lg:w-1/2">
          <form
            onSubmit={e => {
              e.preventDefault()
              form.handleSubmit()
            }}
            className="-m-2 flex flex-wrap"
          >
            {/* Nom */}
            <div className="w-1/2 p-2">
              <form.AppField name="name">
                {field => (
                  <field.TextField
                    label={t('form.last_name')} // ajustez la clé si besoin
                    disabled={isPending}
                  />
                )}
              </form.AppField>
            </div>

            {/* Email */}
            <div className="w-1/2 p-2">
              <form.AppField name="email">
                {field => (
                  <field.TextField
                    label={t('form.mail')}
                    type="email"
                    disabled={isPending}
                  />
                )}
              </form.AppField>
            </div>

            {/* Message */}
            <div className="w-full p-2">
              <form.AppField name="message">
                {field => (
                  <field.TextAreaField
                    label={t('form.message')}
                    rows={6}
                    disabled={isPending}
                  />
                )}
              </form.AppField>
            </div>

            {/* Erreurs serveur éventuelles */}

            {isError && error?.errors?.length > 0 && (
              <Alert
                variant="destructive"
                className="border-red-300 bg-red-50 mt-4 mb-4"
              >
                <AlertCircle className="h-4 w-4" />
                <AlertTitle>{t('common.error')}</AlertTitle>
                <AlertDescription>
                  <ul className="list-disc">
                    {error.errors.map((err, idx) => (
                      <li key={idx} className="mb-1">
                        {err.field
                          ? `${t('errors.fields.' + err.field)}: `
                          : ''}
                        {t('errors.' + err.code)}
                      </li>
                    ))}
                  </ul>
                </AlertDescription>
              </Alert>
            )}
            {/* Boutons */}
            <div className="flex w-full items-center justify-center gap-4 p-2">
              <form.AppForm>
                <form.SubmitButton disabled={isPending}>
                  {isPending ? t('common.loading') : t('buttons.submit')}
                </form.SubmitButton>
                <form.ResetButton disabled={isPending}>
                  {t('buttons.reinitialise')}
                </form.ResetButton>
              </form.AppForm>
            </div>
          </form>
        </div>
      </div>
    </section>
  )
}
