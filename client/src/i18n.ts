import i18next from 'i18next'
import { initReactI18next } from 'react-i18next'
import LanguageDetector from 'i18next-browser-languagedetector'

import frTranslation from './locales/fr/translation.json'
import enTranslation from './locales/en/translation.json'

i18next
  .use(LanguageDetector) // détecte la langue du navigateur
  .use(initReactI18next) // passe i18n dans React Context
  .init({
    resources: {
      fr: {
        translation: frTranslation,
      },
      en: {
        translation: enTranslation,
      },
    },
    fallbackLng: 'fr',
    interpolation: { escapeValue: false },
    detection: {
      order: ['localStorage', 'navigator'],
      caches: ['localStorage'],
    },
  })

export default i18next
