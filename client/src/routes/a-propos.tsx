import { createFileRoute } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'
import logo from '@/assets/logo.svg'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Lightbulb, Users, TrendingUp, BookOpen } from 'lucide-react'

export const Route = createFileRoute('/a-propos')({
  component: AboutPage,
})

const BeninFlag = () => (
  <svg
    width="48"
    height="32"
    viewBox="0 0 3 2"
    xmlns="http://www.w3.org/2000/svg"
    className="rounded"
  >
    <rect width="1" height="2" fill="#008751" />
    <rect x="1" width="2" height="1" fill="#fcd116" />
    <rect x="1" y="1" width="2" height="1" fill="#e8112d" />
  </svg>
)

function AboutPage() {
  const { t } = useTranslation()

  const objectives = [
    {
      icon: <TrendingUp className="mb-4 h-12 w-12 text-green-600" />,
      titleKey: 'pages.about_page.objectives.item1.title',
      descriptionKey: 'pages.about_page.objectives.item1.description',
    },
    {
      icon: <Lightbulb className="mb-4 h-12 w-12 text-yellow-500" />,
      titleKey: 'pages.about_page.objectives.item2.title',
      descriptionKey: 'pages.about_page.objectives.item2.description',
    },
    {
      icon: <BookOpen className="mb-4 h-12 w-12 text-blue-500" />,
      titleKey: 'pages.about_page.objectives.item3.title',
      descriptionKey: 'pages.about_page.objectives.item3.description',
    },
  ]

  const animationProps = (delay = 0, existingClassName = '') => ({
    className:
      `${existingClassName} opacity-0 translate-y-4 intersect:opacity-100 intersect:translate-y-0 transition-all duration-700 ease-out intersect-once intersect-half delay-${delay}`.trim(),
  })

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Section Héros */}
      <section className="relative bg-gradient-to-br from-green-900 to-yellow-900 py-20 text-white">
        <div className="bg-pattern absolute inset-0 opacity-10" />{' '}
        <div {...animationProps(0, 'container mx-auto px-6 text-center')}>
          <div className="mx-auto mb-8 inline-block p-3">
            <img
              src={logo}
              alt={t('pages.about_page.logo_alt')}
              className="block h-28 md:h-36"
            />
          </div>
          <h1 className="mb-4 text-4xl font-extrabold md:text-5xl">
            {t('pages.about_page.hero.title')}
          </h1>
          <p className="mx-auto max-w-2xl text-lg text-green-50 md:text-xl">
            {t('pages.about_page.hero.subtitle')}
          </p>
        </div>
      </section>

      {/* Section Notre Projet */}
      <section className="py-16 lg:py-24">
        <div className="container mx-auto px-6">
          <div {...animationProps(150, 'mx-auto max-w-3xl text-center')}>
            <h2 className="mb-6 text-3xl font-bold text-gray-800 md:text-4xl dark:text-white">
              {t('pages.about_page.project.title')}
            </h2>{' '}
            <div className="mb-6 flex justify-center">
              <BeninFlag />
            </div>
            <p className="mb-6 text-lg text-gray-600 dark:text-gray-300">
              {t('pages.about_page.project.paragraph1')}
            </p>{' '}
            <p className="text-lg text-gray-600 dark:text-gray-300">
              {t('pages.about_page.project.paragraph2')}
            </p>
          </div>
        </div>
      </section>

      {/* Section Nos Objectifs */}
      <section className="bg-white py-16 lg:py-24 dark:bg-gray-800">
        <div className="container mx-auto px-6">
          <div {...animationProps(300, 'mx-auto max-w-4xl text-center')}>
            <h2 className="mb-12 text-3xl font-bold text-gray-800 md:text-4xl dark:text-white">
              {t('pages.about_page.objectives.title')}
            </h2>{' '}
          </div>{' '}
          <div className="grid gap-8 md:grid-cols-3">
            {objectives.map((obj, index) => (
              <div key={obj.titleKey} {...animationProps(300 + index * 150)}>
                <Card className="dark:bg-gray-850 h-full transform text-center shadow-lg transition-all duration-300 hover:scale-105 hover:shadow-xl">
                  <CardHeader className="flex flex-col items-center">
                    {obj.icon}
                    <CardTitle className="text-xl font-semibold text-gray-700 dark:text-gray-100">
                      {t(obj.titleKey)}
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <p className="text-gray-600 dark:text-gray-300">
                      {t(obj.descriptionKey)}
                    </p>
                  </CardContent>
                </Card>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Section Un Projet Évolutif */}
      <section className="py-16 lg:py-24">
        <div className="container mx-auto px-6">
          <div {...animationProps(600, 'mx-auto max-w-3xl text-center')}>
            <Users className="mx-auto mb-6 h-16 w-16 text-green-600" />
            <h2 className="mb-6 text-3xl font-bold text-gray-800 md:text-4xl dark:text-white">
              {t('pages.about_page.future.title')}{' '}
            </h2>{' '}
            <p className="text-lg text-gray-600 dark:text-gray-300">
              {' '}
              {t('pages.about_page.future.paragraph')}
            </p>
          </div>
        </div>
      </section>
    </div>
  )
}
