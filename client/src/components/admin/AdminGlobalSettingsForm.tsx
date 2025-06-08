import { useAppForm } from '@/components/form'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Loader2, AlertCircle } from 'lucide-react'
import { useMutation, useQuery } from '@tanstack/react-query'
import { type GlobalSettingsUpdateDto } from '@/api/generated'
import { zGlobalSettingsUpdateDto } from '@/api/generated/zod.gen'
import { useTranslation } from 'react-i18next'
import { useEffect, useMemo } from 'react'
import {
  getGlobalSettingsOptions,
  updateGlobalSettingsMutation,
  listAuctionStrategiesOptions,
} from '@/api/generated/@tanstack/react-query.gen'
import { toast } from 'sonner'

const zGlobalSettingsForm = zGlobalSettingsUpdateDto

/**
 * Composant pour gérer les paramètres globaux de l'application
 */
export const AdminGlobalSettingsForm: React.FC = () => {
  const { t } = useTranslation()

  const { data: globalSettings, isLoading: isLoadingSettings } = useQuery({
    ...getGlobalSettingsOptions(),
  })

  const { data: auctionStrategies, isLoading: isLoadingStrategies } = useQuery({
    ...listAuctionStrategiesOptions(),
  })

  const mutation = useMutation({
    ...updateGlobalSettingsMutation(),
    onSuccess: () =>
      toast.success(t('settings.update_success'), {
        duration: 3000,
      }),
  })

  const form = useAppForm({
    validators: { onChange: zGlobalSettingsForm },
    defaultValues: {
      defaultStrategyId: globalSettings?.defaultStrategy?.id ?? '',
      defaultFixedPriceKg: globalSettings?.defaultFixedPriceKg ?? '',
      defaultMaxPriceKg: globalSettings?.defaultMaxPriceKg ?? '',
      defaultMinPriceKg: globalSettings?.defaultMinPriceKg ?? '',
      showOnlyActive: globalSettings?.showOnlyActive ?? false,
      forceBetterBids: globalSettings?.forceBetterBids ?? false,
      minIncrement: globalSettings?.minIncrement ?? 1,
    } as GlobalSettingsUpdateDto,
    async onSubmit({ value }) {
      await mutation.mutateAsync({ body: value })
    },
  })

  const strategyOptions = useMemo(() => {
    return (
      auctionStrategies?.map(strategy => ({
        id: strategy.id,
        label: strategy.name,
      })) || []
    )
  }, [auctionStrategies])

  useEffect(() => {
    if (globalSettings && auctionStrategies) {
      form.reset()
    }
  }, [globalSettings, auctionStrategies, form])

  const { isError, error } = mutation

  const isLoading = isLoadingSettings || isLoadingStrategies

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">{t('settings.title')}</CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="flex justify-center">
            <Loader2 className="h-6 w-6 animate-spin" />
          </div>
        ) : (
          <form
            onSubmit={e => {
              e.preventDefault()
              form.handleSubmit()
            }}
            className="space-y-6"
          >
            <form.AppField name="defaultStrategyId">
              {f => (
                <f.VirtualizedSelectField
                  label={t('settings.default_strategy')}
                  options={strategyOptions}
                  placeholder={t('settings.select_strategy_placeholder')}
                  required
                  disabled={isLoadingStrategies}
                />
              )}
            </form.AppField>

            <form.AppField name="defaultFixedPriceKg">
              {f => (
                <f.NumberField
                  label={t('settings.default_fixed_price_kg')}
                  required={false}
                />
              )}
            </form.AppField>

            <form.AppField name="defaultMaxPriceKg">
              {f => (
                <f.NumberField
                  label={t('settings.default_max_price_kg')}
                  required
                />
              )}
            </form.AppField>

            <form.AppField name="defaultMinPriceKg">
              {f => (
                <f.NumberField
                  label={t('settings.default_min_price_kg')}
                  required
                />
              )}
            </form.AppField>

            <form.AppField name="showOnlyActive">
              {f => (
                <f.CheckboxField
                  label={t('settings.show_only_active')}
                  required={false}
                />
              )}
            </form.AppField>

            <form.AppField name="forceBetterBids">
              {f => (
                <f.CheckboxField
                  label={t('settings.force_better_bids')}
                  required={false}
                />
              )}
            </form.AppField>

            <form.AppField name="minIncrement">
              {f => (
                <f.NumberField label={t('settings.min_increment')} required />
              )}
            </form.AppField>

            {isError && error?.errors?.length > 0 && (
              <Alert variant="destructive" className="border-red-300 bg-red-50">
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

            <Button
              type="submit"
              disabled={mutation.isPending || isLoading}
              className="w-full"
            >
              {mutation.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  {t('buttons.submitting')}
                </>
              ) : (
                t('buttons.submit')
              )}
            </Button>
          </form>
        )}
      </CardContent>
    </Card>
  )
}
