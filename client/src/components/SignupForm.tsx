import { zUserRegistration } from '@/schemas/api-schemas'
import {useNavigate} from '@tanstack/react-router'
import { useAppForm } from '@/components/form'
import { useStore } from '@tanstack/react-form'
import { useMutation } from "@tanstack/react-query";
import { createUserMutation } from "@/api/generated/@tanstack/react-query.gen.ts";


export function SignupForm() : React.ComponentProps<'div'> {
  const navigate = useNavigate()

  const signinMutation = useMutation({
    ...createUserMutation(),
    onSuccess(data) {
      console.log(data)
        navigate({ to: '/'})
    }
  })

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
      agriculturalIdentifier: '',
    },
    onSubmit({ value }) {
      const validatedValue = zUserRegistration.parse(value);
      signinMutation.mutate({ body: validatedValue})
    }
  })

  const type = useStore(form.store, state => state.values.type)
  const canSubmit = useStore(form.store, state => state.canSubmit)

  const isPending = signinMutation.isPending

  return (
    <section className="body-font relative text-gray-600">
      <div className="container mx-auto px-5 py-24">
        <div className="mb-12 flex w-full flex-col text-center">
          <h1 className="title-font mb-4 text-2xl font-medium text-gray-900 sm:text-3xl">
            Contactez-nous
          </h1>
          <p className="mx-auto text-base leading-relaxed lg:w-2/3">
            N'hésitez pas à nous envoyer vos questions ou commentaires.
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
                    label="Je suis"
                  />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="lastName"
                children={field => <field.TextField label="Nom" />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="firstName"
                children={field => <field.TextField label="Prénom" />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="language"
                children={field => <field.SelectLanguageField />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="email"
                children={field => (
                  <field.TextField type="email" label="Adresse mail" />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="address"
                children={field => <field.TextField label="Adresse" />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="phone"
                children={field => <field.TextField label="Téléphone" />}
              />
            </div>
            {type === 'producer' && (
              <div className="w-1/2 p-2">
                <form.AppField
                  name="agriculturalIdentifier"
                  children={field => (
                    <field.TextField label="Identifiant agricole" />
                  )}
                />
              </div>
            )}
            <div className="w-1/2 p-2">
              <form.AppField
                name="password"
                children={field => (
                  <field.TextField type="password" label="Mot de passe" />
                )}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="passwordValidation"
                children={field => (
                  <field.TextField
                    type="password"
                    label="Valider le mot de passe"
                  />
                )}
              />
            </div>
            <div className="flex w-full items-center justify-center gap-4 p-2">
              <form.AppForm>
                <form.SubmitButton disabled={!canSubmit}>
                  Envoyer
                </form.SubmitButton>
                <form.ResetButton>Réinitialiser</form.ResetButton>
              </form.AppForm>
            </div>
          </form>
        </div>
      </div>
    </section>
  )
}
