import Stepper from './Stepper'
import { checkEmail, checkPhone } from '@/api/generated'
import { createUserMutation } from '@/api/generated/@tanstack/react-query.gen'
import { zUserUpdateDto } from '@/api/generated/zod.gen'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import type { DeepPartial } from '@/lib/utils'
import { zUser } from '@/schemas/api-schemas'
import { useAppData } from '@/store/appStore'
import { useStore } from '@tanstack/react-form'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle, ArrowRight, Loader2 } from 'lucide-react'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { z } from 'zod/v4'

/* ------------------------------------------------------------------------ */
/* Schema d’inscription                                                 */
/* ------------------------------------------------------------------------ */

export const zUserRegistration = zUser
  .and(
    z.object({
      passwordValidation: z.string(),
      acceptTerms: z.boolean().refine(v => v === true, {
        message: 'errors.accept_terms',
      }),
      documents: z.array(z.file()).min(1),
    })
  )
  .refine(d => d.password === d.passwordValidation, {
    path: ['passwordValidation'],
  })

export type UserRegistration = z.infer<typeof zUserRegistration>
type UserRegistrationDraft = DeepPartial<UserRegistration>

/* ------------------------------------------------------------------------ */
/*   Composant principal                                                  */
/* ------------------------------------------------------------------------ */

type StepField =
  | keyof UserRegistration
  | 'agriculturalIdentifier'
  | 'address.cityId'
  | 'address.street'
  | 'address.location'

