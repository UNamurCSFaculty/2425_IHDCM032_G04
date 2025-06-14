import Stepper from '../Stepper'
import {
  type ApiErrorResponse,
  type ErrorDetail,
  type ProductDto,
} from '@/api/generated'
import {
  createAuctionMutation,
  getAuctionSettingsOptions,
  listAuctionsQueryKey,
  listProductsOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { zAuctionUpdateDto } from '@/api/generated/zod.gen'
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
import { cn, getPricePerKg } from '@/lib/utils'
import { useAuthUser } from '@/store/userStore'
import { formatDate, formatWeight } from '@/utils/formatter'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle, ArrowRight, Loader2 } from 'lucide-react'
import React, { useState, useRef, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { Table, TableBody, TableCell, TableRow } from '../ui/table'
import FormSectionTitle from '../FormSectionTitle'
import { toast } from 'sonner'

/**
 * Composant React pour afficher un panneau d'étape dans le formulaire
 * Utilisé pour gérer l'affichage des différentes étapes du formulaire de création d'enchères.
 */
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

/**
 * Composant React pour le formulaire de création d'enchères
 * Permet aux utilisateurs de créer une nouvelle enchère en plusieurs étapes.
 * Le formulaire est divisé en deux étapes :
 * 1. Sélection du produit et de la quantité
 * 2. Définition du prix et des conditions de vente
 */
export const AuctionForm: React.FC = () => {
  const navigate = useNavigate()
  const user = useAuthUser()
  const { t } = useTranslation()
  const containerRef = useRef<HTMLDivElement>(null)
  const isMountedRef = useRef(false)

  const [step, setStep] = useState(1)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const queryClient = useQueryClient()

  const createAuctionRequest = useMutation({
    ...createAuctionMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: listAuctionsQueryKey({ query: { traderId: user.id } }),
      })

      toast.success(t('auction.form.created_ok'), {
        duration: 3000,
      })

      navigate({ to: '/ventes/mes-encheres' })
    },
    onError(error: ApiErrorResponse) {
      toast.error(t('auction.form.created_fail') + ' (' + error.code + ')', {
        duration: 3000,
      })
    },
  })

  const { isError, error } = createAuctionRequest

  const form = useAppForm({
    validators: { onChange: zAuctionUpdateDto },
    defaultValues: {
      price: 0,
      productId: 0,
      productQuantity: 0,
      expirationDate: '',
      traderId: user.id,
      active: true,
    },
    async onSubmit({ value }) {
      try {
        setIsSubmitting(true)
        await createAuctionRequest.mutateAsync({ body: value })
      } finally {
        setIsSubmitting(false)
      }
    },
  })

  const validateStep = async (): Promise<boolean> => {
    return true
  }

  const nextStep = async () => {
    if (await validateStep()) {
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
        const isStepValid = await validateStep()
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

  const [selectedProduct, setSelectedProduct] = useState<ProductDto | null>(
    null
  )

  const [pricePerKg, setPricePerKg] = useState<number>(0)

  const { data: products, isLoading: isProductsLoading } = useQuery({
    ...listProductsOptions({ query: { traderId: user.id } }),
    staleTime: 10_000,
  })

  const handleProductChange = (productId: number) => {
    const product = (products as ProductDto[]).find(p => p.id === productId)
    setSelectedProduct(product || null)
    if (product) {
      form.setFieldValue('productQuantity', product.weightKgAvailable)
    }
  }

  useEffect(() => {
    return form.store.subscribe(() => {
      setPricePerKg(
        getPricePerKg(
          form.store.state.values.price,
          form.store.state.values.productQuantity
        )
      )
    })
  }, [form.store])

  const { data: auctionSettings } = useQuery({
    ...getAuctionSettingsOptions(),
    staleTime: 10_000,
  })

  return (
    <section className="body-font relative text-gray-600">
      <BreadcrumbSection
        titleKey="app.auctions_new_sale.title"
        subtitleKey="app.auctions_new_sale.subtitle"
        breadcrumbs={[
          { labelKey: 'breadcrumb.vendre' },
          { labelKey: 'breadcrumb.new_auction' },
        ]}
      />

      <div
        ref={containerRef}
        className="container mx-auto max-w-5xl px-2 py-12 sm:px-5 md:py-24"
      >
        <Card>
          <CardHeader>
            <CardTitle className="text-xl font-semibold">
              {t('auction.form.title')}
            </CardTitle>
            <CardDescription></CardDescription>
          </CardHeader>
          <CardContent>
            <Stepper step={step} onStepClick={handleStepClick} totalSteps={2} />

            <form
              onSubmit={e => {
                e.preventDefault()
                form.handleSubmit()
              }}
              className="relative h-fit space-y-6"
            >
              <StepPanel index={1} current={step}>
                <div className="flex flex-col gap-6 md:flex-row md:gap-10">
                  <div className="flex w-full flex-col gap-6 md:w-1/2">
                    <FormSectionTitle
                      text={t('auction.form.section_select_product_title')}
                    />
                    <form.AppField
                      name="productId"
                      children={field => (
                        <field.SelectField
                          loading={isProductsLoading}
                          options={
                            isProductsLoading
                              ? []
                              : (products as ProductDto[])
                                  .filter(
                                    product => product.weightKgAvailable > 0
                                  )
                                  .sort((a, b) => a.id - b.id)
                                  .map(product => ({
                                    value: product.id,
                                    label:
                                      t('auction.lot_label', {
                                        id: product.id,
                                      }) +
                                      ' - ' +
                                      t('database.' + product.type) +
                                      ' @ ' +
                                      product.qualityControl.quality.name,
                                  }))
                          }
                          disabled={
                            !isProductsLoading &&
                            (products as ProductDto[]).filter(
                              product => product.weightKgAvailable
                            ).length === 0
                          }
                          label={t('product.product_label')}
                          onChange={productId => {
                            handleProductChange(productId)
                          }}
                        />
                      )}
                    />

                    <form.AppField
                      name="productQuantity"
                      children={field => (
                        <field.TextField
                          label={t('product.quantity_in_kg_label')}
                          type="quantity"
                          placeholder="0.0"
                          fieldType="number"
                        />
                      )}
                    />
                  </div>
                  <div className="flex w-full flex-col gap-6 md:w-1/2">
                    <FormSectionTitle text={t('product.info_title')} />
                    <Table>
                      <TableBody>
                        <TableRow>
                          <TableCell>
                            {t('product.merchandise_label')}
                          </TableCell>
                          <TableCell>
                            {selectedProduct
                              ? t('database.' + selectedProduct.type)
                              : 'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>{t('product.quality_label')}</TableCell>
                          <TableCell>
                            {selectedProduct?.qualityControl.quality.name ||
                              'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>
                            {t('product.deposited_quantity_label')}
                          </TableCell>
                          <TableCell>
                            {selectedProduct
                              ? formatWeight(selectedProduct.weightKg)
                              : 'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>
                            {t('product.deposited_quantity_available_label')}
                          </TableCell>
                          <TableCell>
                            {selectedProduct
                              ? formatWeight(selectedProduct.weightKgAvailable)
                              : 'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>
                            {t('product.deposit_date_label')}
                          </TableCell>
                          <TableCell>
                            {selectedProduct
                              ? formatDate(selectedProduct.deliveryDate)
                              : 'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>{t('product.store_label')}</TableCell>
                          <TableCell>
                            {selectedProduct?.store.name || 'N/A'}
                          </TableCell>
                        </TableRow>
                      </TableBody>
                    </Table>
                  </div>
                </div>
              </StepPanel>

              <StepPanel index={2} current={step}>
                <div className="flex flex-col gap-6 md:flex-row md:gap-10">
                  <div className="flex w-full flex-col gap-6 md:w-1/2">
                    <FormSectionTitle
                      text={t('auction.form.section_pricing_title')}
                    />
                    <form.AppField
                      name="price"
                      children={field => (
                        <field.TextField
                          label={t('product.price_in_cfa_label')}
                          type="price"
                          placeholder="0.0"
                          fieldType="number"
                          hint={pricePerKg + ' CFA/kg'}
                        />
                      )}
                    />
                    <form.AppField
                      name="expirationDate"
                      children={field => (
                        <field.DateTimePickerField
                          label={t('auction.expiration_date_label')}
                        />
                      )}
                    />
                  </div>
                  <div className="flex w-full flex-col gap-6 md:w-1/2">
                    <FormSectionTitle
                      text={t('auction.form.section_sale_conditions_title')}
                    />

                    <Table>
                      <TableBody>
                        <TableRow>
                          <TableCell>
                            {t('auction.table.strategy_label')}
                          </TableCell>
                          <TableCell>
                            {auctionSettings?.defaultStrategy?.name ||
                              t('common.not_applicable_short')}
                          </TableCell>
                        </TableRow>

                        <TableRow>
                          <TableCell>
                            {t('auction.table.fixed_price_per_kg_label')}
                          </TableCell>
                          <TableCell>
                            {auctionSettings?.defaultFixedPriceKg ??
                              t('common.not_applicable_short')}
                          </TableCell>
                        </TableRow>

                        <TableRow>
                          <TableCell>
                            {t('auction.table.max_price_per_kg_label')}
                          </TableCell>
                          <TableCell>
                            {auctionSettings?.defaultMaxPriceKg ??
                              t('common.not_applicable_short')}
                          </TableCell>
                        </TableRow>

                        <TableRow>
                          <TableCell>
                            {t('auction.table.min_price_per_kg_label')}
                          </TableCell>
                          <TableCell>
                            {auctionSettings?.defaultMinPriceKg ??
                              t('common.not_applicable_short')}
                          </TableCell>
                        </TableRow>
                      </TableBody>
                    </Table>
                  </div>
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
                      {error.errors.map((err: ErrorDetail, i: number) => (
                        <li key={i}>
                          {t('errors.' + err.code)}
                          {err.field ? `: ${t(err.message)}` : ''}
                        </li>
                      ))}
                    </ul>
                  </AlertDescription>
                </Alert>
              )}

              <div className="flex flex-col gap-4 sm:flex-row sm:justify-between">
                {step > 1 ? (
                  <Button
                    type="button"
                    variant="outline"
                    onClick={prevStep}
                    className="w-full sm:w-auto"
                  >
                    {t('pagination.previous')}
                  </Button>
                ) : (
                  <span className="hidden sm:inline-block" /> // Garde l'espace sur sm+
                )}

                <Button
                  type="button"
                  onClick={nextStep}
                  className={cn('w-full sm:w-auto', step >= 2 && 'hidden')}
                >
                  {t('pagination.next')}
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>

                <Button
                  type="submit"
                  disabled={isSubmitting}
                  className={cn('w-full sm:w-auto', step < 2 && 'hidden')}
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
