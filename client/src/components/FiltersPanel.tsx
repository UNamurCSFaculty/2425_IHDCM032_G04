import VirtualizedSelect from './VirtualizedSelect'
import { Button } from './ui/button'
import { Calendar } from './ui/calendar'
import { Input } from './ui/input'
import { Label } from './ui/label'
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover'
import { RadioGroup, RadioGroupItem } from './ui/radio-group'
import { Slider } from './ui/slider'
import type { QualityDto } from '@/api/generated'
import cities from '@/data/cities.json'
import regions from '@/data/regions.json'
import { TradeStatus, productTypes } from '@/lib/utils'
import { formatDate, formatPrice } from '@/utils/formatter'
import dayjs from 'dayjs'
import { ChevronDown, Search, X } from 'lucide-react'
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
  search: string
  onSearch: (value: string) => void
  auctionStatus: TradeStatus
  onAuctionStatusChange: (status: TradeStatus) => void
  showAuctionStatusFilter?: boolean
  priceRange: [number, number]
  onPriceChange: (range: [number, number]) => void
  selectedDate: Date | null
  onDateSelect: (date: Date | null) => void
  qualities: QualityDto[]
  qualityId: number | null
  onQualityChange: (q: number | null) => void
  productTypeId: number | null
  onTypeChange: (t: number | null) => void
  regionId: number | null
  onRegionChange: (id: number | null) => void
  cityId: number | null
  onCityChange: (id: number | null) => void
  resetFilters: () => void
}

const FiltersPanel: React.FC<FiltersPanelProps> = ({
  search,
  onSearch,
  auctionStatus,
  onAuctionStatusChange,
  showAuctionStatusFilter,
  priceRange,
  onPriceChange,
  selectedDate,
  onDateSelect,
  qualities,
  qualityId,
  onQualityChange,
  productTypeId,
  onTypeChange,
  regionId,
  onRegionChange,
  cityId,
  onCityChange,
  resetFilters,
}) => {
  const { t } = useTranslation()

  const productTypeOptions = productTypes.map((n, i) => ({
    id: i + 1,
    label: t('database.' + n),
  }))

  const qualityOptions = qualities
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
              onChange={e => onSearch(e.target.value)}
            />
            {search && (
              <Button
                variant="ghost"
                size="icon"
                className="absolute right-1 top-1/2 -translate-y-1/2 h-7 w-7"
                onClick={() => onSearch('')}
              >
                <X className="size-4" />
              </Button>
            )}
          </div>

          {/* Status */}
          {showAuctionStatusFilter && (
            <RadioGroup
              value={auctionStatus}
              defaultValue={TradeStatus.OPEN}
              onValueChange={onAuctionStatusChange}
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
              onValueChange={v => onPriceChange(v as [number, number])}
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
            onChange={onTypeChange}
          />
          {/* Quality */}
          <VirtualizedSelect
            id="quality-select"
            label="Qualité"
            placeholder="Toutes les qualités"
            options={qualityOptions}
            value={qualityId}
            onChange={onQualityChange}
          />
          {/* Region / City */}
          <VirtualizedSelect
            id="region-select"
            label="Région"
            placeholder="Toutes les régions"
            options={regionOptions}
            value={regionId}
            onChange={onRegionChange}
          />
          <VirtualizedSelect
            id="city-select"
            label="Ville"
            placeholder="Toutes les villes"
            options={cityOptions}
            value={cityId}
            onChange={onCityChange}
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
                  onSelect={d => onDateSelect(d ?? null)}
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
