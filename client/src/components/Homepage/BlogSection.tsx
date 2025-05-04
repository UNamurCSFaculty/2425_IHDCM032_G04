import avatar from '@/assets/avatar.webp'
import { Link } from '@tanstack/react-router'
import { Button } from '../ui/button'

export function BlogSection() {
  return (
    <section className="bg-gray-50 dark:bg-gray-900">
      <div className="mx-auto max-w-screen-xl px-4 py-8 lg:px-6 lg:py-16">
        <div className="mx-auto mb-8 max-w-screen-sm text-center lg:mb-16">
          <h2 className="mb-4 text-3xl font-extrabold tracking-tight text-gray-900 lg:text-4xl dark:text-white">
            Notre Blog
          </h2>
          <p className="font-light text-gray-500 sm:text-xl dark:text-gray-400">
            Découvrez les dernières actualités, normes et conseils pour
            optimiser vos ventes aux enchères de noix de cajou au Bénin.
          </p>
        </div>
        <div className="grid gap-8 lg:grid-cols-2">
          {/** Article 1 **/}
          <article className="intersect-once intersect:scale-100 intersect:opacity-100 intersect-half scale-50 rounded-lg border border-gray-200 bg-white p-6 opacity-0 shadow-md duration-500 dark:border-gray-700 dark:bg-gray-800">
            <div className="mb-5 flex items-center justify-between text-gray-500">
              <span className="bg-primary-100 text-primary-800 dark:bg-primary-200 dark:text-primary-800 inline-flex items-center rounded px-2.5 py-0.5 text-xs font-medium">
                <svg
                  className="mr-1 h-3 w-3"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path d="M2 6a2 2 0 012-2h6a2 2 0 012 2v8a2 2 0 01-2 2H4a2 2 0 01-2-2V6zM14.553 7.106A1 1 0 0014 8v4a1 1 0 00.553.894l2 1A1 1 0 0018 13V7a1 1 0 00-1.447-.894l-2 1z" />
                </svg>
                Tutoriel
              </span>
              <span className="text-sm">il y a 3 jours</span>
            </div>
            <h3 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
              <Link to="#">
                Comment maîtriser les enchères de noix de cajou sur e-Anacarde
              </Link>
            </h3>
            <p className="mb-5 font-light text-gray-500 dark:text-gray-400">
              Suivez notre guide pas à pas pour comprendre le processus
              d'enchères, fixer vos réserves et maximiser vos revenus sur la
              première plateforme d'enchères de cajou au Bénin.
            </p>
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <img
                  className="h-7 w-7 rounded-full"
                  src={avatar}
                  alt="Admin"
                />
                <span className="font-medium dark:text-white">
                  Aïssatou Adégbidi
                </span>
              </div>
              <Link
                to="#"
                className="text-primary-600 dark:text-primary-500 inline-flex items-center font-medium hover:underline"
              >
                Lire la suite
                <svg
                  className="ml-2 h-4 w-4"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    fillRule="evenodd"
                    d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z"
                    clipRule="evenodd"
                  />
                </svg>
              </Link>
            </div>
          </article>

          {/** Article 2 **/}
          <article className="intersect-once intersect:scale-100 intersect:opacity-100 scale-50 rounded-lg border border-gray-200 bg-white p-6 opacity-0 shadow-md duration-500 dark:border-gray-700 dark:bg-gray-800">
            <div className="mb-5 flex items-center justify-between text-gray-500">
              <span className="bg-primary-100 text-primary-800 dark:bg-primary-200 dark:text-primary-800 inline-flex items-center rounded px-2.5 py-0.5 text-xs font-medium">
                <svg
                  className="mr-1 h-3 w-3"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    fillRule="evenodd"
                    d="M2 5a2 2 0 012-2h8a2 2 0 012 2v10a2 2 0 002 2H4a2 2 0 01-2-2V5zm3 1h6v4H5V6zm6 6H5v2h6v-2z"
                    clipRule="evenodd"
                  />
                  <path d="M15 7h1a2 2 0 012 2v5.5a1.5 1.5 0 01-3 0V7z" />
                </svg>
                Actualité
              </span>
              <span className="text-sm">il y a 7 jours</span>
            </div>
            <h3 className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
              <Link to="#">
                Nouvelles normes gouvernementales pour la production de cajou
              </Link>
            </h3>
            <p className="mb-5 font-light text-gray-500 dark:text-gray-400">
              Le gouvernement béninois a publié de nouvelles directives pour
              améliorer la qualité et la traçabilité de la noix de cajou.
              Découvrez les exigences et comment vous y conformer.
            </p>
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <img
                  className="h-7 w-7 rounded-full"
                  src={avatar}
                  alt="Admin"
                />
                <span className="font-medium dark:text-white">Kofi Mensah</span>
              </div>
              <Link
                to="#"
                className="text-primary-600 dark:text-primary-500 inline-flex items-center font-medium hover:underline"
              >
                Lire la suite
                <svg
                  className="ml-2 h-4 w-4"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    fillRule="evenodd"
                    d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z"
                    clipRule="evenodd"
                  />
                </svg>
              </Link>
            </div>
          </article>
        </div>

        <div className="mt-8 text-center">
          <Link to="/actualites">
            <Button variant="outline" className="">
              Voir toutes les actualités
            </Button>
          </Link>
        </div>
      </div>
    </section>
  )
}
