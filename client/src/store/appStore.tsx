import { create } from 'zustand'
import { type ApplicationDataDtoReadable } from '@/api/generated/types.gen'

interface AppDatatate {
  appData: ApplicationDataDtoReadable | null
  setAppData: (appData: ApplicationDataDtoReadable) => void
}

export const useAppStore = create<AppDatatate>(set => ({
  appData: null,
  setAppData: appData => set({ appData }),
}))

export function useAppData(): ApplicationDataDtoReadable {
  const appData = useAppStore(s => s.appData)
  if (appData === null) {
    throw new Error('App data should have been loaded by now!')
  }
  return appData
}
