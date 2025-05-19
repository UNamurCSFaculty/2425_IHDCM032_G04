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

const AuctionMap: React.FC<{
  auctions: AuctionDto[]
  onSelect: (a: AuctionDto) => void
}> = ({ auctions, onSelect }) => {
  const center: [number, number] = [9.3, 2.3] // centre Bénin
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
          const typeLabel =
            a.product.type === 'harvest' ? 'Récolte' : 'Transformé'
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
                    <h3 className="font-semibold text-base mb-1">
                      {a.product.type === 'harvest' ? 'Récolte' : 'Transformé'}{' '}
                      · lot #{a.product.id}
                    </h3>
                    <dl className="space-y-0.5">
                      <div className="flex justify-between">
                        <dt className="font-semibold">Vendeur</dt>
                        <dd className="ml-2">
                          {' '}
                          {a.trader.firstName} {a.trader.lastName}
                        </dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">Type</dt>
                        <dd className="ml-2">{typeLabel}</dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">Qualité</dt>
                        <dd className="ml-2">
                          {' '}
                          {a.product.qualityControl?.quality.name ?? 'N/A'}
                        </dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">Quantité</dt>
                        <dd className="ml-2">
                          {formatWeight(a.product.weightKg)}
                        </dd>
                      </div>
                      <div className="flex justify-between">
                        <dt className="font-semibold">Prix demandé</dt>
                        <dd className="ml-2">{formatPrice.format(a.price)}</dd>
                      </div>
                      {bestBid !== null ? (
                        <div className="flex justify-between">
                          <dt className="font-semibold">Meilleure offre</dt>
                          <dd className="ml-2">
                            {formatPrice.format(bestBid)}
                          </dd>
                        </div>
                      ) : (
                        <div className="italic text-muted-foreground text-center">
                          Pas d’offres
                        </div>
                      )}
                      <div className="flex justify-between">
                        <dt className="font-semibold">Temps restant</dt>
                        <dd className="ml-2">
                          <CountdownTimer endDate={expires.toDate()} />
                        </dd>
                      </div>
                    </dl>
                    {/* Invite à cliquer */}
                    <div className="mt-1 text-center text-xs font-medium text-blue-600">
                      Cliquez pour voir les détails →
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
