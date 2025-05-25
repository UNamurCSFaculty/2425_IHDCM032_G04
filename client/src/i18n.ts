import arTranslation from './locales/ar/translation.json'
import enTranslation from './locales/en/translation.json'
import esTranslation from './locales/es/translation.json'
import frTranslation from './locales/fr/translation.json'
import i18next from 'i18next'
import LanguageDetector from 'i18next-browser-languagedetector'
import { initReactI18next } from 'react-i18next'
import zhCNTranslation from './locales/zh-cn/translation.json'

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
      es: {
        translation: esTranslation,
      },
      ar: {
        translation: arTranslation,
      },
      'zh-CN': {
        translation: zhCNTranslation,
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
