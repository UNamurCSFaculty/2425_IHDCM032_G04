import VirtualizedSelect from './VirtualizedSelect'
import { Button } from './ui/button'
import { Calendar } from './ui/calendar'
import { Input } from './ui/input'
import { Label } from './ui/label'
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover'
import { RadioGroup, RadioGroupItem } from './ui/radio-group'
import { Slider } from './ui/slider'
import type { AuctionDto, ProductDto } from '@/api/generated'
import { listQualitiesOptions } from '@/api/generated/@tanstack/react-query.gen'
import cities from '@/data/cities.json'
import regions from '@/data/regions.json'
import { TradeStatus, productTypes } from '@/lib/utils'
import { formatDate, formatPrice } from '@/utils/formatter'
import { useSuspenseQuery } from '@tanstack/react-query'
import dayjs from 'dayjs'
import { ChevronDown, Search, X } from 'lucide-react'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

const cityOptions = cities.map((n, i) => ({
  id: i + 1,
  label: n,
}))

const regionOptions = regions.map((n, i) => ({
  id: i + 1,
  label: n,
}))

interface FiltersPanelProps {
  filterDataType: 'auction' | 'product'
  filterData: (AuctionDto | ProductDto)[]
  onFilteredDataChange: (filteredData: (AuctionDto | ProductDto)[]) => void

  // Optional filters
  filterByAuctionStatus?: boolean
}

