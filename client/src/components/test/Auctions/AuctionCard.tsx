import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '../../ui/card'
import type { AuctionDtoReadable } from '@/api/generated'
import { CountdownTimer } from '@/components/test/CountdownTimer'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  ChevronRight,
  Clock,
  DollarSign,
  Home,
  MapPin,
  Package,
} from 'lucide-react'
import React from 'react'

interface AuctionCardProps {
  auction: AuctionDtoReadable
  isSelected?: boolean
  onVoirDetails: (id: number) => void
}

export const AuctionCard: React.FC<AuctionCardProps> = ({
  auction,
  isSelected = false,
  onVoirDetails,
}) => {
  const endsIn = new Date(auction.expirationDate)
  return (
    <Card
      className={`overflow-hidden shadow-lg transform transition-all duration-300 hover:shadow-xl ${
        isSelected
          ? 'ring-2 ring-emerald-500'
          : 'hover:scale-105  hover:-translate-y-1'
      }`}
    >
      <CardHeader>
        <div className="flex justify-between items-start">
          <CardTitle
            className="text-lg font-medium truncate w-full"
            title={auction.product.type}
          >
            <div className="center flex items-center justify-center flex-col">
              <div>
                <Badge
                  variant="outline"
                  className="text-green-600 border-green-600 font-semibold mb-2"
                >
                  {auction.bids.length}{' '}
                  {auction.bids.length > 1 ? 'Enchères' : 'Enchère'}
                </Badge>
              </div>
              <div>
                Lot de {auction.product.type} à {auction.product.store.location}
              </div>
              <CardDescription className="flex items-center text-sm text-gray-400 mt-4">
                <MapPin className="w-4 h-4 mr-1" />
                {auction.product.store.location}
              </CardDescription>
            </div>
          </CardTitle>
        </div>
      </CardHeader>

      <CardContent>
        <div className="p-3 bg-gradient-to-b from-green-50 to-yellow-50 rounded-lg mb-4">
          <div className="flex items-center text-lg text-gray-700 justify-center">
            <Clock className="w-4 h-4 mr-1" /> Fini dans
          </div>
          <div className="mt-1 font-semibold text-3xl flex items-center justify-center text-dark-gray-800">
            <CountdownTimer endDate={endsIn} />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 mb-4">
          <div className="p-3 bg-gradient-to-b from-green-50 to-yellow-50 rounded-lg">
            <div className="flex items-center text-xs text-gray-400">
              <Home className="w-4 h-4 mr-1" /> Magasin
            </div>
            <div className="mt-1 font-semibold text-sm">
              {auction.product.store.location}
            </div>
          </div>
          <div className="p-3 bg-gradient-to-b from-green-50 to-yellow-50 rounded-lg">
            <div className="flex items-center text-xs text-gray-400">
              <Package className="w-4 h-4 mr-1" /> Quantité
            </div>
            <div className="mt-1 font-semibold text-sm">
              {auction.productQuantity} × {auction.product.weightKg}kg
            </div>
          </div>
          <div className="p-3 bg-gradient-to-b from-green-50 to-yellow-50 rounded-lg">
            <div className="flex items-center text-xs text-gray-400">
              <DollarSign className="w-4 h-4 mr-1" /> Prix demandé
            </div>
            <div className="mt-1 font-semibold text-sm">
              {auction.price.toLocaleString('fr-BE', {
                style: 'currency',
                currency: 'EUR',
              })}
            </div>
          </div>
          <div className="p-3 bg-gradient-to-b from-green-50 to-yellow-50 rounded-lg">
            <div className="flex items-center text-xs text-gray-400">
              <Package className="w-4 h-4 mr-1" /> Type
            </div>
            <div className="mt-1 font-semibold text-sm">
              {auction.product.type}
            </div>
          </div>
        </div>

        <div className="flex items-center space-x-4 justify-center">
          <div className="w-16 h-16 bg-neutral-00 rounded-lg flex items-center justify-center">
            <Package className="w-8 h-8" />
          </div>
          <div>
            <div className="text-md text-gray-400">Meilleure offre</div>
            <div className="mt-1 text-lg font-bold text-green-600">
              €{auction.price.toLocaleString('fr-BE')}
            </div>
          </div>
        </div>
      </CardContent>

      <CardFooter className="pt-0">
        <Button
          variant="outline"
          className="w-full flex items-center justify-center space-x-2"
          onClick={() => onVoirDetails(auction.id)}
        >
          <span>Voir détails</span>
          <ChevronRight className="w-4 h-4" />
        </Button>
      </CardFooter>
    </Card>
  )
}
