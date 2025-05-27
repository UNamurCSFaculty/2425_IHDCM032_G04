import React from 'react'
import { useTranslation } from 'react-i18next'
import { format } from 'date-fns'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { FileText, Download, Loader2, Eye } from 'lucide-react'
import { formatFileSize } from '@/lib/utils'
import type { AppUserDetailDto } from '@/schemas/api-schemas'
import { SelectorMap } from '@/components/map/SelectorMap'

import citiesData from '@/data/cities.json'
import regionsData from '@/data/regions.json'

interface UserDetailsDisplayProps {
  user: AppUserDetailDto
  onDownloadDocument?: (docId: number, docName: string) => Promise<void>
  onViewDocument?: (docId: number, docName: string) => Promise<void>
  downloadingDocIds?: Record<number, boolean>
  viewingDocIds?: Record<number, boolean>
}

export const UserDetailsDisplay: React.FC<UserDetailsDisplayProps> = ({
  user,
  onDownloadDocument,
  onViewDocument,
  downloadingDocIds = {},
  viewingDocIds = {},
}) => {
  const { t } = useTranslation()

  let cityName: string | null = null
  let regionName: string | null = null

  if (user.address) {
    if (user.address.cityId && citiesData[user.address.cityId - 1]) {
      cityName = citiesData[user.address.cityId - 1]
    }
    if (user.address.regionId && regionsData[user.address.regionId - 1]) {
      regionName = regionsData[user.address.regionId - 1]
    }
  }

  return (
    <div className="space-y-8">
      <div className="grid grid-cols-1 gap-x-8 lg:grid-cols-2">
        <div className="space-y-4">
          <h3 className="text-lg font-semibold">
            {t('form.sections.user_details')}
          </h3>
          <div className="space-y-2 text-sm">
            <div>
              <strong>{t('form.full_name')}:</strong> {user.firstName}{' '}
              {user.lastName}
            </div>
            <div>
              <strong>{t('form.mail')}:</strong> {user.email}
            </div>
            <div>
              <strong>{t('form.phone')}:</strong> {user.phone || '-'}
            </div>
            <div>
              <strong>{t('form.type')}:</strong>{' '}
              <Badge variant="outline" className="capitalize">
                {t(`types.${user.type}` as any)}
              </Badge>
            </div>
            <div>
              <strong>{t('form.language')}:</strong>{' '}
              {user.language?.name || user.language?.code || '-'}
            </div>
            <div>
              <strong>{t('form.submitted_at')}:</strong>{' '}
              {user.registrationDate
                ? format(new Date(user.registrationDate), 'PPp')
                : '-'}
            </div>
            {user.validationDate && (
              <div>
                <strong>{t('form.validation_date')}:</strong>{' '}
                {format(new Date(user.validationDate), 'PPp')}
              </div>
            )}
            <div>
              <strong>{t('form.status')}:</strong>{' '}
              {user.enabled ? t('status.active') : t('status.inactive')}
            </div>

            {user.type === 'producer' && user.agriculturalIdentifier && (
              <div>
                <strong>{t('form.agricultural_identifier')}:</strong>{' '}
                {user.agriculturalIdentifier}
              </div>
            )}
            {user.type === 'producer' && user.cooperative && (
              <div>
                <strong>{t('form.cooperative.label')}:</strong>{' '}
                {user.cooperative.name}
              </div>
            )}
            {user.type === 'carrier' &&
              user.pricePerKm !== undefined &&
              user.pricePerKm !== null && (
                <div>
                  <strong>{t('form.price_per_km')}:</strong> {user.pricePerKm}
                </div>
              )}
            {user.type === 'carrier' &&
              user.radius !== undefined &&
              user.radius !== null && (
                <div>
                  <strong>{t('form.radius')}:</strong> {user.radius} km
                </div>
              )}
          </div>
        </div>

        <div className="mt-4 space-y-4 lg:mt-0">
          {user.address && (
            <>
              <h3 className="text-lg font-semibold">{t('form.address')}</h3>
              <div className="text-sm">
                <p>
                  <strong>{t('form.street_quarter')}:</strong>{' '}
                  {user.address.street || t('common.not_provided')}
                </p>
                <p>
                  <strong>{t('form.city')}:</strong>{' '}
                  {cityName || t('common.not_provided')}
                </p>
                <p>
                  <strong>{t('address.region_label')}:</strong>{' '}
                  {regionName || t('common.not_provided')}
                </p>

                {user.address.location && (
                  <div className="mt-2">
                    <p className="mb-1">
                      <span className="font-medium">{t('form.location')}:</span>
                    </p>
                    <SelectorMap
                      initialPosition={user.address.location as any}
                      mapHeight="200px"
                      showSearch={false}
                      isDisplayOnly={true}
                      defaultZoom={13}
                      radius={
                        user.type === 'carrier' &&
                        typeof user.radius === 'number'
                          ? user.radius * 1000
                          : 0
                      }
                    />
                    <p className="mt-1 text-xs text-gray-500">
                      {t('form.coordinates_label')} {user.address.location}
                    </p>
                  </div>
                )}
                {!user.address.location && (
                  <p className="mt-1">
                    <span className="font-medium">{t('form.location')}:</span>{' '}
                    {t('common.not_provided')}
                  </p>
                )}
              </div>
            </>
          )}
          {!user.address && (
            <>
              <h3 className="text-lg font-semibold">{t('form.address')}</h3>
              <p className="text-muted-foreground text-sm">
                {t('common.not_provided')}
              </p>
            </>
          )}
        </div>
      </div>

      <div>
        {user.documents && user.documents.length > 0 && (
          <>
            <h3 className="mt-4 text-lg font-semibold">
              {t('form.documents')}
            </h3>
            <div className="mt-2 space-y-2">
              {user.documents.map(doc => (
                <div
                  key={doc.id}
                  className="flex items-center justify-between rounded-md border p-3"
                >
                  <div className="flex flex-grow items-center space-x-3 overflow-hidden">
                    <FileText className="text-muted-foreground h-5 w-5 flex-shrink-0" />
                    <div className="flex-grow overflow-hidden">
                      <p
                        className="truncate text-sm font-medium"
                        title={doc.originalFilename || `document-${doc.id}`}
                      >
                        {doc.originalFilename || `document-${doc.id}`}
                      </p>
                      <p className="text-muted-foreground text-xs">
                        {formatFileSize(doc.size as any as number)}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2">
                    {onViewDocument && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() =>
                          onViewDocument(
                            doc.id,
                            doc.originalFilename || `document-${doc.id}`
                          )
                        }
                        disabled={
                          viewingDocIds[doc.id] || downloadingDocIds[doc.id]
                        }
                      >
                        {viewingDocIds[doc.id] ? (
                          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        ) : (
                          <Eye className="mr-2 h-4 w-4" />
                        )}
                        {viewingDocIds[doc.id]
                          ? t('buttons.loading')
                          : t('buttons.view')}
                      </Button>
                    )}
                    {onDownloadDocument && (
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() =>
                          onDownloadDocument(
                            doc.id,
                            doc.originalFilename || `document-${doc.id}`
                          )
                        }
                        disabled={
                          downloadingDocIds[doc.id] || viewingDocIds[doc.id]
                        }
                      >
                        {downloadingDocIds[doc.id] ? (
                          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        ) : (
                          <Download className="mr-2 h-4 w-4" />
                        )}
                        {downloadingDocIds[doc.id]
                          ? t('buttons.downloading')
                          : t('buttons.download')}
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
        {(!user.documents || user.documents.length === 0) && (
          <>
            <h3 className="mt-4 text-lg font-semibold">
              {t('form.documents')}
            </h3>
            <p className="text-muted-foreground mt-2 text-sm">
              {t('admin.user_management.no_documents_submitted')}
            </p>
          </>
        )}
      </div>
    </div>
  )
}
