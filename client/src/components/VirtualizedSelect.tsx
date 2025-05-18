import { Button } from './ui/button'
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover'
import { ChevronDown } from 'lucide-react'
import React from 'react'
import { Virtuoso } from 'react-virtuoso'

interface VirtualizedSelectProps {
  id: string
  label: string
  options: string[]
  value: number | null
  onChange: (value: number | null) => void
  placeholder: string
}

const VirtualizedSelect: React.FC<VirtualizedSelectProps> = ({
  id,
  label,
  options,
  value,
  onChange,
  placeholder,
}) => (
  <div className="space-y-2">
    <label htmlFor={id} className="text-sm font-medium">
      {label}
    </label>
    <Popover>
      <PopoverTrigger asChild>
        <Button id={id} variant="outline" className="w-full justify-between">
          {value ? options[value - 1] : placeholder}
          <ChevronDown className="size-4 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="p-0 w-60" align="start">
        <Virtuoso
          style={{ height: 220 }}
          totalCount={options.length + 1}
          itemContent={index => {
            if (index === 0)
              return (
                <Button
                  variant="ghost"
                  className="w-full justify-start"
                  onClick={() => onChange(null)}
                >
                  Toutes
                </Button>
              )
            return (
              <Button
                variant="ghost"
                className="w-full justify-start"
                onClick={() => onChange(index)}
              >
                {options[index - 1]}
              </Button>
            )
          }}
        />
      </PopoverContent>
    </Popover>
  </div>
)

export default VirtualizedSelect
