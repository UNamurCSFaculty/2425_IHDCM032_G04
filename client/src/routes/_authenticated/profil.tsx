import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import type { AppUserDetailDto } from '@/schemas/api-schemas'
import { useAuthUser } from '@/store/userStore'
import { createFileRoute } from '@tanstack/react-router'
import { Edit } from 'lucide-react'
import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'
import citiesData from '@/data/cities.json'
import regionsData from '@/data/regions.json' // Added import
import { UserProfileForm } from '@/components/profile/UserProfileForm'

export const Route = createFileRoute('/_authenticated/profil')({
  component: ProfilePage,
})

function ProfilePage() {
  const { t } = useTranslation()
  const user = useAuthUser() as AppUserDetailDto

  const [isEditing, setIsEditing] = useState(false)

  const handleUpdateSuccess = () => {
    setIsEditing(false)
  }

  let cityName: string | null = null
  if (user.address?.cityId && citiesData[user.address.cityId - 1]) {
    cityName = citiesData[user.address.cityId - 1]
  }

  let regionName: string | null = null
  if (user.address?.regionId && regionsData[user.address.regionId - 1]) {
    regionName = regionsData[user.address.regionId - 1]
  }

  let displayLocation: string | null = null
  if (user.address?.location && typeof user.address.location === 'string') {
    const parts = user.address.location.split(',')
    if (parts.length === 2) {
      const lat = parseFloat(parts[0])
      const lng = parseFloat(parts[1])
      if (!isNaN(lat) && !isNaN(lng)) {
        displayLocation = `Lat: ${lat.toFixed(4)}, Lng: ${lng.toFixed(4)}`
      } else {
        displayLocation = user.address.location
      }
    } else {
      displayLocation = user.address.location
    }
  } else if (
    user.address?.location &&
    typeof user.address.location === 'object' &&
    'latitude' in user.address.location &&
    'longitude' in user.address.location
  ) {
    const loc = user.address.location as { latitude: number; longitude: number }
    displayLocation = `Lat: ${loc.latitude.toFixed(4)}, Lng: ${loc.longitude.toFixed(4)}`
  }

  const UserInfoItem: React.FC<{
    labelKey: string
    value?: string | number | null
  }> = ({ labelKey, value }) => (
    <div className="grid grid-cols-3 gap-4 py-2">
      <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">
        {t(labelKey)}
      </dt>
      <dd className="col-span-2 text-sm text-gray-900 dark:text-white">
        {value || '-'}
      </dd>
    </div>
  )

  return (
    <>
      <BreadcrumbSection
        titleKey="profile.page_title"
        subtitleKey="profile.page_subtitle"
        breadcrumbs={[{ labelKey: 'breadcrumb.profile' }]}
      />
      <div className="container mx-auto max-w-3xl py-10">
        <Card className="shadow-lg">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <div>
              <CardTitle className="text-2xl font-bold">
                {t('profile.card_title')}
              </CardTitle>
              <CardDescription>
                {isEditing
                  ? t('profile.edit_mode_description')
                  : t('profile.view_mode_description')}
              </CardDescription>
            </div>
            <Button
              variant="outline"
              size="icon"
              onClick={() => setIsEditing(!isEditing)}
            >
              <Edit className="h-5 w-5" />
              <span className="sr-only">
                {isEditing ? t('buttons.cancel') : t('buttons.edit')}
              </span>
            </Button>
          </CardHeader>
          <CardContent className="mt-6">
            {isEditing ? (
              <UserProfileForm
                currentUser={user}
                onUpdateSuccess={handleUpdateSuccess}
                onCancel={() => setIsEditing(false)}
              />
            ) : (
              <div className="space-y-6">
                <div>
                  <h3 className="mb-3 text-lg font-semibold text-gray-900 dark:text-white">
                    {t('profile.personal_details_title')}{' '}
                  </h3>
                  <dl className="divide-y divide-gray-200 dark:divide-gray-700">
                    <UserInfoItem labelKey="form.mail" value={user.email} />
                    <UserInfoItem
                      labelKey="form.last_name"
                      value={user.lastName}
                    />
                    <UserInfoItem
                      labelKey="form.first_name"
                      value={user.firstName}
                    />
                    <UserInfoItem labelKey="form.phone" value={user.phone} />
                    <UserInfoItem
                      labelKey="form.language"
                      value={user.language?.name} // ou user.language?.code
                    />
                  </dl>
                </div>

                <Separator />

                <div>
                  <h3 className="mb-3 text-lg font-semibold text-gray-900 dark:text-white">
                    {t('profile.role_specific_details_title')}{' '}
                  </h3>
                  <dl className="divide-y divide-gray-200 dark:divide-gray-700">
                    <UserInfoItem
                      labelKey="form.type"
                      value={t(`types.${user.type}`)}
                    />
                    {user.type === 'producer' && (
                      <>
                        <UserInfoItem
                          labelKey="form.agricultural_identifier"
                          value={user.agriculturalIdentifier}
                        />
                        <UserInfoItem
                          labelKey="form.cooperative"
                          value={user.cooperative?.name}
                        />
                      </>
                    )}
                    {user.type === 'carrier' && (
                      <>
                        <UserInfoItem
                          labelKey="form.price_per_km"
                          value={user.pricePerKm}
                        />
                        <UserInfoItem
                          labelKey="form.radius"
                          value={user.radius}
                        />
                      </>
                    )}
                  </dl>
                </div>
                {user.address && (
                  <>
                    <Separator />
                    <div>
                      <h3 className="mb-3 text-lg font-semibold text-gray-900 dark:text-white">
                        {t('form.address')}
                      </h3>
                      <dl className="divide-y divide-gray-200 dark:divide-gray-700">
                        <UserInfoItem
                          labelKey="form.street_quarter"
                          value={user.address.street}
                        />
                        <UserInfoItem labelKey="form.city" value={cityName} />
                        <UserInfoItem
                          labelKey="address.region_label"
                          value={regionName}
                        />
                        {user.address.location && (
                          <UserInfoItem
                            labelKey="form.location"
                            value={displayLocation}
                          />
                        )}
                      </dl>
                    </div>
                  </>
                )}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </>
  )
}
