import type { AuctionDto } from '@/api/generated'
import { CountdownTimer } from '@/components/CountDownTimer'
import { CardContent } from '@/components/ui/card'
import { wktToLatLon } from '@/lib/utils'
import { formatPrice, formatWeight } from '@/utils/formatter'
import dayjs from 'dayjs'
import L from 'leaflet'
import React from 'react'
import { MapContainer, Marker, TileLayer, Tooltip } from 'react-leaflet'
import MarkerClusterGroup from 'react-leaflet-markercluster'
import { useTranslation } from 'react-i18next'

const AuctionMap: React.FC<{
  auctions: AuctionDto[]
  onSelect: (a: AuctionDto) => void
}> = ({ auctions, onSelect }) => {
  const center: [number, number] = [9.3, 2.3] // centre Bénin
  const { t } = useTranslation()
  return (
    <MapContainer center={center} zoom={7} className="h-[70vh] rounded-md">
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution="© OpenStreetMap"
      />
      <MarkerClusterGroup>
        {auctions.map(a => {
          const coords = wktToLatLon(a.product.store.address.location)
          if (!coords) return null
          // Calcul de la meilleure offre
          const bids = a.bids ?? []
          const bestBid =
            bids.length > 0 ? Math.max(...bids.map(b => b.amount)) : null

          // Libellés
          const typeLabel = t('database.' + a.product.type)
          const expires = dayjs(a.expirationDate)
          return (
            <Marker
              key={a.id}
              position={coords as L.LatLngTuple}
              eventHandlers={{ click: () => onSelect(a) }}
            >
              <Tooltip
                direction="top"
                offset={[-15.5, -20]}
                opacity={1}
                // interactive si vous voulez autoriser le survol du tooltip
                interactive={false}
              >
                <div className="pointer-events-auto">
                  <CardContent className="p-1 text-xs">
                    <h3 className="mb-1 text-base font-semibold">
                      {typeLabel} ·{' '}
                      {t('auction.lot_label_short', { id: a.product.id })}
                    </h3>
                    <dl className="space-y-0.5">
                      <div className="flex justify-between">
                        <dt className="font-semibold">
                          {t('auction.seller_label')}
                        </dt>
                        <dd className="ml-2">
                          {' '}
                          {a.trader.firstName} {a.trader.lastName}
                        </dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">
                          {t('product.type_label')}
                        </dt>
                        <dd className="ml-2">{typeLabel}</dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">
                          {t('product.quality_label')}
                        </dt>
                        <dd className="ml-2">
                          {' '}
                          {a.product.qualityControl?.quality.name ?? 'N/A'}
                        </dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">
                          {t('product.quantity_label')}
                        </dt>
                        <dd className="ml-2">
                          {formatWeight(a.product.weightKg)}
                        </dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">
                          {t('auction.asking_price')}
                        </dt>
                        <dd className="ml-2">{formatPrice.format(a.price)}</dd>
                      </div>
                      {bestBid !== null ? (
                        <div className="flex justify-between">
                          <dt className="font-semibold">
                            {t('auction.best_bid')}
                          </dt>
                          <dd className="ml-2">
                            {formatPrice.format(bestBid)}
                          </dd>
                        </div>
                      ) : (
                        <div className="text-muted-foreground text-center italic">
                          {t('auction.no_bids_message')}
                        </div>
                      )}
                      <div className="flex justify-between">
                        <dt className="font-semibold">
                          {t('auction.time_remaining_label')}
                        </dt>
                        <dd className="ml-2">
                          <CountdownTimer endDate={expires.toDate()} />
                        </dd>
                      </div>
                    </dl>
                    <div className="mt-1 text-center text-xs font-medium text-blue-600">
                      {t('auction.click_to_see_details_prompt')}
                    </div>
                  </CardContent>
                </div>
              </Tooltip>
            </Marker>
          )
        })}
      </MarkerClusterGroup>
    </MapContainer>
  )
}

export default AuctionMap
