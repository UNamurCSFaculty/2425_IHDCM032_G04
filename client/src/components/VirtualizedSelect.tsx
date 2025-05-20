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
import { Virtuoso, type VirtuosoHandle } from 'react-virtuoso'

export interface Option {
  id: number | null
  label: string
}

interface Props {
  id: string
  label: string
  options: Option[] // List of options without placeholder
  placeholder: string // Placeholder label, treated as option id null
  value: number | null // Selected id or null
  onChange: (val: number | null) => void
}

const ITEM_HEIGHT = 40
const MAX_VISIBLE = 5

const VirtualizedSelect: React.FC<Props> = ({
  id,
  label,
  options,
  placeholder,
  value,
  onChange,
}) => {
  const [open, setOpen] = useState(false)
  const [search, setSearch] = useState('')
  const [activeIndex, setActiveIndex] = useState(0)
  const virtuosoRef = useRef<VirtuosoHandle>(null)

  // Combine placeholder with options
  const allOptions = useMemo(
    () => [{ id: null, label: placeholder }, ...options],
    [options, placeholder]
  )

  // Filter options by search
  const filtered = useMemo(() => {
    const q = search.toLowerCase()
    return q
      ? allOptions.filter(o => o.label.toLowerCase().includes(q))
      : allOptions
  }, [allOptions, search])

  // Reset activeIndex on filtered change
  useEffect(() => {
    setActiveIndex(0)
    virtuosoRef.current?.scrollToIndex({ index: 0, align: 'start' })
  }, [filtered])

  // Compute list height
  const height =
    Math.min(filtered.length, MAX_VISIBLE) * ITEM_HEIGHT || ITEM_HEIGHT

  // Display currently selected label
  const display = allOptions.find(o => o.id === value)?.label ?? placeholder

  return (
    <div className="space-y-2">
      <label htmlFor={id} className="text-sm font-medium">
        {label}
      </label>

      <Popover
        open={open}
        onOpenChange={openState => {
          if (!openState) setSearch('')
          setOpen(openState)
        }}
      >
        <PopoverTrigger asChild>
          <Button id={id} variant="outline" className="w-full justify-between">
            {display}
            <ChevronDown className="size-4 opacity-50" />
          </Button>
        </PopoverTrigger>

        <PopoverContent className="p-0 w-64" align="start">
          <Command>
            <CommandInput
              placeholder="Rechercher…"
              value={search}
              onValueChange={setSearch}
              onKeyDown={e => {
                if (e.key === 'ArrowDown') {
                  e.preventDefault()
                  const next = Math.min(activeIndex + 1, filtered.length - 1)
                  setActiveIndex(next)
                  virtuosoRef.current?.scrollToIndex({
                    index: next,
                    align: 'center',
                  })
                } else if (e.key === 'ArrowUp') {
                  e.preventDefault()
                  const prev = Math.max(activeIndex - 1, 0)
                  setActiveIndex(prev)
                  virtuosoRef.current?.scrollToIndex({
                    index: prev,
                    align: 'center',
                  })
                } else if (e.key === 'Enter') {
                  e.preventDefault()
                  const opt = filtered[activeIndex]
                  if (opt) {
                    onChange(opt.id)
                    setOpen(false)
                    setSearch('')
                  }
                }
              }}
            />

            <CommandList>
              <CommandEmpty>Aucun résultat</CommandEmpty>
              {filtered.length > 0 && (
                <CommandGroup>
                  <div style={{ height }}>
                    <Virtuoso
                      ref={virtuosoRef}
                      data={filtered}
                      itemContent={(index, opt) => (
                        <CommandItem
                          key={opt.id ?? 'placeholder'}
                          className={cn(
                            'cursor-pointer',
                            index === activeIndex && 'bg-muted'
                          )}
                          value={opt.label}
                          onMouseEnter={() => setActiveIndex(index)}
                          onSelect={() => {
                            onChange(opt.id)
                            setOpen(false)
                            setSearch('')
                          }}
                        >
                          <Check
                            className={cn(
                              'mr-2 h-4 w-4',
                              value === opt.id ? 'opacity-100' : 'opacity-0'
                            )}
                          />
                          {opt.label}
                        </CommandItem>
                      )}
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
