import { createFileRoute, Link } from '@tanstack/react-router'
import imgNoix from '@/assets/Noix de cajou bol.webp'
import { useUserStore } from '@/store/userStore'
import { Button } from '@/components/ui/button'
import { ArrowUpRight, UserRound } from 'lucide-react'
import { FeaturesSection } from '@/components/FeatureSection'

export const Route = createFileRoute('/')({
  component: RouteComponent,
})

function RouteComponent() {
  const user = useUserStore(state => state.user)

  return (
    <div className="bg-base-200 flex min-h-screen flex-col">
      {/* --- Bandeau de bienvenue si connecté --- */}
      {user && (
        <div className="bg-amber-950 p-4 text-center text-sm text-white">
          <span>
            Bienvenue, <strong>{user.name}</strong> ! Prêt(e) pour vos
            prochaines enchères ?
          </span>
        </div>
      )}

      {/* Contenu principal */}
      <main>
        {/* Hero */}
        <section className="relative overflow-hidden bg-gradient-to-r from-green-500 to-yellow-400">
          {/* Décorations circulaires */}
          <div className="absolute -top-16 -left-16 h-72 w-72 rounded-full bg-white/20 blur-3xl"></div>
          <div className="absolute -right-16 -bottom-16 h-96 w-96 rounded-full bg-white/10 blur-2xl"></div>

          <div className="container mx-auto px-6 py-24 lg:flex lg:items-center lg:justify-around">
            {/* Texte */}
            <div className="text-center lg:max-w-xl lg:text-left">
              <h1 className="mb-6 text-6xl leading-tight font-extrabold text-white drop-shadow-lg">
                Enchères & Marché du Cajou
              </h1>
              <p className="mb-8 text-xl text-white/90">
                Rejoignez la plateforme d’enchères en temps réel pour la noix de
                cajou et ses produits transformés au Bénin. Surveillez,
                surenchérissez et soutenez les producteurs locaux en toute
                confiance.
              </p>
              {!user && (
                <div className="flex flex-col justify-center sm:flex-row sm:gap-4 lg:justify-start">
                  <Link to="/login">
                    <Button size="lg">
                      Démarrer maintenant <ArrowUpRight className="!h-5 !w-5" />
                    </Button>
                  </Link>
                  <Link to="/register">
                    <Button variant="outline" size="lg">
                      Créer un compte <UserRound className="!h-5 !w-5" />
                    </Button>
                  </Link>
                </div>
              )}
            </div>

            {/* Illustration */}
            <div className="relative mt-12 animate-spin duration-[120s] before:absolute before:inset-0 before:rounded-full before:bg-white/50 before:blur-2xl before:content-[''] lg:mt-0 lg:flex-shrink-0">
              <img
                src={imgNoix}
                alt="Illustration noix de cajou"
                className="mx-auto w-full max-w-md drop-shadow-2xl lg:mx-0"
              />
            </div>
          </div>
        </section>

        {/* Fonctionnalités */}
        <FeaturesSection />
      </main>
    </div>
  )
}
