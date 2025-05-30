import FormContainer from '../FormContainer'
import FormSectionTitle from '../FormSectionTitle'
import {
  type HarvestProductUpdateDto,
  ProductType,
  type UserListDto,
  type HarvestProductDto,
  UserType,
} from '@/api/generated'
import {
  createProductMutation,
  createQualityControlMutation,
  listFieldsOptions,
  listProductsOptions,
  listQualitiesOptions,
  listStoresOptions,
  listUsersOptions,
} from '@/api/generated/@tanstack/react-query.gen.ts'
import {
  zHarvestProductUpdateDto,
  zQualityControlUpdateDto,
  zTransformedProductUpdateDto,
} from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { formatCoordinates } from '@/utils/formatter'
import { useStore } from '@tanstack/react-form'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'
import z from 'zod/v4'

export function ProductForm(): React.ReactElement<'div'> {
  const navigate = useNavigate()

  const { t } = useTranslation()

  const staleTime = 10_000

  const { data: harvestProducts, isLoading: isHarvestProductsLoading } =
    useQuery({
      ...listProductsOptions({ query: { productType: ProductType.HARVEST } }),
      staleTime: staleTime,
    })

  const { data: transformers, isLoading: isTransformersLoading } = useQuery({
    ...listUsersOptions({ query: { userType: UserType.TRANSFORMER } }),
    staleTime: staleTime,
  })

  const { data: producers, isLoading: isProducersLoading } = useQuery({
    ...listUsersOptions({ query: { userType: UserType.PRODUCER } }),
    staleTime: staleTime,
  })

  const { data: qualityInspectors, isLoading: isQualityInspectorsLoading } =
    useQuery({
      ...listUsersOptions({ query: { userType: UserType.QUALITY_INSPECTOR } }),
      staleTime: staleTime,
    })

  const { data: qualities, isLoading: isQualitiesLoading } = useQuery({
    ...listQualitiesOptions(),
    staleTime: staleTime,
  })

  const { data: fields, isLoading: isFieldsLoading } = useQuery({
    ...listFieldsOptions(),
    staleTime: staleTime,
  })

  const { data: stores, isLoading: isStoresLoading } = useQuery({
    ...listStoresOptions(),
    staleTime: staleTime,
  })

  // this should be handled internally by the form
  const [selectedHarvestProductsIds, setSelectedHarvestProductsIds] = useState<
    number[]
  >([])

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
    documents: z.array(z.file()).min(1),
  })

  type ProductRegistration = z.infer<typeof productSchema>

  const handleSaveProduct = async (value: ProductRegistration) => {
    try {
      const qc = await createQualityControlRequest.mutateAsync({
        body: {
          qualityControl: value.qualityControl,
          documents: value.documents,
        },
      })

      if (value.product.type === ProductType.TRANSFORMED) {
        createProductRequest.mutate({
          body: {
            ...value.product,
            qualityControlId: qc.id,
            harvestProductIds: selectedHarvestProductsIds,
          },
        })
      } else if (value.product.type === ProductType.HARVEST) {
        createProductRequest.mutate({
          body: {
            ...value.product,
            qualityControlId: qc.id,
          },
        })
      }
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
        harvestProductIds: [],
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
      documents: [],
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
                let traders: UserListDto[] = []
                if (!isProducersLoading && !isTransformersLoading) {
                  traders =
                    productType === ProductType.HARVEST
                      ? producers!
                      : transformers!
                }

                return (
                  <field.SelectField
                    options={traders.map(trader => ({
                      value: trader.id,
                      label: trader.lastName + ' ' + trader.firstName,
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
                  const gps =
                    !isFieldsLoading &&
                    fields!.find(f => f.id === field.state.value)?.address
                      .location
                  const hintText = gps
                    ? t('form.gps_label', { gps: formatCoordinates(gps) })
                    : ''
                  return (
                    <field.SelectField
                      options={
                        isFieldsLoading
                          ? []
                          : fields!
                              .filter(
                                field => field.producer?.id === producerId
                              )
                              .map(field => ({
                                value: field.id,
                                label: field.identifier!,
                              }))
                      }
                      label={t('product.form.cultivated_field_label')}
                      hint={hintText}
                    />
                  )
                }}
              />
            )}
            {productType == ProductType.TRANSFORMED && (
              <>
                <form.AppField
                  name="product.harvestProductIds"
                  children={field => (
                    <field.MultiSelectField
                      loading={isHarvestProductsLoading}
                      placeholder="Sélectionner les lots"
                      options={
                        isHarvestProductsLoading
                          ? []
                          : (harvestProducts as HarvestProductDto[]).map(
                              product => ({
                                value: product.id,
                                label:
                                  'Lot n°' +
                                  product.id +
                                  ' - ' +
                                  t('database.' + product.type) +
                                  ' (' +
                                  product.qualityControl.quality.name +
                                  ')',
                              })
                            )
                      }
                      label="Matières premières"
                      maxCount={2}
                      onChange={values => {
                        setSelectedHarvestProductsIds(values as number[])
                      }}
                    />
                  )}
                />
              </>
            )}
            <form.AppField
              name="product.storeId"
              children={field => (
                <field.SelectField
                  options={
                    isStoresLoading
                      ? []
                      : stores!.map(store => ({
                          value: store.id,
                          label: store.name,
                        }))
                  }
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

            <div className="flex gap-4">
              <div className="flex-1">
                <form.AppField
                  name="qualityControl.qualityId"
                  children={field => (
                    <field.SelectField
                      options={
                        isQualitiesLoading
                          ? []
                          : qualities!
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
                              }))
                      }
                      label={t('product.quality_label')}
                    />
                  )}
                />
              </div>
              <div className="flex-1">
                <form.AppField
                  name="qualityControl.controlDate"
                  children={field => (
                    <field.DateTimePickerField
                      label={t('product.form.control_date_label')}
                    />
                  )}
                />
              </div>
            </div>

            <form.AppField
              name="qualityControl.qualityInspectorId"
              children={field => (
                <field.SelectField
                  options={
                    isQualityInspectorsLoading
                      ? []
                      : qualityInspectors!.map(qi => ({
                          value: qi.id,
                          label: qi.lastName + ' ' + qi.firstName,
                        }))
                  }
                  label={t('product.quality_inspector_label')}
                  hint={
                    field.state.value !== -1
                      ? t('form.identifier_label', {
                          id: field.state.value,
                        })
                      : ''
                  }
                />
              )}
            />

            <form.AppField name="documents">
              {f => (
                <f.FileUploadField
                  className="col-span-full"
                  label={t('product.form.certificate_label')}
                  accept="application/pdf,image/*"
                  maxFiles={1}
                  maxSize={5}
                  required={false}
                />
              )}
            </form.AppField>
          </div>
        </div>
      </form>
    </FormContainer>
  )
}
