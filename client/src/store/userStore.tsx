import { create } from 'zustand'
import { type UserDetailDtoReadable } from '@/api/generated/types.gen'

interface UserState {
  user: UserDetailDtoReadable | null
  loading: boolean

  setUser: (user: UserDetailDtoReadable) => void
  logout: () => void
  setLoading: (loading: boolean) => void

  isAuthenticated: () => boolean
}

export const useUserStore = create<UserState>((set, get) => ({
  user: null,
  loading: true,
  setUser: user => set({ user }),
  logout: () => set({ user: null }),
  setLoading: loading => set({ loading }),

  isAuthenticated: () => !!get().user,
}))
