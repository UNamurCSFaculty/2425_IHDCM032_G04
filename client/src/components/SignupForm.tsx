import { zUserRegistration } from '@/schemas/api-schemas'
import { useNavigate } from '@tanstack/react-router'
import { useAppForm } from '@/components/form'
import { useStore } from '@tanstack/react-form'
import { useMutation } from "@tanstack/react-query";
import { createUserMutation } from "@/api/generated/@tanstack/react-query.gen.ts";
import { useTranslation } from 'react-i18next'


export function SignupForm() : React.ComponentProps<'div'> {
  const navigate = useNavigate()
  const { t } = useTranslation()

  const signinMutation = useMutation({
    ...createUserMutation(),
    onSuccess() {
        navigate({ to: '/login'})
    },
    onError(error) {
      console.error("Requête invalide :", error)
    }
  })

  const { isPending, isError, error } = signinMutation

  const form = useAppForm({
    validators: { onChange: zUserRegistration },
    defaultValues: {
      type: 'producer',
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      address: '',
      password: '',
      passwordValidation: '',
      language: { id: 1352, name: "Français" },
      agriculturalIdentifier: ''
    },
    onSubmit({ value }) {
      const validatedValue = zUserRegistration.parse(value);
      signinMutation.mutate({ body: validatedValue})
    }
  })

  const type = useStore(form.store, state => state.values.type)
  const canSubmit = useStore(form.store, state => state.canSubmit)

  return (
    <section className="body-font relative text-gray-600">
      <div className="container mx-auto px-5 py-24">
        <div className="mb-12 flex w-full flex-col text-center">
          <h1 className="title-font mb-4 text-2xl font-medium text-gray-900 sm:text-3xl">
            {t('app.signup.titre')}
          </h1>
          <p className="mx-auto text-base leading-relaxed lg:w-2/3">
            {t('app.signup.sous_titre')}
          </p>
        </div>
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
                    label={t('form.type')}
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="lastName"
                children={field => <field.TextField label={t('form.last_name')} disabled={isPending} />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="firstName"
                children={field => <field.TextField label={t('form.first_name')} disabled={isPending} />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="language"
                children={field => <field.SelectLanguageField disabled={isPending} />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="email"
                children={field => (
                  <field.TextField type="email" label={t('form.mail')} disabled={isPending} />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="address"
                children={field => <field.TextField label={t('form.address')} disabled={isPending} />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="phone"
                children={field => <field.TextField label={t('form.phone')} disabled={isPending} />}
              />
            </div>
            {type === 'producer' ? (
              <div className="w-1/2 p-2">
                <form.AppField
                  name="agriculturalIdentifier"
                  children={field => (
                    <field.TextField label={t('form.agricultural_identifier')} disabled={isPending} />
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
                  <field.TextField type="password" label={t('form.password')} disabled={isPending} />
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
                <div className="flex w-full p-2 justify-center text-sm text-red-600">
                  {error.errors.map((err, idx) => (
                      <div key={idx}>
                        {err.field
                            ? `${err.field} : ${err.message}`
                            : err.message
                        }
                      </div>
                  ))}
                </div>
            )}
            <div className="flex w-full items-center justify-center gap-4 p-2">
              <form.AppForm>
                <form.SubmitButton disabled={isPending || !canSubmit}>
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
