import { type GeoPointString, parseGeoPoint } from '@/utils/geo-utils'
import L, { LatLng, type LatLngExpression } from 'leaflet'
// Exemple d'icône Lucide
import React, { useCallback, useEffect, useState } from 'react'
import {
  MapContainer,
  Marker,
  Popup,
  TileLayer,
  useMap,
  useMapEvents,
} from 'react-leaflet'

const iconMarker = new L.Icon({
  iconUrl: '/public/leaflet/marker-icon.png',
  iconRetinaUrl: '/public/leaflet/marker-icon-2x.png',
  iconSize: new L.Point(25, 41),
  shadowSize: new L.Point(41, 41),
  className: 'leaflet-div-icon',
})

interface BeninPointSelectorMapProps {
  initialPosition?: GeoPointString | null // ex: "POINT(2.3158 9.3077)"
  onPositionChange: (point: GeoPointString, address?: NominatimAddress) => void
  defaultCenter?: LatLngExpression
  defaultZoom?: number
  mapHeight?: string
}

interface NominatimAddress {
  display_name: string
  road?: string
  quarter?: string // quartier
  suburb?: string // banlieue / arrondissement
  city_district?: string // arrondissement de la ville
  city?: string
  town?: string
  village?: string
  county?: string // département
  state?: string // région (peut varier)
  country?: string
  country_code?: string
}

// Helper pour recentrer la carte si la position change
const ChangeView: React.FC<{ center: LatLngExpression; minZoom: number }> = ({
  center,
  minZoom,
}) => {
  const map = useMap()
  useEffect(() => {
    const zoom = Math.max(map.getZoom(), minZoom)
    map.setView(center, zoom)
  }, [center, minZoom, map])
  return null
}

export const SelectorMap: React.FC<BeninPointSelectorMapProps> = ({
  initialPosition,
  onPositionChange,
  defaultCenter = [9.3077, 2.3158], // Centre approximatif du Bénin
  defaultZoom = 7,
  mapHeight = '400px',
}) => {
  const [selectedPosition, setSelectedPosition] = useState<LatLng | null>(
    () => {
      const parsed = parseGeoPoint(initialPosition)
      return parsed ? new LatLng(parsed.lat, parsed.lng) : null
    }
  )
  const [addressInfo, setAddressInfo] = useState<NominatimAddress | null>(null)
  const [loadingAddress, setLoadingAddress] = useState(false)

  const fetchAddress = useCallback(async (lat: number, lng: number) => {
    setLoadingAddress(true)
    setAddressInfo(null)
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}&accept-language=fr`
      )
      if (!response.ok) {
        throw new Error('Failed to fetch address')
      }
      const data = await response.json()
      console.log('Fetched address data:', data)
      const address = data.address as NominatimAddress
      // Simplification pour obtenir la ville ou le village
      const cityOrTown =
        address.city || address.town || address.village || address.city_district
      // Simplification pour obtenir la région/département
      const region = address.state || address.county

      const simplifiedAddress: NominatimAddress = {
        display_name: data.display_name,
        city: cityOrTown,
        state: region,
        country: address.country,
        road: address.road,
        // ... autres champs si besoin
      }
      setAddressInfo(simplifiedAddress)
      return simplifiedAddress
    } catch (error) {
      console.error('Error fetching address:', error)
      setAddressInfo({ display_name: "Impossible de récupérer l'adresse" })
      return undefined
    } finally {
      setLoadingAddress(false)
    }
  }, [])

  const MapEvents = () => {
    useMapEvents({
      async click(e) {
        const { lat, lng } = e.latlng
        setSelectedPosition(e.latlng)
        const geoPointString = `POINT(${lng} ${lat})` as GeoPointString
        const fetchedAddress = await fetchAddress(lat, lng)
        onPositionChange(geoPointString, fetchedAddress)

        const q = `[out:json][timeout:25];
        area["ISO3166-1"="BJ"]->.a;
        (
          node["place"~"city|town|village"](area.a);
        );
        out body;
        `
        const url = `https://overpass-api.de/api/interpreter?data=${encodeURIComponent(q)}`
        const cities = await fetch(url).then(r => r.json())
        console.log('Fetched cities:', cities)
      },
    })
    return null
  }

  // Si initialPosition change depuis l'extérieur
  useEffect(() => {
    const parsed = parseGeoPoint(initialPosition)
    const newPos = parsed ? new LatLng(parsed.lat, parsed.lng) : null
    setSelectedPosition(newPos)
    if (newPos) {
      fetchAddress(newPos.lat, newPos.lng)
    } else {
      setAddressInfo(null)
    }
  }, [initialPosition, fetchAddress])

  return (
    <div>
      <MapContainer
        center={selectedPosition || defaultCenter}
        zoom={defaultZoom}
        style={{ height: mapHeight, width: '100%' }}
      >
        <ChangeView
          center={selectedPosition || defaultCenter}
          minZoom={selectedPosition ? 12 : defaultZoom}
        />
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />
        {selectedPosition && (
          <Marker position={selectedPosition} icon={iconMarker}>
            <Popup>
              Position sélectionnée: <br />
              Lat: {selectedPosition.lat.toFixed(5)}, Lng:{' '}
              {selectedPosition.lng.toFixed(5)} <br />
              {loadingAddress && "Chargement de l'adresse..."}
              {addressInfo && `Adresse: ${addressInfo.display_name}`}
            </Popup>
          </Marker>
        )}
        <MapEvents />
      </MapContainer>
      {addressInfo && (
        <div
          style={{
            marginTop: '10px',
            padding: '10px',
            border: '1px solid #ccc',
            borderRadius: '4px',
          }}
        >
          <strong>Informations géographiques :</strong>
          <p>Adresse complète : {addressInfo.display_name}</p>
          {addressInfo.road && <p>Rue : {addressInfo.road}</p>}
          {addressInfo.city && <p>Ville/Village : {addressInfo.city}</p>}
          {addressInfo.state && <p>Région/Département : {addressInfo.state}</p>}
          {addressInfo.country && <p>Pays : {addressInfo.country}</p>}
        </div>
      )}
    </div>
  )
}
