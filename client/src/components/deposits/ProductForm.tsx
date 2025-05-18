import type {
  QualityDto,
  QualityInspectorDetailDto,
  StoreDetailDto,
  TraderDetailDto,
} from '@/api/generated'
import { createProductMutation } from '@/api/generated/@tanstack/react-query.gen.ts'
import {
  zHarvestProductUpdateDto,
  zQualityControlUpdateDto,
  zTransformedProductUpdateDto,
} from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Card, CardContent } from '@/components/ui/card'
import { useAuthUser } from '@/store/userStore'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import z from 'zod'

interface ProductFormProps {
  mode: 'create' | 'update'
  traders: TraderDetailDto[]
  stores: StoreDetailDto[]
  qualities: QualityDto[]
  qualityInspectors: QualityInspectorDetailDto[]
}

const FormSectionTitle: React.FC<{ text: string }> = ({ text }) => {
  return (
    <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
      <span className="bg-background text-muted-foreground relative z-10 px-2">
        {text}
      </span>
    </div>
  )
}

const productSchema = z.object({
  product: z.union([zHarvestProductUpdateDto, zTransformedProductUpdateDto]),
  qualityControl: zQualityControlUpdateDto,
})

export type ProductRegistration = z.infer<typeof productSchema>

export function ProductForm({
  traders,
  stores,
  qualities,
  qualityInspectors,
}: ProductFormProps): React.ReactElement<'div'> {
  const user = useAuthUser()
  const navigate = useNavigate()
  const { t, i18n } = useTranslation()

  const createProductRequest = useMutation({
    ...createProductMutation(),
    onSuccess() {
      navigate({ to: '/depots/mes-produits' })
    },
    onError(error) {
      console.error('Requête invalide :', error)
    },
  })

  const { isError, error } = createProductRequest

  const form = useAppForm<
    ProductRegistration, // TFormData
    undefined, // TOnMount
    typeof productSchema, // TOnChange
    undefined, // TOnChangeAsync
    undefined, // TOnBlur
    undefined, // TOnBlurAsync
    undefined, // TOnSubmit
    undefined, // TOnSubmitAsync
    undefined, // TOnServer
    undefined // TSubmitMeta
  >({
    validators: { onChange: productSchema },
    defaultValues: {
      product: {
        id: 0,
        storeId: 0,
        weightKg: 0,
        qualityControlId: 0,
        fieldId: 0,
        type: 'harvest',
        producerId: 0,
      },
      qualityControl: {
        identifier: 'QC-980', //TODO: remove from DB
        controlDate: '',
        granularity: 0,
        korTest: 0,
        humidity: 0,
        qualityInspectorId: 0,
        productId: 0,
        qualityId: 0,
      },
    },
    onSubmit({ value }) {
      console.log('value submit: ', value)
      // const productData = {
      //   type: value.product.type,
      //   producerId: value.product.producerId,
      //   fieldId: value.product.fieldId,
      //   deliveryDate: value.product.deliveryDate,
      //   weightKg: value.product.weightKg,
      // }

      // const qualityControlData = {
      //   qualityControlId: value.qualityControl.qualityControlId,
      //   inspectorName: value.qualityControl.inspectorName,
      //   inspectionDate: value.qualityControl.inspectionDate,
      // }

      // const data = {
      //   product: productData,
      //   qualityControl: qualityControlData,
      // }

      // const result = productSchema.safeParse(data)

      // if (!result.success) {
      //   console.error(result.error.format())
      // } else {
      //   console.log('Validation réussie', result.data)
      // }

      // createProductRequest.mutate({ body: value })
    },
  })

  useEffect(() => {
    form.validate('change')
  }, [i18n.language, form])

  const handleStoreChange = (storeId: number) => {
    console.log(storeId)
    console.log(user.id)
    // const product = products.find(p => p.id === productId)
    // setSelectedProduct(product || null)
  }

  return (
    <div className="flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-screen-lg">
        <div className="flex flex-col gap-6">
          <Card className="overflow-hidden">
            <CardContent>
              <section className="body-font relative text-gray-600">
                {/* <h2 className="text-2xl font-bold">Déposer un produit</h2> */}
                <div className="container mx-auto px-5 py-10">
                  <form
                    onSubmit={e => {
                      e.preventDefault()
                      e.stopPropagation()
                      form.handleSubmit()
                    }}
                  >
                    <div className="flex flex-row gap-10">
                      <div className="flex flex-col gap-6 w-1/2">
                        <FormSectionTitle text="Paramètres du dépôt" />

                        <div className="flex gap-4">
                          <form.AppField
                            name="product.type"
                            children={field => (
                              <field.SelectField
                                options={[
                                  {
                                    value: 'harvest',
                                    label: t('database.harvest'),
                                  },
                                  {
                                    value: 'transformed',
                                    label: t('database.transformed'),
                                  },
                                ]}
                                label="Produit"
                              />
                            )}
                          />

                          <form.AppField
                            name="product.weightKg"
                            children={field => (
                              <field.TextField
                                label="Poids (kg)"
                                type="product.weightKg"
                                placeholder="0.0"
                                fieldType="number"
                              />
                            )}
                          />
                        </div>

                        <form.AppField
                          name="product.producerId"
                          children={field => (
                            <field.SelectField
                              options={traders.map(trader => ({
                                value: trader.id,
                                label:
                                  'ID ' +
                                  trader.id +
                                  ' | ' +
                                  t('database.' + trader.type) +
                                  ' | ' +
                                  trader.firstName +
                                  ' ' +
                                  trader.lastName,
                              }))}
                              label="Fournisseur"
                            />
                          )}
                        />

                        <form.AppField
                          name="product.storeId"
                          children={field => (
                            <field.SelectField
                              options={stores.map(store => ({
                                value: store.id,
                                label: store.name,
                              }))}
                              label="Magasin"
                              onChange={storeId => {
                                handleStoreChange(storeId)
                              }}
                            />
                          )}
                        />

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

                        <form.AppForm>
                          <form.SubmitButton className="w-full">
                            Enregistrer le produit
                          </form.SubmitButton>
                        </form.AppForm>
                      </div>

                      <div className="flex flex-col gap-6 w-1/2">
                        <FormSectionTitle text="Contrôle qualité" />

                        <div className="flex gap-4">
                          <form.AppField
                            name="qualityControl.qualityId"
                            children={field => (
                              <field.SelectField
                                options={qualities.map(quality => ({
                                  value: quality.id,
                                  label: quality.name,
                                }))}
                                label="Qualité"
                              />
                            )}
                          />
                          <form.AppField
                            name="qualityControl.qualityInspectorId"
                            children={field => (
                              <field.SelectField
                                options={qualityInspectors.map(qi => ({
                                  value: qi.id,
                                  label:
                                    'ID ' +
                                    qi.id +
                                    ' | ' +
                                    qi.firstName +
                                    ' ' +
                                    qi.lastName,
                                }))}
                                label="Qualiticien"
                              />
                            )}
                          />
                        </div>
                        <form.AppField
                          name="qualityControl.controlDate"
                          children={field => (
                            <field.DateTimePickerField label="Date du contrôle" />
                          )}
                        />
                        <form.AppField
                          name="qualityControl.document"
                          children={field => (
                            <field.TextField
                              label="Certificat (Photo ou PDF)"
                              type="document"
                            />
                          )}
                        />
                      </div>
                    </div>
                  </form>
                </div>
              </section>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
