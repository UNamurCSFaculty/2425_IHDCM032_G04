import { Button } from '@/components/ui/button'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { cn } from '@/lib/utils'
import { Check, ChevronDown } from 'lucide-react'
import React, { useEffect, useMemo, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Virtuoso, type VirtuosoHandle } from 'react-virtuoso'

export interface Option {
  // Le placeholder ayant un id `null`, on garde ce type.
  id: number | null
  label: string
}

interface Props {
  id: string
  label: string
  options: Option[] // Assure que les options ont un id de type number
  placeholder?: string
  value: number | null // La valeur peut être un id ou null pour le placeholder
  onChange: (val: number | null) => void
  /**
   * Détermine si le placeholder est une option sélectionnable.
   * @default false
   */
  placeholderSelectable?: boolean
}

// Constantes pour la virtualisation
const ITEM_HEIGHT = 40
const MAX_VISIBLE_ITEMS = 5

const VirtualizedSelect: React.FC<Props> = ({
  id,
  label,
  options,
  placeholder = '',
  value,
  onChange,
  placeholderSelectable = false,
}) => {
  const [open, setOpen] = useState(false)
  const [search, setSearch] = useState('')
  const [activeIndex, setActiveIndex] = useState(0)
  const virtuosoRef = useRef<VirtuosoHandle>(null)
  const { t } = useTranslation()

  // 1. On crée une liste unique d'options incluant toujours le placeholder.
  //    Sa sélection sera gérée au niveau de l'item.
  const allOptions = useMemo<Option[]>(
    () =>
      placeholderSelectable
        ? [{ id: null, label: placeholder }, ...options]
        : options,
    [options, placeholder, placeholderSelectable]
  )

  // 2. On filtre cette liste complète en fonction de la recherche.
  const filteredOptions = useMemo(() => {
    if (!search) return allOptions
    const q = search.toLowerCase()
    return allOptions.filter(o => o.label.toLowerCase().includes(q))
  }, [allOptions, search])

  // 3. On trouve le label à afficher dans le bouton.
  //    Si la valeur est `null`, on affiche le placeholder, sinon le label correspondant.
  const displayLabel = useMemo(() => {
    return allOptions.find(o => o.id === value)?.label ?? placeholder
  }, [allOptions, value, placeholder])

  // Reset l'index actif quand le filtre change
  useEffect(() => {
    setActiveIndex(0)
    virtuosoRef.current?.scrollToIndex({ index: 0, align: 'start' })
  }, [filteredOptions])

  // Calcule la hauteur de la liste pour éviter les sauts de layout
  const height =
    Math.min(filteredOptions.length, MAX_VISIBLE_ITEMS) * ITEM_HEIGHT ||
    ITEM_HEIGHT

  const handleSelect = (option: Option) => {
    const isPlaceholder = option.id === null
    if (isPlaceholder && !placeholderSelectable) {
      return // Ne fait rien si le placeholder n'est pas sélectionnable
    }
    onChange(option.id)
    setOpen(false)
    setSearch('')
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    const total = filteredOptions.length
    if (!total) return

    if (e.key === 'ArrowDown') {
      e.preventDefault()
      const nextIndex = (activeIndex + 1) % total
      setActiveIndex(nextIndex)
      virtuosoRef.current?.scrollToIndex({ index: nextIndex, align: 'center' })
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      const prevIndex = (activeIndex - 1 + total) % total
      setActiveIndex(prevIndex)
      virtuosoRef.current?.scrollToIndex({ index: prevIndex, align: 'center' })
    } else if (e.key === 'Enter') {
      e.preventDefault()
      const selectedOption = filteredOptions[activeIndex]
      if (selectedOption) {
        handleSelect(selectedOption)
      }
    }
  }

  if (!placeholder) {
    placeholder = t('form.select.placeholder')
  }

  return (
    <div className="space-y-2">
      <label htmlFor={id} className="text-sm font-medium">
        {label}
      </label>

      <Popover
        open={open}
        onOpenChange={openState => {
          if (!openState) setSearch('') // Réinitialise la recherche à la fermeture
          setOpen(openState)
        }}
      >
        <PopoverTrigger asChild>
          <Button id={id} variant="outline" className="w-full justify-between">
            <span className="truncate">{displayLabel}</span>
            <ChevronDown className="size-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>

        <PopoverContent
          className="w-[var(--radix-popover-trigger-width)] p-0"
          align="start"
        >
          <Command>
            <CommandInput
              placeholder="Rechercher…"
              value={search}
              onValueChange={setSearch}
              onKeyDown={handleKeyDown}
            />

            <CommandList>
              <CommandEmpty>Aucun résultat</CommandEmpty>
              {filteredOptions.length > 0 && (
                <CommandGroup>
                  <div style={{ height }}>
                    <Virtuoso
                      ref={virtuosoRef}
                      data={filteredOptions}
                      itemContent={(index, option) => {
                        const isPlaceholder = option.id === null
                        const isDisabled =
                          isPlaceholder && !placeholderSelectable

                        return (
                          <CommandItem
                            key={option.id ?? 'placeholder'}
                            className={cn(
                              'cursor-pointer',
                              index === activeIndex && 'bg-muted',
                              isDisabled && 'text-muted-foreground opacity-60'
                            )}
                            value={option.label}
                            disabled={isDisabled}
                            onMouseEnter={() => setActiveIndex(index)}
                            onSelect={() => handleSelect(option)}
                          >
                            <Check
                              className={cn(
                                'mr-2 h-4 w-4',
                                value === option.id
                                  ? 'opacity-100'
                                  : 'opacity-0'
                              )}
                            />
                            {option.label}
                          </CommandItem>
                        )
                      }}
                    />
                  </div>
                </CommandGroup>
              )}
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
    </div>
  )
}

export default VirtualizedSelect
