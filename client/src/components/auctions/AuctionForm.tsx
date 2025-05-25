import FormContainer from '../FormContainer'
import FormSectionTitle from '../FormSectionTitle'
import type { ProductDto } from '@/api/generated'
import { createAuctionMutation } from '@/api/generated/@tanstack/react-query.gen.ts'
import { zAuctionUpdateDto } from '@/api/generated/zod.gen'
import { useAppForm } from '@/components/form'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Table, TableBody, TableCell, TableRow } from '@/components/ui/table'
import { useAuthUser } from '@/store/userStore'
import { formatDate, formatWeight } from '@/utils/formatter'
import { useMutation } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { AlertCircle } from 'lucide-react'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

interface AuctionFormProps {
  products: ProductDto[]
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
    if (product) {
      form.setFieldValue('productQuantity', product.weightKg)
    }
  }

  return (
    <FormContainer title={t('auction.form.title')}>
      <form
        onSubmit={e => {
          e.preventDefault()
          e.stopPropagation()
          form.handleSubmit()
        }}
      >
        <div className="flex flex-row gap-10">
          <div className="flex w-1/2 flex-col gap-6">
            <FormSectionTitle text={t('auction.form.section_settings_title')} />

            <form.AppField
              name="productId"
              children={field => (
                <field.SelectField
                  options={products.map(product => ({
                    value: product.id,
                    label:
                      t('auction.lot_label', { id: product.id }) +
                      ' - ' +
                      t('database.' + product.type) +
                      ' ' +
                      formatWeight(product.weightKg) +
                      ' @ ' +
                      product.store.name,
                  }))}
                  label={t('product.product_label')}
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
                    label={t('product.quantity_in_kg_label')}
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
                    label={t('product.price_in_cfa_label')}
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
                <field.DateTimePickerField
                  label={t('auction.expiration_date_label')}
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
                {t('auction.form.submit_button')}
              </form.SubmitButton>
            </form.AppForm>
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
                  <TableCell>{t('product.merchandise_label')}</TableCell>
                  <TableCell>
                    {selectedProduct
                      ? t('database.' + selectedProduct.type)
                      : 'N/A'}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>{t('product.quality_label')}</TableCell>
                  <TableCell>
                    {selectedProduct?.qualityControl.quality.name || 'N/A'}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>{t('product.deposited_quantity_label')}</TableCell>
                  <TableCell>
                    {selectedProduct
                      ? formatWeight(selectedProduct.weightKg)
                      : 'N/A'}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>{t('product.deposit_date_label')}</TableCell>
                  <TableCell>
                    {selectedProduct
                      ? formatDate(selectedProduct.deliveryDate)
                      : 'N/A'}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell>{t('product.store_label')}</TableCell>
                  <TableCell>{selectedProduct?.store.name || 'N/A'}</TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </div>
        </div>
      </form>
    </FormContainer>
  )
}