const FiltersPanel: React.FC<FiltersPanelProps> = ({
  filterDataType,
  filterData,
  onFilteredDataChange,
  filterByAuctionStatus,
}) => {
  const [search, setSearch] = useState('')
  const [auctionStatus, setAuctionStatus] = useState<TradeStatus>(
    TradeStatus.OPEN
  )
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 5_000_000])
  const [selectedDate, setSelectedDate] = useState<Date | null>(null)
  const [qualityId, setQualityId] = useState<number | null>(null)
  const [productTypeId, setProductTypeId] = useState<number | null>(null)
  const [regionId, setRegionId] = useState<number | null>(null)
  const [cityId, setCityId] = useState<number | null>(null)

  // Reset dependent filters
  useEffect(() => setCityId(null), [regionId])
  useEffect(() => setQualityId(null), [productTypeId])

  // Apply filters
  const filterAuctions = (a: AuctionDto) => {
    if (
      search &&
      !`${a.product.type} ${a.product.store.name} ${a.id} ${a.trader.firstName} ${a.trader.lastName}`
        .toLowerCase()
        .includes(search.toLowerCase())
    )
      return false
    if (
      auctionStatus === TradeStatus.OPEN &&
      a.status.name !== TradeStatus.OPEN
    )
      return false
    if (
      auctionStatus !== TradeStatus.OPEN &&
      a.status.name === TradeStatus.OPEN
    )
      return false
    if (a.price < priceRange[0] || a.price > priceRange[1]) return false
    if (
      selectedDate &&
      dayjs(a.expirationDate).isAfter(dayjs(selectedDate).endOf('day'))
    )
      return false
    if (qualityId && a.product.qualityControl?.quality.id !== qualityId)
      return false
    if (productTypeId && a.product.type !== productTypes[productTypeId - 1])
      return false
    if (regionId && a.product.store.address.regionId !== regionId) return false
    if (cityId && a.product.store.address.cityId !== cityId) return false

    return true
  }

  const filterProducts = (p: ProductDto) => {
    if (
      search &&
      !`${p.type} ${p.store.name} ${p.id}`
        .toLowerCase()
        .includes(search.toLowerCase())
    )
      return false

    if (p.weightKg < priceRange[0] || p.weightKg > priceRange[1]) return false
    if (qualityId && p.qualityControl?.quality.id !== qualityId) return false
    if (productTypeId && p.type !== productTypes[productTypeId - 1])
      return false
    if (regionId && p.store.address.regionId !== regionId) return false
    if (cityId && p.store.address.cityId !== cityId) return false

    return true
  }

  useEffect(() => {
    const filtered = filterData.filter(item => {
      if (filterDataType === 'auction') {
        return filterAuctions(item as AuctionDto)
      } else if (filterDataType === 'product') {
        return filterProducts(item as ProductDto)
      } else {
        return true
      }
    })

    onFilteredDataChange(filtered)
  }, [
    search,
    priceRange,
    productTypeId,
    auctionStatus,
    selectedDate,
    qualityId,
    regionId,
    cityId,
    filterData,
    filterDataType,
    filterAuctions,
    filterProducts,
  ])

  const { t } = useTranslation()

  const productTypeOptions = productTypes.map((n, i) => ({
    id: i + 1,
    label: t('database.' + n),
  }))

  const { data: qualitiesData } = useSuspenseQuery(listQualitiesOptions())

  const qualityOptions = qualitiesData
    .filter(quality => {
      return (
        !productTypeId ||
        quality.qualityType.name.toLowerCase() ==
          productTypes[productTypeId - 1].toLowerCase()
      )
    })
    .map(q => ({
      id: q.id,
      label: q.name,
    }))

  const resetFilters = () => {
    setSearch('')
    setAuctionStatus(TradeStatus.OPEN)
    setPriceRange([0, 5_000_000])
    setSelectedDate(null)
    setQualityId(null)
    setProductTypeId(null)
    setRegionId(null)
    setCityId(null)
  }

  return (
    <div>
      <div className="flex justify-between items-center p-4 border-b">
        <h3 className="font-semibold text-lg">Filtres</h3>
        <Button variant="ghost" size="sm" onClick={resetFilters}>
          Reset
        </Button>
      </div>
      <div className="p-4">
        <div className="space-y-6 p-4 lg:p-0">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher…"
              className="pl-10"
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
            {search && (
              <Button
                variant="ghost"
                size="icon"
                className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7"
                onClick={() => setSearch('')}
              >
                <X className="size-4" />
              </Button>
            )}
          </div>

          {/* Status */}
          {filterByAuctionStatus && (
            <RadioGroup
              value={auctionStatus}
              defaultValue={TradeStatus.OPEN}
              onValueChange={v => setAuctionStatus(v as TradeStatus)}
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value={TradeStatus.OPEN} id="r1" />
                <Label htmlFor="r1">Enchères en cours</Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value={TradeStatus.EXPIRED} id="r2" />
                <Label htmlFor="r2">Enchères terminées</Label>
              </div>
            </RadioGroup>
          )}

          {/* Price */}
          <div className="space-y-2">
            <div className="flex justify-between text-sm font-medium">
              <span>Prix</span>
              <span>
                {formatPrice.format(priceRange[0])} –{' '}
                {formatPrice.format(priceRange[1])}
              </span>
            </div>
            <Slider
              value={priceRange}
              onValueChange={v => setPriceRange(v as [number, number])}
              min={0}
              max={5_000_000}
              step={500}
            />
          </div>
          {/* Product Type */}
          <VirtualizedSelect
            id="product-type-select"
            label="Marchandise"
            placeholder="Tous les types"
            options={productTypeOptions}
            value={productTypeId}
            onChange={v => setProductTypeId(v as number)}
          />
          {/* Quality */}
          <VirtualizedSelect
            id="quality-select"
            label="Qualité"
            placeholder="Toutes les qualités"
            options={qualityOptions}
            value={qualityId}
            onChange={v => setQualityId(v as number)}
          />
          {/* Region / City */}
          <VirtualizedSelect
            id="region-select"
            label="Région"
            placeholder="Toutes les régions"
            options={regionOptions}
            value={regionId}
            onChange={v => setRegionId(v as number)}
          />
          <VirtualizedSelect
            id="city-select"
            label="Ville"
            placeholder="Toutes les villes"
            options={cityOptions}
            value={cityId}
            onChange={v => setCityId(v as number)}
          />
          {/* Date picker */}
          <div className="space-y-2">
            <label
              htmlFor="expiration-date-picker"
              className="text-sm font-medium"
            >
              Expire avant
            </label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  id="expiration-date-picker"
                  variant="outline"
                  className="w-full justify-between"
                >
                  {selectedDate
                    ? formatDate(selectedDate.toISOString())
                    : 'Choisir…'}
                  <ChevronDown className="size-4 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-auto p-0" align="start">
                <Calendar
                  mode="single"
                  selected={selectedDate ?? undefined}
                  onSelect={d => setSelectedDate(d ?? null)}
                  disabled={d => d < dayjs().startOf('day').toDate()}
                />
              </PopoverContent>
            </Popover>
          </div>
        </div>
      </div>
    </div>
  )
}

export default FiltersPanel
