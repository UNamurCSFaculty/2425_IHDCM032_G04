/* eslint-disable react-compiler/react-compiler */
import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from 'react'
import { jwtDecode } from 'jwt-decode'
import { useMutation } from '@tanstack/react-query'
//import { z } from 'zod/v4'
import { zUserDetailDto } from '@/api/generated/zod.gen'
import { useUserStore } from '@/store/userStore'
import type { DocumentDto, UserDetailDto } from '@/api/generated/types.gen'

//type User = z.infer<typeof zUserDetailDto>

interface AuthContextType {
  loginWithGoogle: (idToken: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const setUserStore = useUserStore(s => s.setUser)
  const clearUser = useUserStore(s => s.logout)
  const [loggedOut, setLoggedOut] = useState(false)

  const mutation = useMutation<{ token: string }, unknown, string>({
    mutationFn: async idToken => {
      const form = new FormData()
      form.append(
        'user',
        new Blob([JSON.stringify({ idToken })], { type: 'application/json' })
      )
      const res = await fetch('/api/users/google', {
        method: 'POST',
        body: form,
        credentials: 'include',
      })
      if (!res.ok) throw new Error('Auth Google failed')
      return res.json()
    },
  })

  // hydratation
  useEffect(() => {
    const m = document.cookie.match(/(?:^|; )token=([^;]+)/)
    if (!m) return

    try {
      const payload = jwtDecode<unknown>(m[1])
      const parsed = zUserDetailDto.parse(payload)
      const roles = parsed.roles ? [...parsed.roles] : undefined
      const documents = parsed.documents
        ? parsed.documents.map(
            (d): DocumentDto => ({
              ...d,
              size: Number(d.size),
            })
          )
        : undefined
      const user: UserDetailDto = {
        ...parsed,
        roles,
        documents,
      }
      setUserStore(user)
    } catch {
      document.cookie = 'token=; Max-Age=0; path=/'
    }
  }, [setUserStore])

  useEffect(() => {
    if (loggedOut) {
      document.cookie = 'token=; Max-Age=0; path=/'
      setLoggedOut(false)
    }
  }, [loggedOut])

  async function loginWithGoogle(idToken: string) {
    const { token } = await mutation.mutateAsync(idToken)
    document.cookie = `token=${token}; path=/; secure; samesite=lax`

    const payload = jwtDecode<unknown>(token)
    const parsed = zUserDetailDto.parse(payload)
    const roles = parsed.roles ? [...parsed.roles] : undefined
    const documents = parsed.documents
      ? parsed.documents.map(
          (d): DocumentDto => ({
            ...d,
            size: Number(d.size),
          })
        )
      : undefined

    const user: UserDetailDto = {
      ...parsed,
      roles,
      documents,
    }
    setUserStore(user)
  }

  function logout() {
    clearUser()
    setLoggedOut(true)
  }

  return (
    <AuthContext.Provider value={{ loginWithGoogle, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be inside <AuthProvider>')
  return ctx
}
