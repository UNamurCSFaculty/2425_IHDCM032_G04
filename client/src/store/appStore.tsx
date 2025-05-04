import { create } from 'zustand'
import {type LanguageDtoReadable} from '@/api/generated/types.gen'

interface AppState {
    languages: LanguageDtoReadable[] | null
    setLanguages: (languages: LanguageDtoReadable[]) => void
}

export const useAppStore = create<AppState>((set) => ({
    languages: null,
    setLanguages: (languages) => set({ languages: languages }),
}))