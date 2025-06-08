import type { ApiErrorResponse, ContractOfferDto } from '@/api/generated'
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
  CheckCircle,
  XCircle,
} from 'lucide-react'
import React from 'react'
import { useTranslation } from 'react-i18next'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@radix-ui/react-popover'
import { Button } from '../ui/button'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import {
  acceptContractOfferMutation,
  listContractOffersQueryKey,
  rejectContractOfferMutation,
} from '@/api/generated/@tanstack/react-query.gen'
import { useAuthUser } from '@/store/userStore'
import { toast } from 'sonner'

export type CardLayout = 'grid' | 'row'

interface ContractCardProps {
  contract: ContractOfferDto
  layout: CardLayout
  isDetail?: boolean
  onDetails: () => void
}

/**
 * Composant React pour afficher une carte de contrat
 * avec des actions pour accepter ou rejeter l'offre.
 */
const ContractCard: React.FC<ContractCardProps> = ({ contract, layout }) => {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const [acceptPopoverId, setAcceptPopoverId] = React.useState<number | null>(
    null
  )
  const [rejectPopoverId, setRejectPopoverId] = React.useState<number | null>(
    null
  )
  const user = useAuthUser()

  const acceptContractRequest = useMutation({
    ...acceptContractOfferMutation(),
    onSuccess() {
      queryClient.invalidateQueries({ queryKey: listContractOffersQueryKey() })
      toast.success(t('contract.accepted_ok'), {
        duration: 3000,
      })
    },
    onError(error: ApiErrorResponse) {
      toast.error(t('contract.accepted_fail') + ' (' + error.code + ')', {
        duration: 3000,
      })
    },
  })

  const rejectContractRequest = useMutation({
    ...rejectContractOfferMutation(),
    onSuccess() {
      queryClient.invalidateQueries({ queryKey: listContractOffersQueryKey() })
      toast.success(t('contract.rejected_ok'), {
        duration: 3000,
      })
    },
    onError(error: ApiErrorResponse) {
      toast.error(t('contract.rejected_fail') + ' (' + error.code + ')', {
        duration: 3000,
      })
    },
  })

  const handleContractAction = (
    contractId: number,
    action: 'accept' | 'reject'
  ) => {
    if (action === 'accept') {
      acceptContractRequest.mutate({ path: { contractOfferId: contractId } })
      setAcceptPopoverId(-1)
    } else {
      rejectContractRequest.mutate({ path: { contractOfferId: contractId } })
      setRejectPopoverId(-1)
    }
  }

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
        'flex flex-col gap-2 bg-gradient-to-b from-blue-50/50 to-indigo-50/50 shadow-sm transition-all hover:scale-102 hover:shadow-lg'
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

        {contract.status === 'Ouvert' && contract.seller.id === user.id && (
          <div className="flex justify-center gap-2 pt-2">
            {/* Accept */}
            <Popover
              open={acceptPopoverId === contract.id}
              onOpenChange={open => {
                if (open) {
                  setAcceptPopoverId(contract.id)
                  setRejectPopoverId(-1)
                } else {
                  setAcceptPopoverId(-1)
                }
              }}
            >
              <PopoverTrigger asChild>
                <div>
                  <Button
                    size="sm"
                    variant="outline"
                    className="flex items-center border-green-200 bg-green-700 px-2 py-1 text-white"
                  >
                    <CheckCircle className="mr-1 h-3 w-3" />
                    {t('buttons.accept')}
                  </Button>
                </div>
              </PopoverTrigger>
              <PopoverContent
                className="z-50 w-48 rounded-md bg-white p-2 shadow-lg"
                onClick={e => e.stopPropagation()}
              >
                <p className="mb-2 text-center text-sm">
                  {t('contract.accept_offer_prompt')}
                </p>
                <div className="flex justify-end gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setAcceptPopoverId(-1)}
                  >
                    {t('buttons.cancel')}
                  </Button>
                  <Button
                    size="sm"
                    onClick={() => handleContractAction(contract.id, 'accept')}
                  >
                    {t('common.yes')}
                  </Button>
                </div>
              </PopoverContent>
            </Popover>

            {/* Reject */}
            <Popover
              open={rejectPopoverId === contract.id}
              onOpenChange={open => {
                if (open) {
                  setRejectPopoverId(contract.id)
                  setAcceptPopoverId(-1) // ferme l'autre popover (si ouvert)
                } else {
                  setRejectPopoverId(-1)
                }
              }}
            >
              <PopoverTrigger asChild>
                <div>
                  <Button
                    size="sm"
                    variant="outline"
                    className="flex items-center border-red-200 bg-red-600 px-2 py-1 text-white"
                  >
                    <XCircle className="mr-1 h-3 w-3" />
                    {t('buttons.reject')}
                  </Button>
                </div>
              </PopoverTrigger>
              <PopoverContent
                className="z-50 w-48 rounded-md bg-white p-2 shadow-lg"
                onClick={e => e.stopPropagation()} // empêche les clics de fermer
              >
                <p className="mb-2 text-center text-sm">
                  {t('contract.reject_offer_prompt')}
                </p>
                <div className="flex justify-end gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setRejectPopoverId(-1)}
                  >
                    {t('buttons.cancel')}
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => handleContractAction(contract.id, 'reject')}
                  >
                    {t('common.yes')}
                  </Button>
                </div>
              </PopoverContent>
            </Popover>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

export default ContractCard
