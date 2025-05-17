import { FieldErrors } from '../field-errors'
import { CityList, type CityOption } from './city-list'
import type { AddressReactForm } from './types'
import { Button } from '@/components/ui/button'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandList,
} from '@/components/ui/command'
import { Label } from '@/components/ui/label'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import cityNamesJson from '@/data/cities.json'
import { ChevronsUpDown } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'

/* ------------------------------------------------------------------ */
/* ------------------------- données villes ------------------------- */

const cityOptions: CityOption[] = cityNamesJson.map((n, i) => ({
  id: i + 1,
  label: n,
}))

/* ------------------------------------------------------------------ */
/* ------------------------- composant ------------------------------ */

interface Props {
  form: AddressReactForm
  required: boolean
}

export const CityField = ({ form, required }: Props) => {
  const { t } = useTranslation()

  /* état local du pop-over et de la recherche */
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

  /* -------------------------------------------------------------- */
  /* ----------------------- rendu -------------------------------- */

  return (
    <form.Field
      name="address.cityId"
      validators={{
        onChange: ({ value }) => (value ? undefined : t('validation.required')),
      }}
    >
      {field => (
        <div className="space-y-1">
          <Label htmlFor={`${field.name}-btn`}>
            Ville {required && <span className="text-red-500">*</span>}
          </Label>

          {/* ------------------- déclencheur ----------------------- */}
          <Popover
            open={open}
            onOpenChange={o => {
              /* Si on ferme sans avoir rien choisi, on déclenche handleBlur
                   pour marquer le champ comme 'touché' et lancer la validation */
              if (!o && open) {
                field.handleBlur()
              }
              setOpen(o)
              if (!o) setSearch('')
            }}
          >
            <PopoverTrigger asChild>
              <Button
                id={`${field.name}-btn`}
                variant="outline"
                role="combobox"
                className="w-full justify-between"
              >
                {field.state.value
                  ? cityOptions.find(c => c.id === field.state.value)?.label
                  : t('form.placeholder.select_city')}
                <ChevronsUpDown className="ml-2 h-4 w-4 opacity-50" />
              </Button>
            </PopoverTrigger>

            {/* ------------------- contenu -------------------------- */}
            <PopoverContent className="p-0 w-[--radix-popover-trigger-width]">
              <Command>
                <CommandInput
                  placeholder={t('form.placeholder.search')}
                  value={search}
                  onValueChange={setSearch}
                />
                <CommandList>
                  <CommandEmpty>
                    {filtered.length
                      ? null
                      : t('form.placeholder.no_city_found')}
                  </CommandEmpty>
                  <CommandGroup>
                    <CityList
                      options={filtered}
                      selected={field.state.value}
                      onSelect={async id => {
                        /* 1️⃣ mise à jour du champ ------------------- */
                        field.handleChange(id)
                        field.handleBlur() // champ touché + validation

                        /* 2️⃣ auto-position si aucun point ---------- */
                        if (!form.getFieldValue('address.location')) {
                          const city = cityOptions.find(c => c.id === id)
                          if (city) {
                            try {
                              const url =
                                'https://nominatim.openstreetmap.org/search' +
                                `?city=${encodeURIComponent(city.label)}` +
                                '&country=Benin&format=jsonv2&limit=1'
                              const [best] = await fetch(url).then(r =>
                                r.json()
                              )
                              if (best?.lat && best?.lon) {
                                const point = `POINT(${best.lon} ${best.lat})`
                                form.setFieldValue('address.location', point)
                              }
                            } catch {
                              /* erreur réseau : on ignore */
                            }
                          }
                        }

                        /* 3️⃣ fermeture du pop-over ----------------- */
                        setOpen(false)
                        setSearch('')
                      }}
                    />
                  </CommandGroup>
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover>

          <FieldErrors meta={field.state.meta} />
        </div>
      )}
    </form.Field>
  )
}
