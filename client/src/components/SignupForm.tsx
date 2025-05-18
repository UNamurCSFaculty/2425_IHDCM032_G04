import { Alert, AlertDescription, AlertTitle } from './ui/alert'
import { createUserMutation } from '@/api/generated/@tanstack/react-query.gen.ts'
import { BreadcrumbSection } from '@/components/BreadcrumbSection.tsx'
import { useAppForm } from '@/components/form'
import { zUser } from '@/schemas/api-schemas'
import { useAppData } from '@/store/appStore'
import { useStore } from '@tanstack/react-form'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import z from 'zod'

export const zUserRegistration = zUser
  .and(
    z.object({
      passwordValidation: z.string(),
    })
  )
  .refine(data => data.password === data.passwordValidation, {
    path: ['passwordValidation'],
  })

export type UserRegistration = z.infer<typeof zUserRegistration>

export const SignupForm: React.FC = () => {
  const navigate = useNavigate()
  const { t, i18n } = useTranslation()
  const appData = useAppData()

  const signinMutation = useMutation({
    ...createUserMutation(),
    onSuccess() {
      navigate({ to: '/login' })
    },
    onError(error) {
      console.error('Requête invalide :', error)
    },
  })

  const { isPending, isError, error } = signinMutation

  const form = useAppForm<
    UserRegistration, // TFormData
    undefined, // TOnMount
    typeof zUserRegistration, // TOnChange
    undefined, // TOnChangeAsync
    undefined, // TOnBlur
    undefined, // TOnBlurAsync
    undefined, // TOnSubmit
    undefined, // TOnSubmitAsync
    undefined, // TOnServer
    undefined // TSubmitMeta
  >({
    validators: { onChange: zUserRegistration },
    defaultValues: {
      documents: [],
      type: 'producer',
      firstName: '',
      lastName: '',
      email: '',
      phone: '+22901',
      address: {
        cityId: 0,
        street: '',
      },
      password: '',
      passwordValidation: '',
      languageId: appData.languages[0].id,
      agriculturalIdentifier: '',
    },
    onSubmit({ value }) {
      console.log('Form submitted:', value)
      const { documents = [], ...user } = value
      /*
      const formData = new FormData()
      formData.append(
        'user', // nom de la part JSON
        new Blob([JSON.stringify(user)], {
          type: 'application/json',
        })
      )
      documents.forEach(f => formData.append('documents', f))
      */

      signinMutation.mutate({ body: { user, documents } })
    },
  })

  useEffect(() => {
    form.validate('change')
  }, [i18n.language, form])

  const type = useStore(form.store, state => state.values.type)

  return (
    <section className="body-font relative text-gray-600">
      <BreadcrumbSection
        titleKey="app.signup.title"
        subtitleKey="app.signup.subtitle"
        breadcrumbs={[{ labelKey: 'breadcrumb.signup' }]}
      />
      <div className="container mx-auto px-5 py-24">
        <div className="mx-auto">
          <form
            onSubmit={e => {
              e.preventDefault()
              e.stopPropagation()
              form.handleSubmit()
            }}
            className="-m-2 flex flex-wrap"
          >
            <div className="w-1/2 p-2">
              <form.AppField
                name="type"
                children={field => (
                  <field.SelectField
                    options={[
                      { value: 'exporter', label: 'Exportateur' },
                      { value: 'quality_inspector', label: 'Qualiticien' },
                      { value: 'producer', label: 'Producteur' },
                      { value: 'transformer', label: 'Transformateur' },
                      { value: 'carrier', label: 'Transporteur' },
                    ]}
                    disabled={isPending}
                    label={t('form.type')}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="lastName"
                children={field => (
                  <field.TextField
                    label={t('form.last_name')}
                    disabled={isPending}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="firstName"
                children={field => (
                  <field.TextField
                    label={t('form.first_name')}
                    disabled={isPending}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="languageId"
                children={field => (
                  <field.SelectField
                    options={appData.languages.map(lang => ({
                      value: lang.id,
                      label: t('languages.' + lang.code),
                    }))}
                    label={t('form.language')}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="email"
                children={field => (
                  <field.TextField
                    type="email"
                    label={t('form.mail')}
                    disabled={isPending}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="address"
                children={field => (
                  <field.AddressField
                    withMap={true}
                    label={t('form.address')}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="phone"
                children={field => (
                  <field.TextField
                    label={t('form.phone')}
                    disabled={isPending}
                    value={field.state.value}
                    onChange={e => {
                      field.handleChange(e.target.value.replace(/\s+/g, ''))
                    }}
                  />
                )}
              />
            </div>
            {type === 'producer' ? (
              <div className="w-1/2 p-2">
                <form.AppField
                  name="agriculturalIdentifier"
                  children={field => (
                    <field.TextField
                      label={t('form.agricultural_identifier')}
                      disabled={isPending}
                    />
                  )}
                />
              </div>
            ) : (
              <div className="w-1/2 p-2" aria-hidden="true" />
            )}
            <div className="w-1/2 p-2">
              <form.AppField
                name="password"
                children={field => (
                  <field.TextField
                    type="password"
                    label={t('form.password')}
                    disabled={isPending}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="passwordValidation"
                children={field => (
                  <field.TextField
                    type="password"
                    label={t('form.confirm_password')}
                    disabled={isPending}
                  />
                )}
              />
            </div>
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

            <form.AppField
              name="documents"
              children={field => (
                <field.FileUploadField
                  label={t('form.documents')}
                  accept="application/pdf,image/*"
                  maxFiles={5}
                  maxSize={5}
                />
              )}
            />

            <div className="flex w-full items-center justify-center gap-4 p-2">
              <form.AppForm>
                <form.SubmitButton disabled={isPending}>
                  {t('buttons.submit')}
                </form.SubmitButton>
                <form.ResetButton>{t('buttons.reinitialise')}</form.ResetButton>
              </form.AppForm>
            </div>
          </form>
        </div>
      </div>
    </section>
  )
}
