// src/components/form/CityField.tsx

import { useFieldContext } from '.'
import { Button } from '../ui/button'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandList,
} from '../ui/command'
import { Label } from '../ui/label'
import { Popover, PopoverContent, PopoverTrigger } from '../ui/popover'
import { FieldErrors } from './field-errors'
import cityNamesJson from '@/data/cities.json'
import { ChevronsUpDown } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { CityList, type CityOption } from './CityList'

const cityOptions: CityOption[] = cityNamesJson.map((n, i) => ({
  id: i + 1,
  label: n,
}))

interface CityFieldProps {
  label: string
  required?: boolean
}

export function CityField({ label, required = true }: CityFieldProps) {
  const { t } = useTranslation()
  const field = useFieldContext<number>()
  const hasError =
    field.state.meta.isTouched && field.state.meta.errors.length > 0

  const [open, setOpen] = useState(false)
  const [search, setSearch] = useState('')

  const filtered = useMemo(
    () =>
      search
        ? cityOptions.filter(c =>
            c.label.toLowerCase().includes(search.toLowerCase())
          )
        : cityOptions,
    [search]
  )

  const handleSelect = async (id: number) => {
    field.handleChange(id)
    field.handleBlur()
    setOpen(false)
    setSearch('')

    // Si aucune localisation n'est déjà définie manuellement, on la cherche
    if (!field.form.getFieldValue('address.location')) {
      const city = cityOptions.find(c => c.id === id)
      if (city) {
        try {
          const url =
            'https://nominatim.openstreetmap.org/search' +
            `?city=${encodeURIComponent(city.label)}` +
            '&country=Benin&format=jsonv2&limit=1'

          const response = await fetch(url)
          const [bestResult] = await response.json()

          if (bestResult?.lat && bestResult?.lon) {
            const point = `POINT(${bestResult.lon} ${bestResult.lat})`
            // Met à jour le champ de localisation, ce qui mettra à jour la carte
            field.form.setFieldValue('address.location', point)
          }
        } catch {
          // Erreur réseau ou API, on ne fait rien pour ne pas bloquer l'utilisateur
          console.error('Failed to geocode city:', city.label)
        }
      }
    }
  }

  return (
    <div className="space-y-2">
      <Label htmlFor={`${field.name}-btn`}>
        {label}
        {required && <span className="text-red-500">*</span>}
      </Label>
      <Popover
        open={open}
        onOpenChange={o => {
          if (!o && open) field.handleBlur()
          setOpen(o)
          if (!o) setSearch('')
        }}
      >
        <PopoverTrigger asChild>
          <Button
            id={`${field.name}-btn`}
            variant="outline"
            role="combobox"
            className={`w-full justify-between ${hasError ? 'border-red-500' : ''}`}
          >
            {field.state.value
              ? cityOptions.find(c => c.id === field.state.value)?.label
              : t('form.placeholder.select_city')}
            <ChevronsUpDown className="ml-2 h-4 w-4 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[--radix-popover-trigger-width] p-0">
          <Command>
            <CommandInput
              placeholder={t('form.placeholder.search')}
              value={search}
              onValueChange={setSearch}
            />
            <CommandList>
              <CommandEmpty>{t('form.placeholder.no_city_found')}</CommandEmpty>
              <CommandGroup>
                <CityList
                  options={filtered}
                  selected={field.state.value}
                  onSelect={handleSelect}
                />
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
      <FieldErrors meta={field.state.meta} />
    </div>
  )
}
