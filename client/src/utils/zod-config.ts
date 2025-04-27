import { z } from 'zod'
import type {} from 'zod'
import i18next from '../i18n' // votre init i18next
import { customErrorMap } from './zod-map'

// 1) Prépare la map “ancienne” qui renvoie une string
const maps = {
  fr: z.core.locales.en().localeError, // => ZodErrorMap pour le français
  en: z.core.locales.en().localeError, // => ZodErrorMap pour l’anglais
}
// 2) On configure Zod 4
z.config({
  // a) customError = priorité n°1
  customError: issue => {
    if (!issue || !issue.path) return undefined
    const field = issue.path[issue.path.length - 1]
    if (
      issue.code === 'invalid_format' &&
      issue.validation === 'regex' &&
      field === 'phone'
    ) {
      // on renvoie l’objet attendu
      return { message: i18next.t('validation.phone.benin') }
    }

    // on retourne undefined pour laisser Zod
    // tomber sur localeError pour les autres cas
    return customErrorMap(issue)
  },
  // b) localeError = fallback pour tout le reste
  localeError: issue => {
    const lang = i18next.language.split('-')[0] as 'fr' | 'en'
    // Choisit la map correspondante (FR ou EN), default sur FR
    const map = maps[lang] ?? maps.fr
    // Appelle la vraie localeError pour produire le message attendu
    return map(issue)
  },
})
