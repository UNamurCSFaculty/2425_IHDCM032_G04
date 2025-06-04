import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'
import { formatPrice, formatWeight } from '@/utils/formatter'
import { TrendingUp, Weight } from 'lucide-react'

const AuctionTrend = ({
  tooltip,
  icon,
  volume,
  price,
  weightKg,
}: {
  tooltip: string
  icon: React.ReactNode
  volume: number
  price: number
  weightKg: number
}) => {
  const classValue = 'text-xs font-bold text-gray-800'
  const classDescription = 'text-xs font-normal text-gray-500'

  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <div className="w-auto rounded-xl border bg-white p-2 shadow-sm">
            <div className="flex items-center justify-between gap-2">
              {icon}
              <div className="text-xs">
                <span className={classValue}>{volume}</span> <br />
                <span className={classDescription}>enchères</span>
              </div>
              <TrendingUp />
              <div className="text-xs">
                <span className={classValue}>
                  {formatPrice.format(price)}/kg
                </span>
                <br />
                <span className={classDescription}>prix moyen</span>
              </div>
              <Weight />
              <div className="text-xs">
                <span className={classValue}>{formatWeight(weightKg)}</span>
                <br />
                <span className={classDescription}>en vente</span>
              </div>
            </div>
          </div>
        </TooltipTrigger>
        <TooltipContent>
          <p>{tooltip}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  )
}

export default AuctionTrend
