import { Button } from '../ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Link } from '@tanstack/react-router'
import { Clock, Handshake, Lock } from 'lucide-react'
import { useTranslation } from 'react-i18next'

const featureItems = [
  {
    key: 'real_time',
    icon: Clock,
  },
  {
    key: 'secure_transactions',
    icon: Lock,
  },
  {
    key: 'industry_support',
    icon: Handshake,
  },
]

/**
 * Composant React pour afficher la section des fonctionnalités
 * de la page d'accueil, avec une grille de cartes animées.
 * Chaque carte présente une fonctionnalité avec une icône et une description.
 */
export function FeaturesSection() {
  const { t } = useTranslation()

  return (
    <section className="bg-gradient-to-br from-green-50 to-yellow-50 py-20">
      <div className="container mx-auto px-6">
        <div className="mb-16 text-center">
          <h2 className="mb-4 text-4xl font-extrabold text-gray-900 sm:text-5xl">
            {t('homepage.features.section_title_prefix')}
            <span className="text-green-600">
              {t('homepage.features.section_title_brand')}
            </span>
            {t('homepage.features.section_title_suffix')}
          </h2>
          <p className="mx-auto max-w-2xl text-lg text-gray-700">
            {t('homepage.features.section_subtitle')}
          </p>
        </div>

        <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          {featureItems.map(({ key, icon: Icon }, idx) => (
            <div
              key={key}
              className={`animate-appear-on-view animate-in fade-in slide-in-from-bottom duration-500 ease-out delay-${idx * 150} fill-mode-forwards`}
            >
              <Card className="intersect-once intersect-half intersect:scale-100 intersect:opacity-100 h-full scale-50 transform rounded-2xl bg-white opacity-0 shadow-lg transition duration-500 hover:scale-105 hover:shadow-2xl">
                <CardHeader className="flex flex-col items-center space-y-4 pt-6">
                  <div className="rounded-full bg-green-100 p-4 text-green-600">
                    <Icon className="h-8 w-8" />
                  </div>
                  <CardTitle className="text-xl font-semibold text-gray-900">
                    {t(`homepage.features.items.${key}.title`)}
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="px-4 pb-6 text-base leading-relaxed text-gray-600">
                    {t(`homepage.features.items.${key}.description`)}
                  </p>
                </CardContent>
              </Card>
            </div>
          ))}
        </div>

        {/* Call to Action */}
        <div className="mt-12 text-center">
          <Link to="/inscription">
            <Button size="lg" className="px-8">
              {t('buttons.subscribe')}
            </Button>
          </Link>
        </div>
      </div>
    </section>
  )
}

export default FeaturesSection
