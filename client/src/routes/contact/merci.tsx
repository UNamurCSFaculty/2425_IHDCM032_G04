import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { Button } from '@/components/ui/button'
import { createFileRoute } from '@tanstack/react-router'
import { useNavigate } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/contact/merci')({
  component: RouteComponent,
})

export function RouteComponent() {
  const { t } = useTranslation(['app', 'breadcrumb', 'common'])
  const navigate = useNavigate()

  return (
    <section className="min-h-screen flex flex-col ">
      {/* Fil d'Ariane */}
      <BreadcrumbSection
        titleKey=""
        breadcrumbs={[
          { labelKey: 'breadcrumb.contact', href: '/contact' },
          { labelKey: 'breadcrumb.thank_you' },
        ]}
        className="mt-4"
      />

      {/* Contenu principal */}
      <div className="flex flex-1 flex-col items-center justify-center px-6 text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">
          {t('contact.thank_you.title', {
            defaultValue: 'Merci pour votre message !',
          })}
        </h1>
        <p className="text-lg text-gray-700 mb-8">
          {t('contact.thank_you.subtitle', {
            defaultValue: 'Nous vous répondrons dans les plus brefs délais.',
          })}
        </p>

        <Button onClick={() => navigate({ to: '/' })}>
          {t('buttons.back_to_home', { defaultValue: 'Retour à l’accueil' })}
        </Button>
      </div>
    </section>
  )
}

export default RouteComponent
