import { Info } from 'lucide-react'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from './ui/tooltip'

/**
 * Composant de tooltip simple pour afficher des informations contextuelles.
 */
const SimpleTooltip = (props: { content: string }) => {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <Info />
        </TooltipTrigger>
        <TooltipContent>
          <p>{props.content}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  )
}

export default SimpleTooltip
