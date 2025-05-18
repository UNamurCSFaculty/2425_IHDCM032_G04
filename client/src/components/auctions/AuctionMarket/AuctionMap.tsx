import type { AuctionDto } from '@/api/generated'
import { formatDate, wktToLatLon } from '@/lib/utils'
import { formatPrice } from '@/utils/formatter'
import L from 'leaflet'
import React from 'react'
import { MapContainer, Marker, Popup, TileLayer } from 'react-leaflet'
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
          return (
            <Marker
              key={a.id}
              position={coords as L.LatLngTuple}
              eventHandlers={{ click: () => onSelect(a) }}
            >
              <Popup>
                <div className="text-sm space-y-1">
                  <div className="font-medium">
                    {a.product.type === 'harvest' ? 'Récolte' : 'Transformé'} ·{' '}
                    {a.product.id}
                  </div>
                  <div>Prix&nbsp;: {formatPrice.format(a.price)}</div>
                  <div>Expire&nbsp;: {formatDate(a.expirationDate)}</div>
                </div>
              </Popup>
            </Marker>
          )
        })}
      </MarkerClusterGroup>
    </MapContainer>
  )
}

export default AuctionMap
