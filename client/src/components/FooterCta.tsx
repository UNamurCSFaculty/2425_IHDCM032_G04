import { Button } from './ui/button'
import { Link } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export function FooterCta() {
  const { t } = useTranslation()
  return (
    <section className="body-font font-primary bg-gradient-to-r from-green-500 to-yellow-400 text-white">
      <div className="container mx-auto px-5 py-20">
        <div className="mx-auto flex flex-col items-start sm:flex-row sm:items-center lg:w-2/3">
          <h1 className="title-font text-primary-600 flex-grow text-4xl font-medium sm:pr-16">
            {t('footer_cta.title')}
          </h1>
          <Link to="/inscription">
            <Button variant={'outline'} className="text-black" size={'lg'}>
              {t('footer_cta.button_signup')}
            </Button>
          </Link>
        </div>
      </div>
    </section>
  )
}
