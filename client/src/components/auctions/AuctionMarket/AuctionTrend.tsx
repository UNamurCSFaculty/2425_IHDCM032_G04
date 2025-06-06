import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'
import { formatPrice, formatWeight } from '@/utils/formatter'
import { TrendingUp, Weight } from 'lucide-react'

/**
 * Composant React pour afficher les tendances des enchères
 * avec des informations sur le volume, le prix et le poids.
 */
const AuctionTrend = ({
  tooltip,
  icon,
  volume,
  volumeLabel,
  price,
  priceLabel,
  weightKg,
  weightKgLabel,
}: {
  tooltip: string
  icon: React.ReactNode
  volume: number
  volumeLabel: string
  price: number
  priceLabel: string
  weightKgLabel: string
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
                <span className={classDescription}>{volumeLabel}</span>
              </div>
              <TrendingUp />
              <div className="text-xs">
                <span className={classValue}>
                  {formatPrice.format(price)}/kg
                </span>
                <br />
                <span className={classDescription}>{priceLabel}</span>
              </div>
              <Weight />
              <div className="text-xs">
                <span className={classValue}>{formatWeight(weightKg)}</span>
                <br />
                <span className={classDescription}>{weightKgLabel}</span>
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
