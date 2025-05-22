import { listContractOffersOptions } from '@/api/generated/@tanstack/react-query.gen'
import PaginationControls from '@/components/PaginationControls'
// icônes
import { Button } from '@/components/ui/button'
import { Calendar } from '@/components/ui/calendar'
import { Input } from '@/components/ui/input'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Slider } from '@/components/ui/slider'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useAuthUser } from '@/store/userStore'
import { useSuspenseQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'
import { ChevronDown, Search, X } from 'lucide-react'
import React, { useMemo, useState } from 'react'

// Types pour filtres
type QualityOption = string
type ProductTypeOption = string

const userTypeOptions: string[] = ['All', 'Vendeur', 'Acheteur']

const formatPrice = new Intl.NumberFormat('fr-FR', {
  style: 'currency',
  currency: 'XOF',
  minimumFractionDigits: 0,
})

const formatDate = (iso: string) => new Date(iso).toLocaleDateString('fr-FR')

const listContractsQueryOptions = (userId: number) => ({
  ...listContractOffersOptions({
    query: { traderId: userId },
  }),
  staleTime: 10_000,
})

export const Route = createFileRoute('/_authenticated/contrats/mes-contrats')({
  component: RouteComponent,
  loader: async ({ context: { queryClient, user } }) => {
    return queryClient.ensureQueryData(listContractsQueryOptions(user!.id))
  },
})

interface FiltersPanelProps {
  search: string
  onSearch: (value: string) => void
  priceRange: [number, number]
  onPriceChange: (range: [number, number]) => void
  selectedDate: Date | null
  onDateSelect: (date: Date | null) => void
  quality: QualityOption
  onQualityChange: (q: QualityOption) => void
  productType: ProductTypeOption
  onTypeChange: (t: ProductTypeOption) => void
  resetFilters: () => void
  productOptions: string[]
  qualityOptions: string[]
  selectedUserType: string
  onUserTypeChange: (t: string) => void
}

const FiltersPanel: React.FC<FiltersPanelProps> = ({
  search,
  onSearch,
  priceRange,
  onPriceChange,
  selectedDate,
  onDateSelect,
  quality,
  onQualityChange,
  productType,
  onTypeChange,
  resetFilters,
  productOptions,
  qualityOptions,
  selectedUserType,
  onUserTypeChange,
}) => (
  <div className="p-4 border rounded mb-4 bg-white shadow-sm">
    <div className="flex justify-between items-center mb-4">
      <h3 className="font-semibold text-lg">Filtres</h3>
      <Button variant="ghost" size="sm" onClick={resetFilters}>
        Reset
      </Button>
    </div>

    {/* Recherche */}
    <div className="relative mb-4">
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

    {/* user Type */}
    <div>
      <label
        htmlFor="product-type-select"
        className="text-sm font-medium mb-1 block"
      >
        En tant que
      </label>
      <Select value={selectedUserType} onValueChange={v => onUserTypeChange(v)}>
        <SelectTrigger id="product-type-select">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {userTypeOptions.map(t => (
            <SelectItem key={t} value={t}>
              {t === 'All' ? 'Tous' : t}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>

    {/* Prix */}
    <div className="mb-6">
      <div className="flex justify-between text-sm font-medium mb-1">
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

    {/* Qualité */}
    <div className="mb-4">
      <label
        htmlFor="quality-select"
        className="text-sm font-medium mb-1 block"
      >
        Qualité
      </label>
      <Select
        value={quality}
        onValueChange={v => onQualityChange(v as QualityOption)}
      >
        <SelectTrigger id="quality-select">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {qualityOptions.map(q => (
            <SelectItem key={q} value={q}>
              {q === 'All' ? 'Toutes' : q}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>

    {/* Type produit */}
    <div>
      <label
        htmlFor="product-type-select"
        className="text-sm font-medium mb-1 block"
      >
        Type
      </label>
      <Select
        value={productType}
        onValueChange={v => onTypeChange(v as ProductTypeOption)}
      >
        <SelectTrigger id="product-type-select">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {productOptions.map(t => (
            <SelectItem key={t} value={t}>
              {t === 'All' ? 'Tous' : t}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>

    {/* Date picker */}
    <div className="space-y-2">
      <label htmlFor="expiration-date-picker" className="text-sm font-medium">
        Fini le
      </label>
      <Popover>
        <PopoverTrigger asChild>
          <Button
            id="expiration-date-picker"
            variant="outline"
            className="w-full justify-between"
          >
            {selectedDate ? formatDate(selectedDate.toISOString()) : 'Choisir…'}
            <ChevronDown className="size-4 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-auto p-0" align="start">
          <Calendar
            mode="single"
            selected={selectedDate ?? undefined}
            onSelect={d => onDateSelect(d ?? null)}
          />
        </PopoverContent>
      </Popover>
    </div>
  </div>
)

export function RouteComponent() {
  const user = useAuthUser()
  const { data: contracts } = useSuspenseQuery(
    listContractsQueryOptions(user!.id)
  )

  const productOptions = [
    'All',
    ...new Set(contracts.map(c => c.quality.qualityType.name)),
  ]
  const qualityOptions = ['All', ...new Set(contracts.map(c => c.quality.name))]

  // États filtres
  const [search, setSearch] = React.useState('')
  const [priceRange, setPriceRange] = React.useState<[number, number]>([
    0, 5_000_000,
  ])
  const [selectedDate, setSelectedDate] = React.useState<Date | null>(null)
  const [quality, setQuality] = React.useState<QualityOption>('All')
  const [productType, setProductType] = React.useState<ProductTypeOption>('All')
  const [userType, setUserType] = React.useState<QualityOption>('All')

  // Filtrage manuel des contrats
  const filteredData = React.useMemo(() => {
    const searchLower = search.toLowerCase()

    return contracts.filter(contract => {
      // Recherche sur vendeur, acheteur, statut
      if (
        search &&
        !(
          contract.seller.firstName.toLowerCase().includes(searchLower) ||
          contract.seller.lastName.toLowerCase().includes(searchLower) ||
          contract.buyer.firstName.toLowerCase().includes(searchLower) ||
          contract.buyer.lastName.toLowerCase().includes(searchLower) ||
          contract.status.toLowerCase().includes(searchLower)
        )
      ) {
        return false
      }

      // Filtre prix
      if (
        contract.pricePerKg < priceRange[0] ||
        contract.pricePerKg > priceRange[1]
      ) {
        return false
      }

      // Filtre date (fin contrat)
      if (selectedDate) {
        const contractDate = new Date(contract.endDate)
        const selected = new Date(selectedDate)

        // On compare les années, mois, et jours
        if (
          contractDate.getFullYear() !== selected.getFullYear() ||
          contractDate.getMonth() !== selected.getMonth() ||
          contractDate.getDay() !== selected.getDay()
        ) {
          return false
        }
      }

      // Filtre qualité
      if (quality !== 'All' && contract.quality.name !== quality) {
        return false
      }

      if (
        (userType !== 'All' &&
          userType === 'Vendeur' &&
          contract.seller.id != user.id) ||
        (userType === 'Acheteur' && contract.buyer.id != user.id)
      ) {
        return false
      }

      // Filtre type produit
      if (
        productType !== 'All' &&
        contract.quality.qualityType.name !== productType
      ) {
        return false
      }

      return true
    })
  }, [
    contracts,
    search,
    priceRange,
    selectedDate,
    quality,
    productType,
    userType,
  ])

  const resetFilters = () => {
    setSearch('')
    setPriceRange([0, 5_000_000])
    setSelectedDate(null)
    setQuality('All')
    setProductType('All')
  }

  //pagination
  const [currentPage, setCurrentPage] = useState(1)
  const perPage = 10 // Ou le nombre que tu veux afficher par page

  const totalPages = Math.max(1, Math.ceil(filteredData.length / perPage))

  const paginated = useMemo(
    () =>
      filteredData.slice((currentPage - 1) * perPage, currentPage * perPage),
    [filteredData, currentPage]
  )

  return (
    <div className="grid lg:grid-cols-[260px_1fr] gap-6 items-start mt-6 mb-6">
      {/* Filtres à gauche, sticky sur desktop */}
      <div className="hidden lg:block sticky top-20 border rounded-lg shadow-sm bg-background self-start m-1 ml-4 ">
        <FiltersPanel
          search={search}
          onSearch={setSearch}
          priceRange={priceRange}
          onPriceChange={setPriceRange}
          selectedDate={selectedDate}
          onDateSelect={setSelectedDate}
          quality={quality}
          onQualityChange={setQuality}
          productType={productType}
          onTypeChange={setProductType}
          resetFilters={resetFilters}
          productOptions={productOptions}
          qualityOptions={qualityOptions}
          onUserTypeChange={setUserType}
          selectedUserType={userType}
        />
      </div>

      {/* Table à droite */}
      <div className="m-2">
        {/* Affiche les filtres au-dessus sur mobile */}
        <div className="lg:hidden mb-4">
          <FiltersPanel
            search={search}
            onSearch={setSearch}
            priceRange={priceRange}
            onPriceChange={setPriceRange}
            selectedDate={selectedDate}
            onDateSelect={setSelectedDate}
            quality={quality}
            onQualityChange={setQuality}
            productType={productType}
            onTypeChange={setProductType}
            resetFilters={resetFilters}
            productOptions={productOptions}
            qualityOptions={qualityOptions}
            onUserTypeChange={setUserType}
            selectedUserType={userType}
          />
        </div>

        <div className="border rounded-lg bg-background overflow-x-auto mr-6">
          <Table className="text-sm table-auto">
            <TableHeader className="sticky top-0 backdrop-blur supports-[backdrop-filter]:bg-muted/60 z-10">
              <TableRow className="h-9 bg-neutral-100">
                <TableHead>Vendeur</TableHead>
                <TableHead>Acheteur</TableHead>
                <TableHead>Qualité</TableHead>
                <TableHead>Statut</TableHead>
                <TableHead>Prix garanti</TableHead>
                <TableHead>Qté minimum</TableHead>
                <TableHead>Fin du contrat</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {paginated.map(contract => (
                <TableRow key={contract.id}>
                  <TableCell>
                    {contract.seller.firstName} {contract.seller.lastName}
                  </TableCell>
                  <TableCell>
                    {contract.buyer.firstName} {contract.buyer.lastName}
                  </TableCell>
                  <TableCell>{contract.quality.name}</TableCell>
                  <TableCell>{contract.status}</TableCell>
                  <TableCell>
                    {contract.pricePerKg.toLocaleString()} CFA / kg
                  </TableCell>
                  <TableCell>—</TableCell>
                  <TableCell>
                    {new Date(contract.endDate).toLocaleDateString()}
                  </TableCell>
                </TableRow>
              ))}
              {paginated.length === 0 && (
                <TableRow>
                  <TableCell colSpan={7} className="text-center">
                    Aucun contrat trouvé avec ces filtres.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>

        {totalPages > 1 && (
          <PaginationControls
            current={currentPage}
            total={totalPages}
            onChange={setCurrentPage}
          />
        )}
      </div>
    </div>
  )
}
