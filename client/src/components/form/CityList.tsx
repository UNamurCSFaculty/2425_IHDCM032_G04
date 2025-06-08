import { CommandItem } from '@/components/ui/command'
import { cn } from '@/lib/utils'
import { Check } from 'lucide-react'
import { Virtuoso } from 'react-virtuoso'

export interface CityOption {
  id: number
  label: string
}

interface Props {
  options: CityOption[]
  selected: number | undefined
  onSelect: (id: number) => void
}

export const ITEM_HEIGHT = 35
export const MAX_VISIBLE = 7

/**
 *
 * Composant de liste de villes.
 * Affiche une liste déroulante de villes avec sélection.
 * Utilise Virtuoso pour le rendu performant de grandes listes.
 */
export function CityList({ options, selected, onSelect }: Props) {
  const height = Math.min(options.length, MAX_VISIBLE) * ITEM_HEIGHT
  return (
    <Virtuoso
      style={{ height }}
      totalCount={options.length}
      itemContent={index => {
        const opt = options[index]
        return (
          <CommandItem
            key={opt.id}
            value={opt.label}
            onSelect={() => onSelect(opt.id)}
          >
            <Check
              className={cn(
                'mr-2 h-4 w-4',
                selected === opt.id ? 'opacity-100' : 'opacity-0'
              )}
            />
            {opt.label}
          </CommandItem>
        )
      }}
    />
  )
}
