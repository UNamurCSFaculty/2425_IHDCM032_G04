import type { SellerAuctionsTab } from './SellerAuctions'
import type { AuctionDtoReadable } from '@/api/generated'
import { Button } from '@/components/ui/button'
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { ArrowLeft } from 'lucide-react'
import React from 'react'

type AuctionHeaderProps = {
  auctions: AuctionDtoReadable[]
  selectedAuction?: AuctionDtoReadable
  tab: SellerAuctionsTab
  setSelectedId: (id: number | null) => void
  setPage: (page: number) => void
  setTab: (tab: SellerAuctionsTab) => void
}

export const AuctionHeader: React.FC<AuctionHeaderProps> = ({
  auctions,
  selectedAuction,
  setSelectedId,
  setPage,
  tab,
  setTab,
}) => {
  return (
    <div>
      {/* En-tête */}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Gestion des enchères</h1>
        <span className="text-sm text-gray-500">
          {auctions.length} enchère{auctions.length > 1 ? 's' : ''} au total
        </span>
      </div>

      {/* Onglets / Bouton Retour superposés */}
      <div className="relative w-[300px] mt-4">
        {/* Onglets */}
        <div
          className={
            'transition-opacity duration-200 ' +
            (selectedAuction
              ? 'opacity-0 pointer-events-none'
              : 'opacity-100 pointer-events-auto')
          }
        >
          <Tabs
            value={tab}
            onValueChange={val => {
              setTab(val as SellerAuctionsTab)
              setPage(1)
            }}
          >
            <TabsList className="grid grid-cols-2">
              <TabsTrigger value="active">En cours</TabsTrigger>
              <TabsTrigger value="ended">Terminées</TabsTrigger>
            </TabsList>
          </Tabs>
        </div>

        {/* Bouton Retour */}
        <div
          className={
            'absolute inset-0 flex items-center justify-center transition-opacity duration-200 ' +
            (selectedAuction
              ? 'opacity-100 pointer-events-auto'
              : 'opacity-0 pointer-events-none')
          }
        >
          <Button
            variant="ghost"
            onClick={() => setSelectedId(null)}
            className="flex items-center space-x-2"
          >
            <ArrowLeft className="w-5 h-5" />
            <span>Retour aux enchères</span>
          </Button>
        </div>
      </div>
    </div>
  )
}
