import type {
  AuctionDtoWritable,
  HarvestProductDtoWritable,
  ProducerDetailDtoWritable,
  ProductDtoReadable,
} from '@/api/generated'
import { createAuctionMutation } from '@/api/generated/@tanstack/react-query.gen.ts'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Card, CardContent } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableRow } from '@/components/ui/table'
import { zAuction } from '@/schemas/api-schemas'
import { useUserStore } from '@/store/userStore'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

interface AuctionFormProps {
  mode: 'create' | 'update'
  products: ProductDtoReadable[]
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

export function AuctionForm({
  products,
}: AuctionFormProps): React.ReactElement<'div'> {
  const { user } = useUserStore()
  const navigate = useNavigate()
  const { t, i18n } = useTranslation()

  const createAuctionRequest = useMutation({
    ...createAuctionMutation(),
    onSuccess() {
      navigate({ to: '/ventes/mes-encheres' })
    },
    onError(error) {
      console.error('Requête invalide :', error)
    },
  })

  const { isError, error } = createAuctionRequest

  const form = useAppForm({
    validators: { onChange: zAuction },

    defaultValues: {
      price: '',
      productId: '',
      productQuantity: '',
      expirationDate: '',
    },

    onSubmit({ value }) {
      const formData = zAuction.parse(value)

      const productDto: HarvestProductDtoWritable = {
        id: formData.productId,
        type: 'harvest',
      }

      const traderDto: ProducerDetailDtoWritable = {
        id: user!.id,
        type: 'producer',
      }

      const auctionDto: AuctionDtoWritable = {
        price: formData.price,
        productQuantity: formData.productQuantity,
        expirationDate: formData.expirationDate,
        active: true,
        product: productDto,
        trader: traderDto,
        strategy: null,
        status: null,
      }

      createAuctionRequest.mutate({ body: auctionDto })
    },
  })

  useEffect(() => {
    form.validate('change')
  }, [i18n.language, form])

  const [selectedProduct, setSelectedProduct] =
    useState<ProductDtoReadable | null>(null)

  const handleProductChange = (productId: string) => {
    const product = products.find(p => p.id.toString() === productId)
    setSelectedProduct(product || null)
  }

  return (
    <div className="bg-muted flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-screen-lg">
        <div className="flex flex-col gap-6">
          <Card className="overflow-hidden">
            <CardContent>
              <section className="body-font relative text-gray-600">
                <h2 className="text-2xl font-bold">Vendre un produit</h2>
                <div className="container mx-auto px-5 py-24">
                  <div className="mx-auto">
                    <form
                      onSubmit={e => {
                        e.preventDefault()
                        e.stopPropagation()
                        form.handleSubmit()
                      }}
                    >
                      <div className="flex flex-row gap-10">
                        <div className="flex flex-col gap-6 w-1/2">
                          <FormSectionTitle text="Paramètres de la vente" />

                          <form.AppField
                            name="productId"
                            children={field => (
                              <field.SelectField
                                options={products.map(product => ({
                                  value: String(product.id),
                                  label:
                                    'Lot n°' +
                                    product.id +
                                    ' - ' +
                                    product.type +
                                    ' ' +
                                    product.weightKg +
                                    ' kg @ ' +
                                    product.store.name,
                                }))}
                                label="Produit"
                                onChange={productId => {
                                  handleProductChange(productId)
                                }}
                              />
                            )}
                          />

                          <div className="flex gap-4">
                            <form.AppField
                              name="productQuantity"
                              children={field => (
                                <field.TextField
                                  label="Quantité (kg)"
                                  type="quantity"
                                  placeholder="0.0"
                                />
                              )}
                            />

                            <form.AppField
                              name="price"
                              children={field => (
                                <field.TextField
                                  label="Prix (CFA)"
                                  type="price"
                                  placeholder="0.0"
                                />
                              )}
                            />
                          </div>

                          <form.AppField
                            name="expirationDate"
                            children={field => (
                              <field.TextField
                                label="Expiration de l'enchère"
                                type="expirationDate"
                                placeholder="2023-10-05T14:48:00.000Z"
                                // value="2023-10-05T14:48:00.000Z"
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
                              Ouvrir la vente
                            </form.SubmitButton>
                          </form.AppForm>
                        </div>

                        <div className="flex flex-col gap-6 w-1/2">
                          <FormSectionTitle text="Informations du produit" />
                          <Table>
                            <TableBody>
                              <TableRow>
                                <TableCell>Numéro du lot</TableCell>
                                <TableCell>
                                  {selectedProduct?.id || 'N/A'}
                                </TableCell>
                              </TableRow>
                              <TableRow>
                                <TableCell>Marchandise</TableCell>
                                <TableCell>
                                  {selectedProduct?.type || 'N/A'}
                                </TableCell>
                              </TableRow>
                              <TableRow>
                                <TableCell>Qualité</TableCell>
                                <TableCell>
                                  {selectedProduct?.qualityControlId || 'N/A'}
                                </TableCell>
                              </TableRow>
                              <TableRow>
                                <TableCell>Quantité déposée</TableCell>
                                <TableCell>
                                  {selectedProduct
                                    ? selectedProduct.weightKg + ' kg'
                                    : 'N/A'}
                                </TableCell>
                              </TableRow>
                              <TableRow>
                                <TableCell>Date de dépôt</TableCell>
                                <TableCell>
                                  {selectedProduct?.deliveryDate || 'N/A'}
                                </TableCell>
                              </TableRow>
                              <TableRow>
                                <TableCell>Magasin</TableCell>
                                <TableCell>N/A</TableCell>
                              </TableRow>
                            </TableBody>
                          </Table>
                        </div>
                      </div>
                    </form>
                  </div>
                </div>
              </section>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
