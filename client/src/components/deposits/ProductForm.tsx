import FormContainer from '../FormContainer'
import FormSectionTitle from '../FormSectionTitle'
import type {
  FieldDto,
  HarvestProductUpdateDto,
  QualityDto,
  StoreDetailDto,
  UserListDto,
} from '@/api/generated'
import {
  createProductMutation,
  createQualityControlMutation,
} from '@/api/generated/@tanstack/react-query.gen.ts'
import {
  zHarvestProductUpdateDto,
  zQualityControlUpdateDto,
  zTransformedProductUpdateDto,
} from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { ProductType } from '@/lib/utils'
import { formatCoordinates } from '@/utils/formatter'
import { useStore } from '@tanstack/react-form'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React from 'react'
import { useTranslation } from 'react-i18next'
import z from 'zod/v4'

interface ProductFormProps {
  users: UserListDto[]
  stores: StoreDetailDto[]
  qualities: QualityDto[]
  fields: FieldDto[]
}

export function ProductForm({
  users,
  stores,
  qualities,
  fields,
}: ProductFormProps): React.ReactElement<'div'> {
  const navigate = useNavigate()
  const { t } = useTranslation()

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
        identifier: 'TR-000',
      },
      qualityControl: {
        identifier: 'QC-000',
        controlDate: '',
        granularity: 0,
        korTest: 0,
        humidity: 0,
        qualityInspectorId: -1,
        qualityId: 0,
      },
    },
    onSubmit({ value }) {
      handleSaveProduct(value)
    },
  })

  const productType = useStore(form.store, state => state.values.product.type)

  const producerId = useStore(form.store, state => {
    if (productType === ProductType.HARVEST) {
      return (state.values.product as HarvestProductUpdateDto).producerId
    }
    return -1
  })

  return (
    <FormContainer title={t('product.form.title')}>
      <form
        onSubmit={e => {
          e.preventDefault()
          e.stopPropagation()
          form.handleSubmit()
        }}
      >
        <div className="flex flex-row gap-10">
          <div className="flex w-1/2 flex-col gap-6">
            <FormSectionTitle
              text={t('product.form.section_deposit_settings_title')}
            />

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
                      label={t('product.merchandise_label')}
                      onChange={() => {
                        form.setFieldValue('product.producerId', -1)
                        form.setFieldValue('product.transformerId', -1)
                        form.setFieldValue('product.fieldId', -1)
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
                      label={t('product.weight_in_kg_label')}
                      type="product.weightKg"
                      placeholder="0.0"
                      fieldType="number"
                    />
                  )}
                />
              </div>
            </div>

            <form.AppField
              name={`product.${productType === ProductType.HARVEST ? 'producerId' : 'transformerId'}`}
              children={field => {
                const traders = users.filter(user =>
                  productType === ProductType.HARVEST
                    ? user.type === 'producer'
                    : productType === ProductType.TRANSFORMED
                      ? user.type === 'transformer'
                      : true
                )

                return (
                  <field.SelectField
                    options={traders.map(trader => ({
                      value: trader.id,
                      label: trader.firstName + ' ' + trader.lastName,
                    }))}
                    label={
                      productType === ProductType.HARVEST
                        ? t('types.producer')
                        : t('types.transformer')
                    }
                    hint={
                      field.state.value !== -1
                        ? t('form.identifier_label', { id: field.state.value })
                        : ''
                    }
                    onChange={() => {
                      form.setFieldValue('product.fieldId', -1)
                    }}
                  />
                )
              }}
            />
            {productType === ProductType.HARVEST && (
              <form.AppField
                name="product.fieldId"
                children={field => {
                  const gps = fields.find(f => f.id === field.state.value)
                    ?.address.location
                  const hintText = gps
                    ? t('form.gps_label', { gps: formatCoordinates(gps) })
                    : ''
                  return (
                    <field.SelectField
                      options={(fields as FieldDto[])
                        .filter(field => field.producer?.id === producerId)
                        .map(field => ({
                          value: field.id,
                          label: field.identifier!,
                        }))}
                      label={t('product.form.cultivated_field_label')}
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
                    productType === ProductType.HARVEST
                      ? t('product.store_label')
                      : t('product.warehouse_label')
                  }
                />
              )}
            />

            {isError && error?.errors?.length > 0 && (
              <Alert
                variant="destructive"
                className="mt-4 mb-4 border-red-300 bg-red-50"
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
                {t('product.form.submit_button')}
              </form.SubmitButton>
            </form.AppForm>
          </div>

          <div className="flex w-1/2 flex-col gap-6">
            <FormSectionTitle
              text={t('product.form.section_quality_control_title')}
            />

            <form.AppField
              name="qualityControl.qualityId"
              children={field => (
                <field.SelectField
                  options={qualities
                    .filter(quality => {
                      return (
                        !productType ||
                        (productType === ProductType.HARVEST &&
                          quality.qualityType.name.toLowerCase() ==
                            ProductType.HARVEST.toLowerCase()) ||
                        (productType === ProductType.TRANSFORMED &&
                          quality.qualityType.name.toLowerCase() ==
                            ProductType.TRANSFORMED.toLowerCase())
                      )
                    })
                    .map(quality => ({
                      value: quality.id,
                      label: quality.name,
                    }))}
                  label={t('product.quality_label')}
                />
              )}
            />
            <form.AppField
              name="qualityControl.qualityInspectorId"
              children={field => (
                <field.SelectField
                  options={users
                    .filter(user => user.type === 'quality_inspector')
                    .map(qi => ({
                      value: qi.id,
                      label: qi.firstName + ' ' + qi.lastName,
                    }))}
                  label={t('product.quality_inspector_label')}
                  hint={
                    field.state.value !== -1
                      ? t('form.identifier_label', { id: field.state.value })
                      : ''
                  }
                />
              )}
            />
            <form.AppField
              name="qualityControl.controlDate"
              children={field => (
                <field.DateTimePickerField
                  label={t('product.form.control_date_label')}
                />
              )}
            />
            <form.AppField
              name="qualityControl.documentId"
              children={field => (
                <field.TextField
                  label={t('product.form.certificate_label')}
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
