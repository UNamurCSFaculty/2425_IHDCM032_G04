import { create } from 'zustand'
import type { User } from '@/types/api'

interface UserState {
  user: User | null
  setUser: (user: User) => void
  logout: () => void
}

export const useUserStore = create<UserState>((set) => ({
  user: null,
  setUser: (user) => set({ user }),
  logout: () => set({ user: null }),
}))
