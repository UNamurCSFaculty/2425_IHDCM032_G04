import { create } from 'zustand'
import type { User } from '@/types/api'

interface UserState {
  user: User | null
  setUser: (user: User) => void
  logout: () => void
  isAuthenticated: () => boolean
}

export const useUserStore = create<UserState>(set => ({
  user: null,
  setUser: user => set({ user }),
  logout: () => set({ user: null }),
  isAuthenticated: () => {
    const { user } = useUserStore.getState() as UserState
    return user !== null
  },
}))
