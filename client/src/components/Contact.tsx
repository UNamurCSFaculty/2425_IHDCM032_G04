import React, { useMemo } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { useAppForm } from './form'
import {
  ContactSchema,
  ContactSchemaDefaultValues,
} from '@/schemas/form-schemas'
import { LanguageSwitcher } from './LanguageSwitcher'

export const ContactForm: React.FC = () => {
  const navigate = useNavigate()
  const form = useAppForm({
    defaultValues: ContactSchemaDefaultValues,
    onSubmit: async ({ value }) => {
      // TODO: envoyer les données au serveur
      console.log('Form submitted:', value)
      navigate({ to: '/merci' })
    },
    validators: {
      onChange: ContactSchema,
    },
  })

  return (
    <section className="body-font relative text-gray-600">
      <LanguageSwitcher />
      <div className="container mx-auto px-5 py-24">
        <div className="mb-12 flex w-full flex-col text-center">
          <h1 className="title-font mb-4 text-2xl font-medium text-gray-900 sm:text-3xl">
            Contactez-nous
          </h1>
          <p className="mx-auto text-base leading-relaxed lg:w-2/3">
            N'hésitez pas à nous envoyer vos questions ou commentaires.
          </p>
        </div>
        <div className="mx-auto md:w-2/3 lg:w-1/2">
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
                name="name"
                children={field => <field.TextField label="Name" />}
              />
            </div>
            <div className="w-1/2 p-2">
              <form.AppField
                name="email"
                children={field => <field.TextField label="Email" />}
              />
            </div>
            <div className="w-full p-2">
              <form.AppField
                name="message"
                children={field => <field.TextAreaField label="Message" />}
              />
            </div>

            <div className="flex w-full items-center justify-center gap-4 p-2">
              <form.AppForm>
                <form.SubmitButton>Envoyer</form.SubmitButton>
                <form.ResetButton>Réinitialiser</form.ResetButton>
              </form.AppForm>
            </div>
          </form>
        </div>
      </div>
    </section>
  )
}
