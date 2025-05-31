import Stepper from '../Stepper'
import { type ProductDto } from '@/api/generated'
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
import { cn } from '@/lib/utils'
import { useAuthUser } from '@/store/userStore'
import { formatDate, formatWeight } from '@/utils/formatter'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle, ArrowRight, Loader2 } from 'lucide-react'
import React, { useState, useRef, useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { Table, TableBody, TableCell, TableRow } from '../ui/table'
import FormSectionTitle from '../FormSectionTitle'

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

export const AuctionForm: React.FC = () => {
  const navigate = useNavigate()
  const user = useAuthUser()
  const { t } = useTranslation()
  const containerRef = useRef<HTMLDivElement>(null)
  const isMountedRef = useRef(false) // Référence pour suivre l'état de montage

  const [step, setStep] = useState(1)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const queryClient = useQueryClient()

  const createAuctionRequest = useMutation({
    ...createAuctionMutation(),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: listAuctionsQueryKey({ query: { traderId: user.id } }),
      })
      navigate({ to: '/ventes/enchere-creee' })
    },
    onError(error) {
      console.error('Requête invalide :', error)
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
      options: {},
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

  // const stepFields: Record<number, StepField[]> = {
  //   1: ['productId', 'productQuantity', 'expirationDate', 'traderId'],
  //   2: ['price'],
  // }

  /** Valide tous les champs du step et renvoie true s’ils sont OK */
  const validateStep = async (stepToValidate: number): Promise<boolean> => {
    console.log(stepToValidate)
    // if (stepToValidate == 1) {
    // } else if (stepToValidate == 2) {
    // }

    return true
  }

  const nextStep = async () => {
    if (await validateStep(step)) {
      setStep(s => s + 1)
    }
  }
  const prevStep = () => setStep(s => s - 1)

  const handleStepClick = async (targetStep: number) => {
    if (targetStep === step) return // Ne rien faire si on clique sur l'étape actuelle

    if (targetStep < step) {
      setStep(targetStep)
    } else {
      // targetStep > step
      let allPreviousStepsValid = true
      // Valider toutes les étapes de l'actuelle (step) jusqu'à targetStep - 1
      for (let i = step; i < targetStep; i++) {
        const isStepValid = await validateStep(i)
        if (!isStepValid) {
          allPreviousStepsValid = false
          setStep(i) // Afficher l'étape qui a échoué la validation
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
      // Si ce n'est pas le rendu initial, alors on effectue le défilement
      containerRef.current?.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      })
    } else {
      // Au premier rendu, on marque que le montage est terminé
      isMountedRef.current = true
    }
  }, [step]) // Ce hook s'exécute à chaque changement de 'step'

  const [selectedProduct, setSelectedProduct] = useState<ProductDto | null>(
    null
  )

  const [pricePerKg, setPricePerKg] = useState<number>(0)

  const { data: products, isLoading: isProductsLoading } = useQuery({
    ...listProductsOptions({ query: { traderId: user.id } }),
    staleTime: 10_000,
  })

  const handleProductChange = (productId: number) => {
    const product = products!.find(p => p.id === productId)
    setSelectedProduct(product || null)
    if (product) {
      form.setFieldValue('productQuantity', product.weightKgAvailable)
    }
  }

  useEffect(() => {
    return form.store.subscribe(() => {
      if (
        form.store.state.values.price != 0 &&
        form.store.state.values.productQuantity != 0
      ) {
        setPricePerKg(
          Math.round(
            (form.store.state.values.price /
              form.store.state.values.productQuantity) *
              100
          ) / 100
        )
      } else {
        setPricePerKg(0)
      }
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
        className="container mx-auto max-w-5xl px-5 py-24"
      >
        <Card>
          <CardHeader>
            <CardTitle className="text-xl">{t('auction.form.title')}</CardTitle>
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
                <div className="flex flex-row gap-10">
                  <div className="flex w-1/2 flex-col gap-6">
                    <FormSectionTitle
                      text={t('auction.form.section_settings_title')}
                    />
                    <form.AppField
                      name="productId"
                      children={field => (
                        <field.SelectField
                          loading={isProductsLoading}
                          options={
                            isProductsLoading
                              ? []
                              : products!
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

                    <form.AppField
                      name="expirationDate"
                      children={field => (
                        <field.DateTimePickerField
                          label={t('auction.expiration_date_label')}
                        />
                      )}
                    />
                  </div>
                  <div className="flex w-1/2 flex-col gap-6">
                    <FormSectionTitle text={t('product.info_title')} />
                    <Table>
                      <TableBody>
                        <TableRow>
                          <TableCell>{t('product.lot_number_label')}</TableCell>
                          <TableCell>{selectedProduct?.id || 'N/A'}</TableCell>
                        </TableRow>
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
                <div className="flex flex-row gap-10">
                  <div className="flex w-1/2 flex-col gap-6">
                    <FormSectionTitle text="Fixation du prix" />
                    <form.AppField
                      name="price"
                      children={field => (
                        <field.TextField
                          label={t('product.price_in_cfa_label')}
                          type="price"
                          placeholder="0.0"
                          fieldType="number"
                        />
                      )}
                    />
                    {pricePerKg > 0 && (
                      <div className="text-sm text-gray-500">
                        <i>Soit {pricePerKg} CFA / kg</i>
                      </div>
                    )}
                  </div>
                  <div className="flex w-1/2 flex-col gap-6">
                    <FormSectionTitle text="Conditions de vente" />
                    <Table>
                      <TableBody>
                        <TableRow>
                          <TableCell>Stratégie d'enchère</TableCell>
                          <TableCell>
                            {auctionSettings?.defaultStrategy?.name || 'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>Prix imposé par kg</TableCell>
                          <TableCell>
                            {auctionSettings?.defaultFixedPriceKg || 'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>Prix maximum par kg</TableCell>
                          <TableCell>
                            {auctionSettings?.defaultMaxPriceKg || 'N/A'}
                          </TableCell>
                        </TableRow>
                        <TableRow>
                          <TableCell>Prix minimum par kg</TableCell>
                          <TableCell>
                            {auctionSettings?.defaultMinPriceKg || 'N/A'}
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
                  <span /> // Pour maintenir l'alignement si le bouton précédent n'est pas là
                )}

                {/* Le bouton "Suivant" est toujours dans le DOM, masqué à la dernière étape */}
                <Button
                  type="button"
                  onClick={nextStep}
                  className={cn(step >= 2 && 'hidden')} // Masqué si à l'étape 3 ou plus
                >
                  {t('pagination.next')}
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>

                {/* Le bouton "Soumettre" est toujours dans le DOM, visible uniquement à la dernière étape */}
                <Button
                  type="submit"
                  disabled={isSubmitting}
                  className={cn(step < 2 && 'hidden')} // Masqué si avant l'étape 3
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
