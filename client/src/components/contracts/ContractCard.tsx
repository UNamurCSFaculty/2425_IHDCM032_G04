import type { ContractOfferDto } from '@/api/generated'
import { InfoTile } from '../InfoTile'
import { Badge } from '@/components/ui/badge'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { TableCell, TableRow } from '@/components/ui/table'
import { cn } from '@/lib/utils'
import { formatDate, formatPrice } from '@/utils/formatter'
import {
  UserCircle2,
  ShieldCheck,
  ShoppingCart,
  Package,
  CalendarX2,
} from 'lucide-react'
import React from 'react'
import { useTranslation } from 'react-i18next'

export type CardLayout = 'grid' | 'row'

interface ContractCardProps {
  contract: ContractOfferDto
  layout: CardLayout
  isDetail?: boolean
  onDetails: () => void
}

const ContractCard: React.FC<ContractCardProps> = ({ contract, layout }) => {
  const { t } = useTranslation()

  if (layout === 'row') {
    return (
      <TableRow className="hover:bg-muted/50 h-10" key={contract.id}>
        <TableCell>
          {contract.seller.firstName} {contract.seller.lastName}
        </TableCell>
        <TableCell>
          {contract.buyer.firstName} {contract.buyer.lastName}
        </TableCell>
        <TableCell>{contract.quality.name}</TableCell>
        <TableCell>{contract.status}</TableCell>
        <TableCell>{formatPrice.format(contract.pricePerKg)}/kg</TableCell>

        <TableCell>N.A</TableCell>
        <TableCell>{formatDate(contract.endDate)}</TableCell>
      </TableRow>
    )
  }

  return (
    <Card
      className={cn(
        'flex flex-col gap-2 overflow-hidden bg-gradient-to-b from-blue-50/50 to-indigo-50/50 shadow-sm transition-all hover:scale-102 hover:shadow-lg'
      )}
    >
      <CardHeader className="pb-2">
        <CardTitle className="flex items-center gap-2 truncate text-base font-semibold">
          {t('contract.label', { id: contract.id })}
          <Badge variant={'default'} className="ml-auto shrink-0">
            {contract.status}
          </Badge>
        </CardTitle>
        <CardDescription className="flex flex-col flex-wrap items-center justify-center gap-1.5 text-sm text-neutral-700">
          <div className="flex items-center gap-1">
            <Badge variant="outline">
              <UserCircle2 className="size-3.5" />
              {contract.seller.firstName} {contract.seller.lastName} (
              {t('contract.seller_label')})
            </Badge>
          </div>
          <div className="flex items-center gap-1">
            <Badge variant="outline">
              <ShoppingCart className="size-4" />
              {contract.buyer.firstName} {contract.buyer.lastName} (
              {t('contract.buyer_label')})
            </Badge>
          </div>
        </CardDescription>
      </CardHeader>
      <CardContent className="flex-grow space-y-3 pt-0">
        <div className="flex items-center justify-between">
          <InfoTile
            icon={<CalendarX2 className="size-4" />}
            label={t('contract.contract_end_label')}
            size="sm"
          >
            {formatDate(contract.endDate)}
          </InfoTile>
        </div>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <InfoTile
            icon={<ShieldCheck className="size-4" />}
            label={t('contract.quality_label')}
          >
            {contract.quality.name}
          </InfoTile>
          <InfoTile
            icon={<Package className="size-4" />}
            label={t('contract.price_per_kg_label')}
          >
            {formatPrice.format(contract.pricePerKg)}/kg
          </InfoTile>
        </div>
      </CardContent>
    </Card>
  )
}

export default ContractCard
