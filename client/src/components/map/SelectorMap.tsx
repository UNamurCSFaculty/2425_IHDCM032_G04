import {
  MapContainer,
  Marker,
  Popup,
  TileLayer,
  useMap,
  useMapEvents,
  Circle,
} from 'react-leaflet'
import L, { LatLng, type LatLngExpression } from 'leaflet'
import 'leaflet/dist/leaflet.css'
import React, { useCallback, useEffect, useRef, useState } from 'react'
import {
  Command,
  CommandEmpty,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command'
import { useDebounce } from '@/hooks/useDebounce'
import type { GeoPointString } from '@/utils/geo-utils'
import { wktToLatLon } from '@/lib/utils'

const iconMarker = new L.Icon({
  iconUrl: '/leaflet/marker-icon.png',
  iconRetinaUrl: '/leaflet/marker-icon-2x.png',
  iconSize: [25, 41],
  iconAnchor: [13, 39],
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
  quarter?: string
  suburb?: string
  city_district?: string
  city?: string
  town?: string
  village?: string
  county?: string
  state?: string // région (peut varier)
  country?: string
  country_code?: string
}

export interface SelectorMapProps {
  initialPosition?: string | null
  onPositionChange?: (point: GeoPointString, address?: NominatimAddress) => void
  defaultCenter?: LatLngExpression
  defaultZoom?: number
  mapHeight?: string
  showSearch?: boolean
  /** Rayon en mètres (optionnel) */
  radius?: number
  isDisplayOnly?: boolean
}

const ChangeView: React.FC<{ center: LatLngExpression; minZoom: number }> = ({
  center,
  minZoom,
}) => {
  const map = useMap()
  useEffect(() => {
    if (center) {
      const zoom = Math.max(map.getZoom(), minZoom)
      map.setView(center, zoom)
    }
  }, [center, minZoom, map])
  return null
}

export const SelectorMap: React.FC<SelectorMapProps> = ({
  initialPosition,
  onPositionChange,
  defaultCenter = [9.3077, 2.3158],
  defaultZoom = 7,
  mapHeight = '400px',
  showSearch = true,
  radius = 0,
  isDisplayOnly = false,
}) => {
  const [selectedPosition, setSelectedPosition] = useState<LatLng | null>(null)

  const [addressInfo, setAddressInfo] = useState<NominatimAddress | null>(null)
  const [loadingAddress, setLoadingAddress] = useState(false)

  const [search, setSearch] = useState('')
  const debounced = useDebounce(search, 500)
  const [suggestions, setSuggestions] = useState<NominatimSearchResult[]>([])
  const [loadingSearch, setLoadingSearch] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)
  const [isFocused, setIsFocused] = useState(false)

  const centerIcon = new L.DivIcon({
    className: 'bg-green-600 rounded-full w-3 h-3 opacity-75',
    iconSize: [3, 3],
    iconAnchor: [1.5, 1.5],
  })

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
        country_code: addr.country_code,
      }
      setAddressInfo(simplified)
      return simplified
    } catch {
      setAddressInfo(null)
      return undefined
    } finally {
      setLoadingAddress(false)
    }
  }, [])

  // sync initialPosition
  useEffect(() => {
    if (!initialPosition) return
    const coords = wktToLatLon(initialPosition)
    if (coords) {
      const pt = new LatLng(coords[0], coords[1])
      setSelectedPosition(pt)
      fetchAddress(pt.lat, pt.lng)
    }
  }, [initialPosition, fetchAddress])

  const MapEvents = () => {
    useMapEvents({
      click: async e => {
        if (isDisplayOnly || !onPositionChange) return
        const { lat, lng } = e.latlng
        setSelectedPosition(e.latlng)
        const point = `POINT(${lng} ${lat})` as GeoPointString
        const addr = await fetchAddress(lat, lng)
        if (onPositionChange) {
          onPositionChange(point, addr)
        }
      },
    })
    return null
  }

  useEffect(() => {
    if (isDisplayOnly || !showSearch || !debounced) {
      setSuggestions([])
      return
    }
    setLoadingSearch(true)
    fetch(
      `https://nominatim.openstreetmap.org/search?format=jsonv2&addressdetails=1&limit=5&countrycodes=bj&q=${encodeURIComponent(
        debounced
      )}`
    )
      .then(r => r.json())
      .then((list: NominatimSearchResult[]) => setSuggestions(list))
      .catch(() => setSuggestions([]))
      .finally(() => setLoadingSearch(false))
  }, [debounced, isDisplayOnly, showSearch])

  const selectSuggestion = (item: NominatimSearchResult) => {
    if (isDisplayOnly || !onPositionChange) return
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
    inputRef.current?.blur()
    setIsFocused(false)
  }

  return (
    <div className="relative">
      {showSearch && !isDisplayOnly && (
        <div className="absolute top-2 right-2 left-11 z-30">
          <Command>
            <CommandInput
              ref={inputRef}
              placeholder="Rechercher une adresse…"
              value={search}
              onValueChange={setSearch}
              onFocus={() => setIsFocused(true)}
              onBlur={() => setIsFocused(false)}
            />
            {isFocused && (
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
            )}
          </Command>
        </div>
      )}

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
          <>
            <Marker position={selectedPosition} icon={centerIcon} />
            <Marker position={selectedPosition} icon={iconMarker}>
              <Popup>
                {selectedPosition.lat.toFixed(5)},{' '}
                {selectedPosition.lng.toFixed(5)}
                {loadingAddress && !addressInfo && (
                  <div>Chargement de l'adresse…</div>
                )}
                {addressInfo && <div>{addressInfo.display_name}</div>}
                {!loadingAddress && !addressInfo && initialPosition && (
                  <div>Adresse non trouvée</div>
                )}
              </Popup>
            </Marker>

            {typeof radius === 'number' && radius > 0 && (
              <Circle
                center={selectedPosition}
                radius={radius}
                pathOptions={{
                  color: '#00aa00',
                  fillColor: '#00aa00',
                  fillOpacity: 0.2,
                  weight: 1,
                }}
              />
            )}
          </>
        )}
        {!isDisplayOnly && <MapEvents />}
      </MapContainer>
    </div>
  )
}
