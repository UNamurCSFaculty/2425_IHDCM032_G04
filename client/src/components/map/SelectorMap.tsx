// src/components/leaflet/BeninPointSelectorMap.tsx
// ShadCN/UI imports
import {
  Command,
  CommandEmpty,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command'
import { useDebounce } from '@/hooks/useDebounce'
import type { GeoPointString } from '@/utils/geo-utils'
import L, { LatLng, type LatLngExpression } from 'leaflet'
import 'leaflet/dist/leaflet.css'
import React, { useCallback, useEffect, useState } from 'react'
import {
  MapContainer,
  Marker,
  Popup,
  TileLayer,
  useMap,
  useMapEvents,
} from 'react-leaflet'

// Default Leaflet marker icon
const iconMarker = new L.Icon({
  iconUrl: '/leaflet/marker-icon.png',
  iconRetinaUrl: '/leaflet/marker-icon-2x.png',
  iconSize: [25, 41],
})

export interface NominatimSearchResult {
  place_id: number
  lat: string
  lon: string
  display_name: string
  address: Record<string, string>
}

export interface NominatimAddress {
  display_name: string
  road?: string
  city?: string
  state?: string
  country?: string
}

export interface BeninPointSelectorMapProps {
  initialPosition?: string | null
  onPositionChange: (point: string, address?: NominatimAddress) => void
  defaultCenter?: LatLngExpression
  defaultZoom?: number
  mapHeight?: string
}

function parseGeoPoint(point?: string | null) {
  if (!point) return null
  const m = point.match(/POINT\(\s*([\d.-]+)\s+([\d.-]+)\s*\)/)
  if (!m) return null
  return { lng: parseFloat(m[1]), lat: parseFloat(m[2]) }
}

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
  defaultCenter = [9.3077, 2.3158],
  defaultZoom = 7,
  mapHeight = '400px',
}) => {
  const [selectedPosition, setSelectedPosition] = useState<LatLng | null>(
    () => {
      const p = parseGeoPoint(initialPosition)
      return p ? new LatLng(p.lat, p.lng) : null
    }
  )

  const [addressInfo, setAddressInfo] = useState<NominatimAddress | null>(null)
  const [loadingAddress, setLoadingAddress] = useState(false)

  // Search state
  const [search, setSearch] = useState('')
  const debounced = useDebounce(search, 500)
  const [suggestions, setSuggestions] = useState<NominatimSearchResult[]>([])
  const [loadingSearch, setLoadingSearch] = useState(false)

  const fetchAddress = useCallback(async (lat: number, lng: number) => {
    setLoadingAddress(true)
    try {
      const res = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}&accept-language=fr`
      )
      if (!res.ok) throw new Error(res.statusText)
      const json = await res.json()
      const addr = json.address as Record<string, string>
      const simplified: NominatimAddress = {
        display_name: json.display_name,
        road: addr.road,
        city: addr.city || addr.town || addr.village,
        state: addr.state || addr.county,
        country: addr.country,
      }
      setAddressInfo(simplified)
      return simplified
    } catch {
      return undefined
    } finally {
      setLoadingAddress(false)
    }
  }, [])

  // Handle map click
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

  // Fetch suggestions
  useEffect(() => {
    if (!debounced) {
      setSuggestions([])
      return
    }
    setLoadingSearch(true)
    fetch(
      `https://nominatim.openstreetmap.org/search?format=jsonv2&addressdetails=1&limit=5&q=${encodeURIComponent(
        debounced
      )}`
    )
      .then(r => r.json())
      .then((list: NominatimSearchResult[]) => setSuggestions(list))
      .catch(() => setSuggestions([]))
      .finally(() => setLoadingSearch(false))
  }, [debounced])

  const selectSuggestion = (item: NominatimSearchResult) => {
    const lat = parseFloat(item.lat),
      lon = parseFloat(item.lon)
    const pt = new LatLng(lat, lon)
    setSelectedPosition(pt)
    const addr = item.address
    const simplified: NominatimAddress = {
      display_name: item.display_name,
      road: addr.road,
      city: addr.city || addr.town || addr.village,
      state: addr.state || addr.county,
      country: addr.country,
    }
    setAddressInfo(simplified)
    onPositionChange(`POINT(${lon} ${lat})`, simplified)
    setSuggestions([])
    setSearch(item.display_name)
  }

  // Sync initialPosition
  useEffect(() => {
    const p = parseGeoPoint(initialPosition)
    if (p) {
      const pt = new LatLng(p.lat, p.lng)
      setSelectedPosition(pt)
      fetchAddress(p.lat, p.lng)
    }
  }, [initialPosition, fetchAddress])

  return (
    <div>
      {/* ShadCN Command for search */}
      <div className="mb-4">
        <Command>
          <CommandInput
            placeholder="Rechercher une adresse…"
            value={search}
            onValueChange={setSearch}
          />
          <CommandList>
            {loadingSearch && <CommandEmpty>Chargement…</CommandEmpty>}
            {!loadingSearch && suggestions.length === 0 && debounced && (
              <CommandEmpty>Aucune suggestion</CommandEmpty>
            )}
            {suggestions.map(s => (
              <CommandItem
                key={s.place_id}
                onSelect={() => selectSuggestion(s)}
              >
                {s.display_name}
              </CommandItem>
            ))}
          </CommandList>
        </Command>
      </div>

      {/* Leaflet Map */}
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
          attribution="&copy; OpenStreetMap contributors"
        />
        {selectedPosition && (
          <Marker position={selectedPosition} icon={iconMarker}>
            <Popup>
              {selectedPosition.lat.toFixed(5)},{' '}
              {selectedPosition.lng.toFixed(5)}
              {loadingAddress && <div>Chargement de l'adresse…</div>}
              {addressInfo && <div>{addressInfo.display_name}</div>}
            </Popup>
          </Marker>
        )}
        <MapEvents />
      </MapContainer>
    </div>
  )
}
