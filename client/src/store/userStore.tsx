import { create } from 'zustand'
import { type UserDetailDtoReadable } from '@/api/generated/types.gen'

interface UserState {
  user: UserDetailDtoReadable | null

  setUser: (user: UserDetailDtoReadable) => void
  logout: () => void
}

export const useUserStore = create<UserState>(set => ({
  user: null,
  setUser: user => set({ user }),
  logout: () => set({ user: null }),
}))
