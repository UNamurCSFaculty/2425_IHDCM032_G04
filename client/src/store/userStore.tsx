import { type UserDetailDto } from '@/api/generated/types.gen'
import { create } from 'zustand'

interface UserState {
  user?: UserDetailDto

  setUser: (user: UserDetailDto) => void
  logout: () => void
}

export const useUserStore = create<UserState>(set => ({
  user: undefined,
  setUser: user => set({ user }),
  logout: () => set({ user: undefined }),
}))

export function useAuthUser(): UserDetailDto {
  const user = useUserStore(s => s.user)
  if (!user) throw new Error('User should be authenticated here')
  return user
}
