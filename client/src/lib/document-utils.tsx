import { downloadDocument } from '@/api/generated'
import { toast } from 'sonner'

export const triggerBlobDownload = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  window.URL.revokeObjectURL(url)
}

export const getActualBlob = (response: any): Blob | null => {
  if (response instanceof Blob) {
    return response
  }
  if (
    response &&
    typeof response === 'object' &&
    'data' in response &&
    response.data instanceof Blob
  ) {
    return response.data
  }
  return null
}

export const createDownloadHandler = (
  setDownloadingIds: React.Dispatch<
    React.SetStateAction<Record<string | number, boolean>>
  >,
  t: (key: string) => string
) => {
  return async (docId: number, docName: string) => {
    setDownloadingIds(prev => ({ ...prev, [docId]: true }))
    try {
      const response = await downloadDocument({ path: { id: docId } })
      const actualBlob = getActualBlob(response)

      if (actualBlob) {
        triggerBlobDownload(actualBlob, docName)
      } else {
        console.error(
          'Expected a Blob response for download, but received:',
          response
        )
        toast.error(t('common.download_error'), {
          description: t(
            'admin.user_management.toasts.invalid_file_format_received'
          ),
        })
      }
    } catch (error: any) {
      console.error('Download error:', error)
      toast.error(t('common.download_error'), {
        description: error?.message || t('common.unknown_error'),
      })
    } finally {
      setDownloadingIds(prev => ({ ...prev, [docId]: false }))
    }
  }
}

export const createViewHandler = (
  setViewingIds: React.Dispatch<React.SetStateAction<Record<number, boolean>>>,
  t: (key: string) => string
) => {
  return async (docId: number) => {
    console.log('Viewing document with ID:', docId)
    setViewingIds(prev => ({ ...prev, [docId]: true }))
    try {
      const response = await downloadDocument({ path: { id: docId } })
      const actualBlob = getActualBlob(response)

      if (actualBlob) {
        const fileURL = URL.createObjectURL(actualBlob)
        const newWindow = window.open(fileURL, '_blank')
        if (!newWindow) {
          toast.error(t('common.error_general'), {
            description: t('common.popup_blocker_error_description'),
          })
        }
      } else {
        console.error(
          'Expected a Blob response for viewing, but received:',
          response
        )
        toast.error(t('common.error_general'), {
          description: t(
            'admin.user_management.toasts.invalid_file_format_received'
          ),
        })
      }
    } catch (error: any) {
      toast.error(t('common.download_error'), {
        description: error?.message || t('common.unknown_error'),
      })
    } finally {
      setViewingIds(prev => ({ ...prev, [docId]: false }))
    }
  }
}
