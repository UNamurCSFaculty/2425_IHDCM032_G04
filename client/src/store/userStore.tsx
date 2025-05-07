import { type UserDetailDtoReadable } from '@/api/generated/types.gen'
import { create } from 'zustand'

interface UserState {
  user?: UserDetailDtoReadable

  setUser: (user: UserDetailDtoReadable) => void
  logout: () => void
}

export const useUserStore = create<UserState>(set => ({
  user: null,
  setUser: user => set({ user }),
  logout: () => set({ user: null }),
}))
