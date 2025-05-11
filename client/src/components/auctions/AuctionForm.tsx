import type { ProductDto } from '@/api/generated'
import { createAuctionMutation } from '@/api/generated/@tanstack/react-query.gen.ts'
import { zAuctionUpdateDto } from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Card, CardContent } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableRow } from '@/components/ui/table'
import { useAuthUser } from '@/store/userStore'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

interface AuctionFormProps {
  mode: 'create' | 'update'
  products: ProductDto[]
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
  const user = useAuthUser()
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
    validators: { onChange: zAuctionUpdateDto },
    defaultValues: {
      price: 0,
      productId: 0,
      productQuantity: 0,
      expirationDate: '',
      active: true,
      traderId: user.id,
    },
    onSubmit({ value }) {
      createAuctionRequest.mutate({ body: value })
    },
  })

  useEffect(() => {
    form.validate('change')
  }, [i18n.language, form])

  const [selectedProduct, setSelectedProduct] = useState<ProductDto | null>(
    null
  )

  const handleProductChange = (productId: number) => {
    const product = products.find(p => p.id === productId)
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
                                  value: product.id,
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
                                  fieldType="number"
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
                                  fieldType="number"
                                />
                              )}
                            />
                          </div>

                          <form.AppField
                            name="expirationDate"
                            children={field => (
                              <field.DateTimePickerField label="Expiration de l'enchère" />
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
                                  {selectedProduct?.qualityControl.quality.id ||
                                    'N/A'}
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
