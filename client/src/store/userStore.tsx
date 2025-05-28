import { type UserDetailDto } from '@/api/generated/types.gen'
import { create } from 'zustand'

interface UserState {
  user?: UserDetailDto
  token?: string

  setUser: (user: UserDetailDto) => void
  setJwt: (jwt: string) => void
  logout: () => void
}

export const useUserStore = create<UserState>(set => ({
  user: undefined,
  token: undefined,

  setUser: user => set({ user }),
  setJwt: token => set({ token }),
  logout: () => set({ user: undefined, token: undefined }),
}))

export function useAuthUser(): UserDetailDto {
  const user = useUserStore(s => s.user)
  if (!user) throw new Error('User should be authenticated here')
  return user
}
