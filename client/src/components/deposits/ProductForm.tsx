import FormSectionTitle from '../FormSectionTitle'
import {
  type HarvestProductUpdateDto,
  ProductType,
  type UserListDto,
  UserType,
  type ApiErrorResponse,
  type FieldDto,
  type StoreDetailDto,
  type ErrorDetail,
  type QualityDto,
  type QualityInspectorDetailDto,
  type ProductDto,
  type TransformedProductUpdateDto,
  type AuctionDto,
} from '@/api/generated'
import {
  createProductMutation,
  createQualityControlMutation,
  listAuctionsOptions,
  listFieldsOptions,
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
import { TradeStatus } from '@/lib/utils'
import { useAuthUser } from '@/store/userStore'
import { formatCoordinates } from '@/utils/formatter'
import { useStore } from '@tanstack/react-form'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import z from 'zod/v4'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '../ui/card'
import { BreadcrumbSection } from '../BreadcrumbSection'

/**
 * Formulaire pour la création ou la mise à jour d'un produit
 * Permet de saisir les informations relatives à un produit de récolte ou transformé
 * ainsi que les contrôles qualité associés.
 */
export function ProductForm(): React.ReactElement<'div'> {
  const navigate = useNavigate()
  const user = useAuthUser()
  const { t } = useTranslation()
  const containerRef = useRef<HTMLDivElement>(null)
  const isMountedRef = useRef(false)

  useEffect(() => {
    if (isMountedRef.current) {
      containerRef.current?.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      })
    } else {
      isMountedRef.current = true
    }
  }, [])

  const staleTime = 10_000

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

  // TODO: Ceci devrait être géré en interne par le formulaire
  const [selectedHarvestProductsIds, setSelectedHarvestProductsIds] = useState<
    number[]
  >([])

  const createProductRequest = useMutation({
    ...createProductMutation(),
    onSuccess(data: ProductDto) {
      toast.success(t('product.form.created_ok') + ' ID: ' + data.id, {
        duration: 3000,
      })

      navigate({ to: '/depots/mes-produits' })
    },
    onError(error: ApiErrorResponse) {
      toast.error(t('product.form.created_fail') + ' (' + error.code + ')', {
        duration: 3000,
      })
    },
  })

  const createQualityControlRequest = useMutation({
    ...createQualityControlMutation(),
    onError(error: ApiErrorResponse) {
      toast.error(t('product.form.created_fail') + ' (' + error.code + ')', {
        duration: 3000,
      })
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
  }

  const form = useAppForm({
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
    } as ProductRegistration,
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

  const transformerId = useStore(form.store, state => {
    if (productType === ProductType.TRANSFORMED) {
      return (state.values.product as TransformedProductUpdateDto).transformerId
    }
    return -1
  })

  // Récupère les enchères gagnées par l'utilisateur.
  // Un transformateur ne peut référencer que des produits de récolte
  // provenant d'enchères qu'il a remportées.
  const listAuctionsQueryOptions = () => ({
    ...listAuctionsOptions({
      query: { buyerId: transformerId, status: TradeStatus.ACCEPTED },
    }),
    staleTime: 10_000,
  })

  const { data: auctions, isLoading: isAuctionsLoading } = useQuery({
    ...listAuctionsQueryOptions(),
    enabled: !!transformerId && transformerId !== -1,
  })

  return (
    <section className="body-font relative text-gray-600">
      <BreadcrumbSection
        titleKey="app.deposits_new_product.title"
        subtitleKey="app.deposits_new_product.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.deposits' },
          { labelKey: 'breadcrumb.new_deposit' },
        ]}
      />

      <div
        ref={containerRef}
        className="container mx-auto max-w-5xl px-2 py-12 sm:px-5 md:py-24"
      >
        <Card>
          <CardHeader>
            <CardTitle className="text-xl font-semibold">
              {t('product.form.title')}
            </CardTitle>
            <CardDescription></CardDescription>
          </CardHeader>
          <CardContent>
            <form
              onSubmit={e => {
                e.preventDefault()
                e.stopPropagation()
                form.handleSubmit()
              }}
            >
              <div className="flex flex-col gap-6 md:flex-row md:gap-10">
                <div className="flex w-full flex-col gap-6 md:w-1/2">
                  <FormSectionTitle
                    text={t('product.form.section_deposit_settings_title')}
                  />

                  <div className="flex flex-col gap-4 sm:flex-row">
                    <div className="flex-1">
                      <form.AppField
                        name="product.type"
                        children={field => (
                          <field.SelectField
                            className="w-full text-sm font-semibold"
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
                        <field.VirtualizedSelectField
                          required
                          options={traders.map(trader => ({
                            id: trader.id,
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
                              ? t('form.identifier_label', {
                                  id: field.state.value,
                                })
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
                          (fields as FieldDto[]).find(
                            f => f.id === field.state.value
                          )?.address.location
                        const hintText = gps
                          ? t('form.gps_label', { gps: formatCoordinates(gps) })
                          : ''
                        return (
                          <field.VirtualizedSelectField
                            options={
                              isFieldsLoading
                                ? []
                                : (fields as FieldDto[])
                                    .filter(
                                      field => field.producer?.id === producerId
                                    )
                                    .map(field => ({
                                      id: field.id,
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
                            loading={isAuctionsLoading}
                            placeholder={t('product.select_lots')}
                            selectAllLabel="Tout"
                            searchLabel="Recherche..."
                            closeLabel="Fermer"
                            clearLabel="Effacer"
                            emptySearchLabel="Aucun lot"
                            required={false}
                            options={
                              isAuctionsLoading || auctions === undefined
                                ? []
                                : (auctions as AuctionDto[])
                                    .filter(
                                      auction =>
                                        auction.bids.some(
                                          bid =>
                                            bid.trader.id === transformerId &&
                                            bid.status.name ===
                                              TradeStatus.ACCEPTED
                                        ) &&
                                        auction.product.type ===
                                          ProductType.HARVEST
                                    )
                                    .map(auction => ({
                                      value: String(auction.product.id),
                                      label:
                                        'Lot n°' +
                                        auction.product.id +
                                        ' - ' +
                                        t('database.' + auction.product.type) +
                                        ' (' +
                                        auction.product.qualityControl.quality
                                          .name +
                                        ')',
                                    }))
                            }
                            label={t('product.raw_materials_label')}
                            onChange={items => {
                              const ids = items.map(i => Number(i))
                              setSelectedHarvestProductsIds(ids)
                            }}
                          />
                        )}
                      />
                    </>
                  )}
                  <form.AppField
                    name="product.storeId"
                    children={field => (
                      <field.VirtualizedSelectField
                        options={
                          isStoresLoading
                            ? []
                            : (stores as StoreDetailDto[])
                                .filter(store => store.userId === user.id)
                                .map(store => ({
                                  id: store.id,
                                  value: store.id,
                                  label: store.name,
                                }))
                        }
                        label={t('product.warehouse_label')}
                      />
                    )}
                  />
                </div>

                <div className="flex w-full flex-col gap-6 md:w-1/2">
                  <FormSectionTitle
                    text={t('product.form.section_quality_control_title')}
                  />

                  <div className="flex flex-col gap-4 sm:flex-row">
                    <div className="flex-1">
                      <form.AppField
                        name="qualityControl.qualityId"
                        children={field => (
                          <field.VirtualizedSelectField
                            options={
                              isQualitiesLoading
                                ? []
                                : (qualities as QualityDto[])
                                    .filter(quality => {
                                      return (
                                        !productType ||
                                        (productType === ProductType.HARVEST &&
                                          quality.qualityType.name.toLowerCase() ==
                                            ProductType.HARVEST.toLowerCase()) ||
                                        (productType ===
                                          ProductType.TRANSFORMED &&
                                          quality.qualityType.name.toLowerCase() ==
                                            ProductType.TRANSFORMED.toLowerCase())
                                      )
                                    })
                                    .map(quality => ({
                                      id: quality.id,
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
                      <field.VirtualizedSelectField
                        options={
                          isQualityInspectorsLoading
                            ? []
                            : (
                                qualityInspectors as QualityInspectorDetailDto[]
                              ).map(qi => ({
                                id: qi.id,
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
              {isError && error?.errors?.length > 0 && (
                <Alert
                  variant="destructive"
                  className="mt-8 mb-4 border-red-300 bg-red-50"
                >
                  <AlertCircle className="h-4 w-4" />
                  <AlertTitle>{t('common.error')}</AlertTitle>
                  <AlertDescription>
                    <ul className="list-disc pl-4">
                      {error.errors.map((err: ErrorDetail, idx: number) => (
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
              <div className="mt-8 flex justify-center">
                <form.AppForm>
                  <form.SubmitButton className="w-full sm:w-auto sm:min-w-[200px] md:max-w-xs">
                    {t('product.form.submit_button')}
                  </form.SubmitButton>
                </form.AppForm>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </section>
  )
}
