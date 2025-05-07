import { create } from 'zustand'

interface AuctionStore {
  selectedAuctionId: number | null
  setSelectedAuctionId: (id: number | null) => void
}

export const useAuctionStore = create<AuctionStore>(set => ({
  selectedAuctionId: null,
  setSelectedAuctionId: id => set({ selectedAuctionId: id }),
}))
