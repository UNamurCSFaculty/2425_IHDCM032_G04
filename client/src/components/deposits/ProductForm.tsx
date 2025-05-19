import FormContainer from '../FormContainer'
import FormSectionTitle from '../FormSectionTitle'
import type {
  FieldDto,
  QualityDto,
  QualityInspectorDetailDto,
  StoreDetailDto,
  TraderDetailDto,
} from '@/api/generated'
import {
  createProductMutation,
  createQualityControlMutation,
  listFieldsOptions,
} from '@/api/generated/@tanstack/react-query.gen.ts'
import {
  zHarvestProductUpdateDto,
  zQualityControlUpdateDto,
  zTransformedProductUpdateDto,
} from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { ProductType, formatCoordinates } from '@/lib/utils'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import z from 'zod'

interface ProductFormProps {
  traders: TraderDetailDto[]
  stores: StoreDetailDto[]
  qualities: QualityDto[]
  qualityInspectors: QualityInspectorDetailDto[]
}

export function ProductForm({
  traders,
  stores,
  qualities,
  qualityInspectors,
}: ProductFormProps): React.ReactElement<'div'> {
  const navigate = useNavigate()
  const { t } = useTranslation()

  const [selectedProductType, setSelectedProductType] = useState(
    ProductType.TRANSFORMED
  )

  const [selectedTraderId, setSelectedTraderId] = useState<number | null>(null)

  const { data: fields } = useQuery({
    ...listFieldsOptions({ path: { userId: selectedTraderId! } }),
    staleTime: 10_000,
    enabled: !!selectedTraderId,
  })

  const createProductRequest = useMutation({
    ...createProductMutation(),
    onSuccess() {
      navigate({ to: '/depots/mes-produits' })
    },
    onError(error) {
      console.error('Invalid request:', error)
    },
  })

  const createQualityControlRequest = useMutation({
    ...createQualityControlMutation(),
    onError(error) {
      console.error('Invalid request:', error)
    },
  })

  const { isError, error } = createProductRequest

  const productSchema = z.object({
    product: z.union([zHarvestProductUpdateDto, zTransformedProductUpdateDto]),
    qualityControl: zQualityControlUpdateDto,
  })

  type ProductRegistration = z.infer<typeof productSchema>

  const handleSaveProduct = async (value: ProductRegistration) => {
    try {
      const qc = await createQualityControlRequest.mutateAsync({
        body: value.qualityControl,
      })
      createProductRequest.mutate({
        body: { ...value.product, qualityControlId: qc.id },
      })
    } catch (error) {
      console.error('Save product failed with error:', error)
    }
  }

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
        type: ProductType.TRANSFORMED,
        transformerId: -1,
        identifier: 'TR-000', //TODO: remove from DB
      },
      qualityControl: {
        identifier: 'QC-000', //TODO: remove from DB
        controlDate: '',
        granularity: 0,
        korTest: 0,
        humidity: 0,
        qualityInspectorId: -1,
        productId: 0,
        qualityId: 0,
      },
    },
    onSubmit({ value }) {
      handleSaveProduct(value)
    },
  })

  useEffect(() => {
    form.setFieldValue(
      `product.${selectedProductType === ProductType.HARVEST ? 'producerId' : 'transformerId'}`,
      -1
    )
    setSelectedTraderId(null)
  }, [form, selectedProductType])

  const filteredTraders = traders.filter(trader =>
    selectedProductType === ProductType.HARVEST
      ? trader.type === 'ProducerListDto'
      : selectedProductType === ProductType.TRANSFORMED
        ? trader.type === 'TransformerListDto'
        : true
  )

  return (
    <FormContainer title="Saisie du produit">
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
              <div className="flex-1">
                <form.AppField
                  name="product.type"
                  children={field => (
                    <field.SelectField
                      options={[
                        {
                          value: ProductType.TRANSFORMED,
                          label: t('database.transformed'),
                        },
                        {
                          value: ProductType.HARVEST,
                          label: t('database.harvest'),
                        },
                      ]}
                      label="Marchandise"
                      onChange={productType => {
                        console.log('field.state.value', field.state.value)
                        setSelectedTraderId(null)
                        setSelectedProductType(productType)
                      }}
                    />
                  )}
                />
              </div>

              <div className="flex-1">
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
            </div>

            <form.AppField
              name={`product.${selectedProductType === ProductType.HARVEST ? 'producerId' : 'transformerId'}`}
              children={field => (
                <field.SelectField
                  options={filteredTraders.map(trader => ({
                    value: trader.id,
                    label: trader.firstName + ' ' + trader.lastName,
                  }))}
                  label={
                    selectedProductType === ProductType.HARVEST
                      ? 'Producteur'
                      : 'Transformateur'
                  }
                  hint={
                    field.state.value !== -1
                      ? 'identifiant : ' + field.state.value
                      : ''
                  }
                  onChange={traderId => {
                    setSelectedTraderId(traderId)
                  }}
                />
              )}
            />
            {selectedProductType == ProductType.HARVEST && fields && (
              <form.AppField
                name="product.fieldId"
                children={field => {
                  const gps = fields.find(
                    f => f.id === field.state.value
                  )?.location
                  const hintText = gps ? 'gps : ' + formatCoordinates(gps) : ''
                  return (
                    <field.SelectField
                      options={(fields as FieldDto[]).map(field => ({
                        value: field.id,
                        label: 'Champ ' + field.identifier,
                      }))}
                      label="Origine"
                      hint={hintText}
                    />
                  )
                }}
              />
            )}

            <form.AppField
              name="product.storeId"
              children={field => (
                <field.SelectField
                  options={stores.map(store => ({
                    value: store.id,
                    label: store.name,
                  }))}
                  label={
                    selectedProductType === ProductType.HARVEST
                      ? 'Magasin'
                      : 'Entrepôt'
                  }
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
                    label: qi.firstName + ' ' + qi.lastName,
                  }))}
                  label="Qualiticien"
                  hint={
                    field.state.value !== -1
                      ? 'identifiant : ' + field.state.value
                      : ''
                  }
                />
              )}
            />
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
                  required={false}
                />
              )}
            />
          </div>
        </div>
      </form>
    </FormContainer>
  )
}
