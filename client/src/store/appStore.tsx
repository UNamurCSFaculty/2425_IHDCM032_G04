import { type ApplicationDataDto } from '@/api/generated/types.gen'
import { create } from 'zustand'

interface AppDatatate {
  appData: ApplicationDataDto | null
  setAppData: (appData: ApplicationDataDto) => void
}

export const useAppStore = create<AppDatatate>(set => ({
  appData: null,
  setAppData: appData => set({ appData }),
}))

export function useAppData(): ApplicationDataDto {
  const appData = useAppStore(s => s.appData)
  if (appData === null) {
    throw new Error('App data should have been loaded by now!')
  }
  return appData
}
