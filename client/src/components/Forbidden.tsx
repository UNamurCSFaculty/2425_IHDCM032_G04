import { Button } from './ui/button'
import { Link } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

/**
 * Composant de page Forbidden
 */
export default function Forbidden() {
  const { t } = useTranslation()
  return (
    <main className="grid min-h-full place-items-center bg-white px-6 py-24 sm:py-32 lg:px-8">
      <div className="text-center">
        <p className="text-primary text-lg font-semibold">
          {t('forbidden.error_code')}
        </p>
        <h1 className="mt-4 text-5xl font-semibold tracking-tight text-balance text-gray-900 sm:text-7xl">
          {t('forbidden.title')}
        </h1>
        <p className="mt-6 text-lg font-medium text-pretty text-gray-500 sm:text-xl/8">
          {t('forbidden.message')}
        </p>
        <div className="mt-10 flex items-center justify-center gap-x-6">
          <Link to="/">
            <Button size="lg" className="px-8">
              {t('forbidden.button_home')}
            </Button>
          </Link>
          <Link to="/contact" className="text-sm font-semibold text-gray-900">
            <Button size="lg" className="px-8">
              {t('forbidden.button_contact_support')}
            </Button>
          </Link>
        </div>
      </div>
    </main>
  )
}