export const SignupForm: React.FC = () => {
  const navigate = useNavigate()
  const { t, i18n } = useTranslation()
  const appData = useAppData()

  const [step, setStep] = useState(2)
  const [isSubmitting, setIsSubmitting] = useState(false)

  /* ------------------------- Mutation API ------------------------------ */
  const mutation = useMutation({
    ...createUserMutation(),
    onSuccess: () => navigate({ to: '/login' }),
  })
  const { isError, error } = mutation

  /* ------------------------ TanStack Form ------------------------------ */
  const form = useAppForm({
    validators: { onChange: zUserRegistration },
    defaultValues: {
      documents: [],

      email: '',
      password: '',
      passwordValidation: '',
      languageId: appData.languages[0].id,
      agriculturalIdentifier: '',
      acceptTerms: false,
    } as UserRegistrationDraft,
    async onSubmit({ value }) {
      const userRegistration = value as UserRegistration
      const { documents = [], ...user } = userRegistration
      setIsSubmitting(true)
      try {
        await mutation.mutateAsync({ body: { user, documents } })
      } finally {
        setIsSubmitting(false)
      }
    },
  })

  /* Validation initiale (langue par défaut) */
  useEffect(() => {
    form.validate('change')
  }, [i18n.language, form])

  /* Données utiles pour les champs conditionnels */
  const values = useStore(form.store, s => s.values)

  /* ------------------------- Gestion des steps ------------------------- */
  const stepFields: Record<number, StepField[]> = {
    1: [
      'type',
      'firstName',
      'lastName',
      'languageId',
      'email',
      'phone',
      'agriculturalIdentifier',
    ],
    2: ['address.cityId', 'address.street', 'address.location', 'documents'],
    3: ['password', 'passwordValidation', 'acceptTerms'],
  }

  /** Valide tous les champs du step et renvoie true s’ils sont OK */
  const canGoToNext = async (): Promise<boolean> => {
    const currentStepFields = stepFields[step]
    let hasErrorInStep = false

    console.log(
      `🔍 [canGoToNext] Validation pour l'étape ${step}. Champs:`,
      currentStepFields
    )

    for (const fieldName of currentStepFields) {
      const fieldNameTyped = fieldName as keyof UserRegistration // Assurez-vous que UserRegistration est bien typé
      const changeErrors = await form.validateField(fieldNameTyped, 'change')
      if (changeErrors && changeErrors.length > 0) {
        hasErrorInStep = true
      }

      const blurErrors = await form.validateField(fieldNameTyped, 'blur')
      if (blurErrors && blurErrors.length > 0) {
        hasErrorInStep = true
      }
    }

    if (hasErrorInStep) {
      return false
    }

    return true
  }

  const nextStep = async () => {
    if (await canGoToNext()) setStep(s => s + 1)
  }
  const prevStep = () => setStep(s => s - 1)

  /* ----------------------------- UI ------------------------------------ */
  return (
    <section className="body-font relative text-gray-600">
      <BreadcrumbSection
        titleKey="app.signup.title"
        subtitleKey="app.signup.subtitle"
        breadcrumbs={[{ labelKey: 'breadcrumb.signup' }]}
      />

      <div className="container mx-auto max-w-3xl px-5 py-24">
        <Card>
          <CardHeader>
            <CardTitle className="text-xl">{t('app.signup.title')}</CardTitle>
            <CardDescription>{t('app.signup.subtitle')}</CardDescription>
          </CardHeader>
          <CardContent>
            <Stepper step={step} />

            <form
              onSubmit={e => {
                e.preventDefault()
                e.stopPropagation()
                form.handleSubmit()
              }}
              className="space-y-6"
            >
              {/* ----------------------- Étape 1 ----------------------- */}
              {step === 1 && (
                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  {/* Type */}
                  <form.AppField
                    name="type"
                    children={f => (
                      <f.RadioGroupField
                        className="md:col-span-2"
                        direction="row"
                        choices={[
                          { value: 'producer', label: t('types.producer') },
                          {
                            value: 'transformer',
                            label: t('types.transformer'),
                          },
                          { value: 'exporter', label: t('types.exporter') },
                          { value: 'carrier', label: t('types.carrier') },
                        ]}
                        label={t('form.type')}
                      />
                    )}
                  />
                  {/* Nom / Prénom */}
                  <form.AppField
                    name="lastName"
                    children={f => <f.TextField label={t('form.last_name')} />}
                  />
                  <form.AppField
                    name="firstName"
                    children={f => <f.TextField label={t('form.first_name')} />}
                  />
                  {/* E-mail / Téléphone */}
                  <form.AppField
                    name="email"
                    validators={{
                      onChange: zUserUpdateDto.shape.email,
                      onBlurAsync: async ({ value }) => {
                        if (!value) return
                        const exists = await checkEmail({
                          query: { email: value },
                        })

                        return exists.data
                          ? t('errors.email.exists')
                          : undefined
                      },
                    }}
                    children={f => (
                      <f.TextField type="email" label={t('form.mail')} />
                    )}
                  />
                  <form.AppField
                    name="phone"
                    validators={{
                      onChange: zUserUpdateDto.shape.phone,
                      onBlurAsync: async ({ value }) => {
                        if (!value) return
                        const exists = await checkPhone({
                          query: { phone: value },
                        })

                        return exists.data
                          ? t('errors.phone.exists')
                          : undefined
                      },
                    }}
                    children={f => (
                      <f.PhoneField label={t('form.phone')} required />
                    )}
                  />

                  {/* Langue */}
                  <form.AppField
                    name="languageId"
                    children={f => (
                      <f.VirtualizedSelectField
                        placeholder="Sélectionnez une langue"
                        options={appData.languages.map(l => ({
                          id: l.id,
                          label: t('languages.' + l.code),
                        }))}
                        label={t('form.language')}
                      />
                    )}
                  />

                  {/* Identifiant agricole (producteur uniquement) */}
                  {values.type === 'producer' ? (
                    <form.AppField
                      name="agriculturalIdentifier"
                      children={f => (
                        <f.TextField
                          label={t('form.agricultural_identifier')}
                        />
                      )}
                    />
                  ) : (
                    <div aria-hidden="true" />
                  )}
                </div>
              )}

              {/* ----------------------- Étape 2 ----------------------- */}
              {step === 2 && (
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-1">
                  {/* Adresse */}
                  <form.AppField
                    name="address"
                    children={f => (
                      <f.AddressField
                        withMap={true}
                        label={t('form.address')}
                      />
                    )}
                  />

                  {/* Documents */}
                  <form.AppField
                    name="documents"
                    children={f => (
                      <f.FileUploadField
                        label={t('form.documents')}
                        accept="application/pdf,image/*"
                        maxFiles={5}
                        maxSize={5}
                      />
                    )}
                  />
                </div>
              )}

              {/* ----------------------- Étape 3 ----------------------- */}
              {step === 3 && (
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  {/* MDP + confirmation */}
                  <form.AppField
                    name="password"
                    children={f => (
                      <f.TextField type="password" label={t('form.password')} />
                    )}
                  />
                  <form.AppField
                    name="passwordValidation"
                    children={f => (
                      <f.TextField
                        type="password"
                        label={t('form.confirm_password')}
                      />
                    )}
                  />
                  {/* Conditions générales */}
                  <form.AppField
                    name="acceptTerms"
                    children={f => (
                      <f.CheckboxField
                        label={t('form.accept_terms')}
                        required
                      />
                    )}
                  />
                </div>
              )}

              {/* ---------- Erreurs serveur ---------- */}
              {isError && error?.errors?.length > 0 && (
                <Alert
                  variant="destructive"
                  className="mt-4 border-red-300 bg-red-50"
                >
                  <AlertCircle className="h-4 w-4" />
                  <AlertTitle>{t('common.error')}</AlertTitle>
                  <AlertDescription>
                    <ul className="list-disc pl-4">
                      {error.errors.map((err, i) => (
                        <li key={i}>
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

              {/* ---------- Navigation ---------- */}
              <div className="flex justify-between">
                {step > 1 ? (
                  <Button type="button" variant="outline" onClick={prevStep}>
                    {t('pagination.previous')}
                  </Button>
                ) : (
                  <span />
                )}

                {step < 3 ? (
                  <Button type="button" onClick={nextStep}>
                    {t('pagination.next')}{' '}
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </Button>
                ) : (
                  <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        {t('buttons.submitting')}
                      </>
                    ) : (
                      t('buttons.submit')
                    )}
                  </Button>
                )}
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </section>
  )
}
