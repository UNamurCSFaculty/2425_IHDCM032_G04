import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Globe } from 'lucide-react' // Icône pour le sélecteur de langue
import { useTranslation } from 'react-i18next'
import { useEffect, useState } from 'react' // MODIFICATION: Importer useEffect et useState

export function LanguageSwitcher({
  inMobileNav = false,
}: {
  inMobileNav?: boolean
}) {
  const { i18n } = useTranslation()
  const [currentLanguage, setCurrentLanguage] = useState(i18n.language)

  useEffect(() => {
    const handleLanguageChange = (lng: string) => {
      setCurrentLanguage(lng)
    }

    // S'abonner à l'événement i18next
    i18n.on('languageChanged', handleLanguageChange)

    // Synchroniser l'état si i18n.language est différent de l'état du composant
    // Cela peut se produire si la langue change avant que l'écouteur ne soit attaché
    // ou lors du montage initial si i18n.language a été mis à jour au moment où l'effet s'exécute.
    if (i18n.language !== currentLanguage) {
      setCurrentLanguage(i18n.language)
    }

    return () => {
      i18n.off('languageChanged', handleLanguageChange)
    }
  }, [i18n, currentLanguage]) // Dépendances de l'effet

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng)
    localStorage.setItem('i18nextLng', lng)
  }

  const languages = [
    { code: 'fr', name: 'Français' },
    { code: 'en', name: 'English' },
  ]

  if (inMobileNav) {
    return (
      <div className="space-y-1">
        <p className="text-muted-foreground px-1 text-sm font-medium">Langue</p>
        {languages.map(lang => (
          <Button
            key={lang.code}
            variant={
              currentLanguage.startsWith(lang.code) ? 'outline' : 'ghost'
            }
            className="w-full justify-start"
            onClick={() => changeLanguage(lang.code)}
            disabled={currentLanguage.startsWith(lang.code)}
          >
            {lang.name}
          </Button>
        ))}
      </div>
    )
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" aria-label="Changer de langue">
          <Globe className="h-5 w-5" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuLabel>Langue</DropdownMenuLabel>
        <DropdownMenuSeparator />
        {languages.map(lang => (
          <DropdownMenuItem
            key={lang.code}
            onClick={() => changeLanguage(lang.code)}
            disabled={currentLanguage.startsWith(lang.code)}
          >
            {lang.name}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
