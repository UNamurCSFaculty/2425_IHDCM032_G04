import VirtualizedSelect, { type Option } from './VirtualizedSelect'
import { Button } from './ui/button'
import { Calendar } from './ui/calendar'
import { Input } from './ui/input'
import { Label } from './ui/label'
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover'
import { RadioGroup, RadioGroupItem } from './ui/radio-group'
import { Slider } from './ui/slider'
import {
  type AuctionDto,
  type ContractOfferDto,
  type ProductDto,
  ProductType,
  type QualityDto,
} from '@/api/generated'
import { listQualitiesOptions } from '@/api/generated/@tanstack/react-query.gen'
import cities from '@/data/cities.json'
import regions from '@/data/regions.json'
import { TradeStatus, getPricePerKg } from '@/lib/utils'
import { useAuthUser } from '@/store/userStore'
import { formatDate, formatPrice } from '@/utils/formatter'
import { useSuspenseQuery } from '@tanstack/react-query'
import dayjs from 'dayjs'
import { ChevronDown, Search, X } from 'lucide-react'
import { useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

const productTypes: ProductType[] = [
  ProductType.HARVEST,
  ProductType.TRANSFORMED,
]

const cityOptions = cities.map((n, i) => ({
  id: i + 1,
  label: n,
}))

const regionOptions = regions.map((n, i) => ({
  id: i + 1,
  label: n,
}))

interface FiltersPanelProps<
  T extends AuctionDto | ProductDto | ContractOfferDto,
> {
  filterDataType: T extends AuctionDto
    ? 'auction'
    : T extends ProductDto
      ? 'product'
      : 'contract'
  filterData: T[]
  onFilteredDataChange: (filteredData: T[], auctionStatus: TradeStatus) => void
  filterByAuctionStatus?: boolean
  filterByPrice?: boolean
}

const FiltersPanel = <T extends AuctionDto | ProductDto | ContractOfferDto>({
  filterDataType,
  filterData,
  onFilteredDataChange,
  filterByAuctionStatus,
  filterByPrice,
}: FiltersPanelProps<T>) => {
  const [search, setSearch] = useState('')
  const [auctionStatus, setAuctionStatus] = useState<TradeStatus>(
    TradeStatus.OPEN
  )
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 20_000])
  const [selectedDate, setSelectedDate] = useState<Date | null>(null)
  const [qualityId, setQualityId] = useState<number | null>(null)
  const [productTypeId, setProductTypeId] = useState<number | null>(null)
  const [regionId, setRegionId] = useState<number | null>(null)
  const [cityId, setCityId] = useState<number | null>(null)
  const [userType, setUserType] = useState<number | null>(1)

  const user = useAuthUser()

  // Reset dependent filters
  useEffect(() => setCityId(null), [regionId])
  useEffect(() => setQualityId(null), [productTypeId])

  // Apply filters
  const filterFunction = useCallback(
    (item: T): boolean => {
      if (filterDataType === 'auction') {
        const a = item as AuctionDto

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

        const pricePerKg = getPricePerKg(a.price, a.productQuantity)
        if (pricePerKg < priceRange[0] || pricePerKg > priceRange[1])
          return false

        if (
          selectedDate &&
          dayjs(a.expirationDate).isAfter(dayjs(selectedDate).endOf('day'))
        )
          return false

        if (qualityId && a.product.qualityControl?.quality.id !== qualityId)
          return false
        if (productTypeId && a.product.type !== productTypes[productTypeId - 1])
          return false
        if (regionId && a.product.store.address.regionId !== regionId)
          return false
        if (cityId && a.product.store.address.cityId !== cityId) return false

        return true
      } else if (filterDataType === 'product') {
        const p = item as ProductDto

        if (
          search &&
          !`${p.type} ${p.store.name} ${p.id}`
            .toLowerCase()
            .includes(search.toLowerCase())
        )
          return false

        if (
          selectedDate &&
          dayjs(p.deliveryDate).isAfter(dayjs(selectedDate).endOf('day'))
        )
          return false

        if (qualityId && p.qualityControl?.quality.id !== qualityId)
          return false
        if (productTypeId && p.type !== productTypes[productTypeId - 1])
          return false
        if (regionId && p.store.address.regionId !== regionId) return false
        if (cityId && p.store.address.cityId !== cityId) return false

        return true
      } else if (filterDataType === 'contract') {
        const c = item as ContractOfferDto

        const searchLower = search.toLowerCase()
        if (
          search &&
          !(
            c.id.toString().includes(searchLower) ||
            c.seller.firstName.toLowerCase().includes(searchLower) ||
            c.seller.lastName.toLowerCase().includes(searchLower) ||
            c.buyer.firstName.toLowerCase().includes(searchLower) ||
            c.buyer.lastName.toLowerCase().includes(searchLower) ||
            c.status.toLowerCase().includes(searchLower)
          )
        ) {
          return false
        }

        if (c.pricePerKg < priceRange[0] || c.pricePerKg > priceRange[1])
          return false

        if (
          selectedDate &&
          dayjs(c.endDate).isAfter(dayjs(selectedDate).endOf('day'))
        ) {
          return false
        }

        if (qualityId && c.quality.id !== qualityId) return false

        if (productTypeId && c.quality.qualityType.id !== productTypeId)
          return false

        if (userType === 2 && c.seller.id !== user.id) return false

        if (userType === 3 && c.buyer.id !== user.id) return false

        return true
      }
      return true // Should not be reached if filterDataType is correctly 'auction' or 'product'
    },
    [
      search,
      auctionStatus,
      priceRange,
      selectedDate,
      qualityId,
      productTypeId,
      regionId,
      cityId,
      filterDataType,
      userType,
      user.id,
    ]
  )

  useEffect(() => {
    const filtered = filterData.filter(filterFunction)
    onFilteredDataChange(filtered, auctionStatus)
  }, [filterData, filterFunction, onFilteredDataChange, auctionStatus])

  const { t } = useTranslation()

  const productTypeOptions = productTypes.map((n, i) => ({
    id: i + 1,
    label: t('database.' + n),
  }))

  const userTypeOptions: Option[] = [
    { id: 2, label: t('filters.user_type.seller') },
    { id: 3, label: t('filters.user_type.buyer') },
  ]

  const { data: qualitiesData } = useSuspenseQuery(listQualitiesOptions())

  const qualityOptions = qualitiesData
    .filter((quality: QualityDto) => {
      return (
        !productTypeId ||
        quality.qualityType.name.toLowerCase() ==
          productTypes[productTypeId - 1].toLowerCase()
      )
    })
    .map((q: QualityDto) => ({
      id: q.id,
      label: q.name,
    }))

  const resetFilters = () => {
    setSearch('')
    setAuctionStatus(TradeStatus.OPEN)
    setPriceRange([0, 20_000])
    setSelectedDate(null)
    setQualityId(null)
    setProductTypeId(null)
    setRegionId(null)
    setCityId(null)
  }

  return (
    <div>
      <div className="flex items-center justify-between border-b p-3">
        <h3 className="text-lg font-semibold">{t('filters.panel_title')}</h3>
        <Button
          variant="ghost"
          size="sm"
          onClick={resetFilters}
          className="px-0 text-xs"
        >
          {t('buttons.reset')}
        </Button>
      </div>
      <div className="p-4">
        <div className="space-y-6 p-3 lg:p-0">
          <div className="relative">
            <Search className="text-muted-foreground absolute top-1/2 left-3 size-4 -translate-y-1/2" />
            <Input
              placeholder={t('form.placeholder.search')}
              className="pl-10"
              value={search}
              onChange={e => setSearch(e.target.value)}
            />
            {search && (
              <Button
                variant="ghost"
                size="icon"
                className="absolute top-1/2 right-1 h-7 w-7 -translate-y-1/2"
                onClick={() => setSearch('')}
              >
                <X className="size-4" />
              </Button>
            )}
          </div>

          {/* Status */}
          {filterByAuctionStatus && filterDataType === 'auction' && (
            <RadioGroup
              value={auctionStatus}
              defaultValue={TradeStatus.OPEN}
              onValueChange={v => setAuctionStatus(v as TradeStatus)}
            >
              <div className="flex items-center space-x-2">
                <RadioGroupItem value={TradeStatus.OPEN} id="r1" />
                <Label htmlFor="r1">
                  {t('filters.auctions_in_progress_label')}
                </Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem value={TradeStatus.EXPIRED} id="r2" />
                <Label htmlFor="r2">{t('filters.auctions_ended_label')}</Label>
              </div>
            </RadioGroup>
          )}

          {filterDataType === 'contract' && (
            <VirtualizedSelect
              id="user-type-select"
              label={t('filters.user_type.label')}
              placeholder={t('filters.user_type.undefined')}
              placeholderSelectable={true}
              options={userTypeOptions}
              value={userType}
              onChange={v => setUserType(v)}
            />
          )}

          {filterByPrice && (
            <div className="space-y-2">
              <div className="flex justify-between text-sm font-medium">
                <span>{t('product.price_label')}/kg</span>
                <span>
                  {formatPrice.format(priceRange[0])} –{' '}
                  {formatPrice.format(priceRange[1])}
                </span>
              </div>
              <Slider
                value={priceRange}
                onValueChange={v => setPriceRange(v as [number, number])}
                min={0}
                max={20_000}
                step={500}
              />
            </div>
          )}

          {/* Product Type */}
          <VirtualizedSelect
            id="product-type-select"
            label={t('product.merchandise_label')}
            placeholder={t('filters.all_types_placeholder')}
            placeholderSelectable={true}
            options={productTypeOptions}
            value={productTypeId}
            onChange={v => setProductTypeId(v as number)}
          />
          {/* Quality */}
          <VirtualizedSelect
            id="quality-select"
            label={t('product.quality_label')}
            placeholder={t('filters.all_qualities_placeholder')}
            placeholderSelectable={true}
            options={qualityOptions}
            value={qualityId}
            onChange={v => setQualityId(v as number)}
          />
          {/* Region / City */}
          {filterDataType !== 'contract' && (
            <VirtualizedSelect
              id="region-select"
              label={t('address.region_label')}
              placeholder={t('filters.all_regions_placeholder')}
              placeholderSelectable={true}
              options={regionOptions}
              value={regionId}
              onChange={v => setRegionId(v as number)}
            />
          )}
          {filterDataType !== 'contract' && (
            <VirtualizedSelect
              id="city-select"
              label={t('form.city')}
              placeholder={t('filters.all_cities_placeholder')}
              placeholderSelectable={true}
              options={cityOptions}
              value={cityId}
              onChange={v => setCityId(v as number)}
            />
          )}
          {/* Date picker - only for auctions */}
          {filterDataType === 'auction' ||
            (filterDataType === 'contract' && (
              <div className="space-y-2">
                <label
                  htmlFor="expiration-date-picker"
                  className="text-sm font-medium"
                >
                  {t('filters.expires_before_label')}
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
                        : t('filters.choose_date_placeholder')}
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
            ))}
        </div>
      </div>
    </div>
  )
}

export default FiltersPanel
