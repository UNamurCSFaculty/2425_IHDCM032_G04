import Stepper from './Stepper'
import { checkEmail, checkPhone } from '@/api/generated'
import { createUserMutation } from '@/api/generated/@tanstack/react-query.gen'
import { zAddressDto, zUserCreateDto } from '@/api/generated/zod.gen' // zAddressDto a été ajouté
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
import { calculatePasswordStrength, cn, type DeepPartial } from '@/lib/utils'
import {
  passwordStrengthRefineConfig,
  passwordStrengthValidationFn,
  zAppCreateUser,
} from '@/schemas/api-schemas'
import { useAppData } from '@/store/appStore'
import { useStore } from '@tanstack/react-form'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle, ArrowRight, Loader2 } from 'lucide-react'
import React, { useState, useRef, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { z } from 'zod/v4'
import { PasswordStrengthIndicator } from './PasswordStrengthIndicator'

const zUserRegistration = zAppCreateUser
  .and(
    z.object({
      password: z.string().min(8),
      passwordValidation: z.string().min(1),
      acceptTerms: z
        .boolean()
        .refine(v => v === true, { message: 'errors.accept_terms' }),
      documents: z.array(z.file()).min(1),
    })
  )
  .refine(data => data.password === data.passwordValidation, {
    path: ['passwordValidation'],
    message: 'validation.passwordValidation',
  })
  .refine(passwordStrengthValidationFn, passwordStrengthRefineConfig)

export type UserRegistration = z.infer<typeof zUserRegistration>
type UserRegistrationDraft = DeepPartial<UserRegistration>
type StepField =
  | keyof UserRegistration
  | 'agriculturalIdentifier'
  | 'address.cityId'
  | 'address.street'
  | 'address.location'
  | 'pricePerKm'
  | 'radius'

const StepPanel: React.FC<{
  index: number
  current: number
  children: React.ReactNode
}> = ({ index, current, children }) => {
  const isActive = index === current
  return (
    <div
      className={cn(
        'w-full transition-all duration-300',
        isActive
          ? 'relative translate-x-0 opacity-100'
          : 'pointer-events-none absolute opacity-0'
      )}
    >
      {children}
    </div>
  )
}

/*
 * Formulaire d'inscription en 3 étapes pour les utilisateurs
 * Étape 1: Informations personnelles et type de compte
 * Étape 2: Adresse et détails spécifiques au transporteur
 * Étape 3: Mot de passe, documents et acceptation des conditions
 */
export const SignupForm: React.FC = () => {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const appData = useAppData()
  const containerRef = useRef<HTMLDivElement>(null)
  const isMountedRef = useRef(false)

  const [step, setStep] = useState(1)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const mutation = useMutation({
    ...createUserMutation(),
    onSuccess: () => navigate({ to: '/inscription-succes' }),
  })
  const { isError, error } = mutation

  const form = useAppForm({
    validators: { onChange: zUserRegistration },
    defaultValues: {
      documents: [],
      type: undefined,
      firstName: '',
      lastName: '',
      email: '',
      phone: '+22901',
      address: {},
      password: '',
      passwordValidation: '',
      languageId: appData.languages[0].id,
      agriculturalIdentifier: '',
      acceptTerms: false,
      radius: 0,
      pricePerKm: 0,
    } as UserRegistrationDraft,
    async onSubmit({ value }) {
      const userRegistration = value as UserRegistration
      zUserRegistration.parse(userRegistration)
      const { documents = [], ...user } = userRegistration
      setIsSubmitting(true)
      try {
        await mutation.mutateAsync({ body: { user, documents } })
      } finally {
        setIsSubmitting(false)
      }
    },
  })

  const values = useStore(form.store, s => s.values)

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
    2: [
      'address.cityId',
      'address.street',
      'address.location',
      'pricePerKm',
      'radius',
    ],
    3: ['documents', 'password', 'passwordValidation', 'acceptTerms'],
  }

  /** Valide tous les champs du step et renvoie true s’ils sont OK */
  const validateStep = async (stepToValidate: number): Promise<boolean> => {
    const fieldsForStep = stepFields[stepToValidate]
    if (!fieldsForStep) return true

    let hasErrorInStep = false
    for (const fieldName of fieldsForStep) {
      const fieldNameTyped = fieldName as keyof UserRegistration

      const changeErrors = await form.validateField(fieldNameTyped, 'change')
      if (changeErrors && changeErrors.length > 0) {
        hasErrorInStep = true
      }

      const blurErrors = await form.validateField(fieldNameTyped, 'blur')
      if (blurErrors && blurErrors.length > 0) {
        hasErrorInStep = true
      }
    }
    return !hasErrorInStep
  }

  const nextStep = async () => {
    if (await validateStep(step)) {
      setStep(s => s + 1)
    }
  }
  const prevStep = () => setStep(s => s - 1)

  const handleStepClick = async (targetStep: number) => {
    if (targetStep === step) return

    if (targetStep < step) {
      setStep(targetStep)
    } else {
      let allPreviousStepsValid = true
      for (let i = step; i < targetStep; i++) {
        const isStepValid = await validateStep(i)
        if (!isStepValid) {
          allPreviousStepsValid = false
          setStep(i)
          break
        }
      }

      if (allPreviousStepsValid) {
        setStep(targetStep)
      }
    }
  }

  useEffect(() => {
    if (isMountedRef.current) {
      containerRef.current?.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      })
    } else {
      isMountedRef.current = true
    }
  }, [step])

  return (
    <section className="body-font relative text-gray-600">
      <BreadcrumbSection
        titleKey="app.signup.title"
        subtitleKey="app.signup.subtitle"
        breadcrumbs={[{ labelKey: 'breadcrumb.signup' }]}
      />

      <div
        ref={containerRef}
        className="container mx-auto max-w-3xl px-5 py-24"
      >
        <Card>
          <CardHeader>
            <CardTitle className="text-xl">{t('app.signup.title')}</CardTitle>
            <CardDescription>{t('app.signup.subtitle')}</CardDescription>
          </CardHeader>
          <CardContent>
            <Stepper step={step} onStepClick={handleStepClick} totalSteps={3} />

            <form
              onSubmit={e => {
                e.preventDefault()
                form.handleSubmit()
              }}
              className="relative h-fit space-y-6"
            >
              <StepPanel index={1} current={step}>
                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <form.AppField name="type">
                    {f => (
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
                  </form.AppField>

                  <form.AppField name="lastName">
                    {f => <f.TextField label={t('form.last_name')} />}
                  </form.AppField>
                  <form.AppField name="firstName">
                    {f => <f.TextField label={t('form.first_name')} />}
                  </form.AppField>

                  <form.AppField
                    name="email"
                    validators={{
                      onChange: zUserCreateDto.shape.email,
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
                  >
                    {f => <f.TextField type="email" label={t('form.mail')} />}
                  </form.AppField>

                  <form.AppField
                    name="phone"
                    validators={{
                      onChange: zUserCreateDto.shape.phone,
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
                  >
                    {f => <f.PhoneField label={t('form.phone')} required />}
                  </form.AppField>

                  <form.AppField name="languageId">
                    {f => (
                      <f.VirtualizedSelectField
                        placeholder={t('form.placeholder.language')}
                        options={appData.languages.map(l => ({
                          id: l.id,
                          label: t('languages.' + l.code),
                        }))}
                        label={t('form.language')}
                      />
                    )}
                  </form.AppField>

                  {values.type === 'producer' ? (
                    <form.AppField name="agriculturalIdentifier">
                      {f => (
                        <f.TextField
                          label={t('form.agricultural_identifier')}
                        />
                      )}
                    </form.AppField>
                  ) : (
                    <div aria-hidden="true" />
                  )}
                </div>
              </StepPanel>

              <StepPanel index={2} current={step}>
                <div className="grid grid-cols-1 gap-4">
                  {values.type === 'carrier' && (
                    <fieldset className="space-y-3 rounded-md border-2 border-gray-200 p-4">
                      <legend className="px-2 text-lg font-semibold">
                        {t('form.carrier_details')}
                      </legend>
                      <div className="grid grid-cols-2 gap-4">
                        <form.AppField name="pricePerKm">
                          {f => (
                            <f.TextField<number>
                              label={t('form.price_per_km')}
                              fieldType="number"
                              tooltip={t('form.tooltip.price_per_km')}
                              type="number"
                              required={false}
                              placeholder={t(
                                'form.placeholder.price_per_km_example'
                              )}
                            />
                          )}
                        </form.AppField>

                        <form.AppField name="radius">
                          {f => (
                            <f.TextField<number>
                              label={t('form.radius')}
                              type="number"
                              tooltip={t('form.tooltip.radius')}
                              fieldType="number"
                              required={false}
                              placeholder={t('form.placeholder.radius_example')}
                            />
                          )}
                        </form.AppField>
                      </div>
                    </fieldset>
                  )}

                  <fieldset className="space-y-3 rounded-md border-2 border-gray-200 p-4">
                    <legend className="px-2 text-lg font-semibold">
                      {t('form.address')}
                    </legend>
                    <form.AppField
                      name="address.cityId"
                      validators={{ onChange: zAddressDto.shape.cityId }}
                    >
                      {f => (
                        <f.CityField
                          label={t('form.city')}
                          tooltip={t('form.tooltip.city')}
                          required
                        />
                      )}
                    </form.AppField>

                    <form.AppField name="address.street">
                      {f => (
                        <f.TextField
                          label={t('form.street_quarter')}
                          required={false}
                          placeholder={t('form.placeholder.street_example')}
                        />
                      )}
                    </form.AppField>

                    <form.AppField
                      name="address.location"
                      validators={{
                        onChange: zAddressDto.shape.location,
                      }}
                    >
                      {f => (
                        <f.LocationField
                          label={t('form.location')}
                          mapHeight="280px"
                          tooltip={t('form.tooltip.location')}
                          radius={
                            values.type === 'carrier' && values.radius
                              ? values.radius * 1000
                              : 0
                          }
                          required
                        />
                      )}
                    </form.AppField>
                  </fieldset>
                </div>
              </StepPanel>

              <StepPanel index={3} current={step}>
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <form.AppField name="password">
                    {f => (
                      <f.TextField type="password" label={t('form.password')} />
                    )}
                  </form.AppField>
                  <form.AppField name="passwordValidation">
                    {f => (
                      <f.TextField
                        type="password"
                        label={t('form.confirm_password')}
                      />
                    )}
                  </form.AppField>
                  {values.password && (
                    <PasswordStrengthIndicator
                      className="col-span-full"
                      strength={calculatePasswordStrength(values.password)}
                    />
                  )}
                  <form.AppField name="documents">
                    {f => (
                      <f.FileUploadField
                        className="col-span-full"
                        label={t('form.documents')}
                        accept="application/pdf,image/*"
                        maxFiles={5}
                        maxSize={5}
                      />
                    )}
                  </form.AppField>
                  <form.AppField name="acceptTerms">
                    {f => (
                      <f.CheckboxField
                        className="col-span-full"
                        label={t('form.accept_terms')}
                        required
                      />
                    )}
                  </form.AppField>
                </div>
              </StepPanel>

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

              <div className="flex justify-between">
                {step > 1 ? (
                  <Button type="button" variant="outline" onClick={prevStep}>
                    {t('pagination.previous')}
                  </Button>
                ) : (
                  <span />
                )}

                <Button
                  type="button"
                  onClick={nextStep}
                  className={cn(step >= 3 && 'hidden')}
                >
                  {t('pagination.next')}
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>

                <Button
                  type="submit"
                  disabled={isSubmitting}
                  className={cn(step < 3 && 'hidden')}
                >
                  {isSubmitting ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      {t('buttons.submitting')}
                    </>
                  ) : (
                    t('buttons.submit')
                  )}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </section>
  )
}
