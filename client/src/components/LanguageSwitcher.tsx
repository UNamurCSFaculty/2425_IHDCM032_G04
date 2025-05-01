import { useTranslation } from 'react-i18next'

export function LanguageSwitcher() {
  const { i18n } = useTranslation()

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng)
    localStorage.setItem('i18nextLng', lng)
  }

  return (
    <select
      value={i18n.language}
      onChange={e => changeLanguage(e.target.value)}
    >
      <option value="fr">Français</option>
      <option value="en">English</option>
    </select>
  )
}
