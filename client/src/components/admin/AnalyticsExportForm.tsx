import { getFilteredData } from '@/api/generated/sdk.gen'
import type { GetFilteredDataData } from '@/api/generated/types.gen'
import { useAppForm } from '@/components/form'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { useMutation } from '@tanstack/react-query'
import { format as formatDateFns } from 'date-fns'
import { Download } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { z } from 'zod/v4'

function downloadFile(blob: Blob | File, filename: string): void {
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', filename)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

const exportFormSchemaBase = z.object({
  start: z.iso.datetime().optional(),
  end: z.iso.datetime().optional(),
  onlyEnded: z.boolean().default(false),
  format: z.enum(['json', 'csv']).default('json'),
})

const exportFormSchema = exportFormSchemaBase.superRefine((data, ctx) => {
  if (data.start && data.end && data.end < data.start) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: 'admin.analytics.export_filtered.end_date_after_start',
      path: ['end'],
    })
  }
  if ((data.start && !data.end) || (!data.start && data.end)) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      message: 'admin.analytics.export_filtered.start_end_both_or_none',
      path: ['start'],
    })
  }
})

type ExportFormValues = z.infer<typeof exportFormSchema>

interface ExportApiParams {
  start?: string
  end?: string
  onlyEnded: boolean
  format: 'json' | 'csv'
}

export function AnalyticsExportForm() {
  const { t } = useTranslation()

  const handleExportSuccess = (
    blob: Blob | File,
    requestedFormat: 'json' | 'csv'
  ) => {
    const suggestedFilename = `auctions_export_${new Date().toISOString().split('T')[0]}.${requestedFormat}`
    downloadFile(blob, suggestedFilename)
    toast.success(t('admin.analytics.export_success_title'), {
      description: t('admin.analytics.export_success_message', {
        filename: suggestedFilename,
      }),
    })
  }

  const handleExportError = (error: any) => {
    console.error('Export error:', error)
    toast.error(t('admin.analytics.export_error_title'), {
      description: error?.message || t('admin.analytics.export_error_message'),
    })
  }

  const exportMutation = useMutation({
    mutationFn: async (params: ExportApiParams) => {
      const queryParams: GetFilteredDataData['query'] = {
        onlyEnded: params.onlyEnded,
        format: params.format,
      }
      if (params.start) {
        queryParams.start = params.start
      }
      if (params.end) {
        queryParams.end = params.end
      }

      const responseData = await getFilteredData({
        query: queryParams,
        throwOnError: true,
      })

      let blobToDownload: Blob

      if (responseData instanceof Blob || responseData instanceof File) {
        blobToDownload = responseData
      } else if (params.format === 'csv') {
        if (typeof responseData === 'string') {
          blobToDownload = new Blob([responseData], {
            type: 'text/csv;charset=utf-8;',
          })
        } else if (
          typeof responseData === 'object' &&
          responseData !== null &&
          typeof (responseData as any).data === 'string'
        ) {
          blobToDownload = new Blob([(responseData as any).data], {
            type: 'text/csv;charset=utf-8;',
          })
        } else {
          console.error(
            'CSV export: Expected string data or Blob/File, or object with data property, received:',
            typeof responseData,
            responseData
          )
          throw new Error(
            t('admin.analytics.export_error_unexpected_csv_format_dev')
          )
        }
      } else if (params.format === 'json') {
        if (typeof responseData === 'object' || Array.isArray(responseData)) {
          try {
            const jsonString = JSON.stringify(responseData)
            blobToDownload = new Blob([jsonString], {
              type: 'application/json;charset=utf-8;',
            })
          } catch (e) {
            console.error('JSON export: Error stringifying data:', e)
            throw new Error(
              t('admin.analytics.export_error_json_stringify_dev')
            )
          }
        } else {
          console.error(
            'JSON export: Expected object/array data, received:',
            typeof responseData,
            responseData
          )
          throw new Error(
            t('admin.analytics.export_error_unexpected_json_format_dev')
          )
        }
      } else {
        console.error('Unsupported format:', params.format)
        throw new Error('Unsupported export format.')
      }
      return blobToDownload
    },
    onSuccess: (blob: Blob, variables) => {
      handleExportSuccess(blob, variables.format)
    },
    onError: handleExportError,
  })

  const form = useAppForm({
    validators: {
      onChange: exportFormSchema,
    },
    defaultValues: {
      onlyEnded: false,
      format: 'json',
      start: undefined,
      end: undefined,
    } as Partial<ExportFormValues>,
    onSubmit: ({ value }) => {
      const apiParams: ExportApiParams = {
        onlyEnded: value.onlyEnded || false,
        format: value.format || 'json',
      }
      if (value.start) {
        apiParams.start = formatDateFns(value.start, "yyyy-MM-dd'T'HH:mm:ss")
      }
      if (value.end) {
        apiParams.end = formatDateFns(value.end, "yyyy-MM-dd'T'HH:mm:ss")
      }
      exportMutation.mutate(apiParams)
    },
  })

  return (
    <Card className="lg:col-span-1">
      <form
        onSubmit={e => {
          e.preventDefault()
          e.stopPropagation()
          void form.handleSubmit()
        }}
      >
        <CardHeader>
          <CardTitle className="text-lg">
            {t('admin.analytics.export_options.title')}
          </CardTitle>
          <CardDescription>
            {t('admin.analytics.export_options.description')}
          </CardDescription>
        </CardHeader>
        <CardContent className="mt-8 space-y-8">
          <form.AppField
            name="start"
            children={f => (
              <f.DateTimePickerField
                label={t('admin.analytics.export_filtered.start_date_label')}
                placeholder={t(
                  'admin.analytics.export_filtered.pick_date_placeholder'
                )}
              />
            )}
          />
          <form.AppField
            name="end"
            children={f => (
              <f.DateTimePickerField
                label={t('admin.analytics.export_filtered.end_date_label')}
                placeholder={t(
                  'admin.analytics.export_filtered.pick_date_placeholder'
                )}
              />
            )}
          />
          <form.AppField
            name="onlyEnded"
            children={f => (
              <f.CheckboxField
                label={t('admin.analytics.export_filtered.only_ended_label')}
              />
            )}
          />
          <form.AppField
            name="format"
            children={f => (
              <f.RadioGroupField
                label={t('admin.analytics.export_filtered.format_label')}
                direction="row"
                choices={[
                  {
                    value: 'json',
                    label: t('admin.analytics.export_filtered.format_json'),
                  },
                  {
                    value: 'csv',
                    label: t('admin.analytics.export_filtered.format_csv'),
                  },
                ]}
              />
            )}
          />
        </CardContent>
        <CardFooter className="flex justify-center">
          <form.Subscribe
            selector={state => [state.canSubmit, state.isSubmitting]}
          >
            {([canSubmit, isSubmitting]) => (
              <Button
                type="submit"
                className="mt-4"
                disabled={
                  !canSubmit || isSubmitting || exportMutation.isPending
                }
              >
                <Download className="mr-2 h-4 w-4" />
                {exportMutation.isPending || isSubmitting
                  ? t('common.loading')
                  : t('admin.analytics.export_filtered.button')}
              </Button>
            )}
          </form.Subscribe>
        </CardFooter>
      </form>
    </Card>
  )
}
