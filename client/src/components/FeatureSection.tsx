import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Link } from '@tanstack/react-router'
import { Clock, Lock, Handshake } from 'lucide-react'
import { Button } from './ui/button'

const features = [
  {
    title: 'Enchères en temps réel',
    description:
      'Suivez chaque enchère et ne manquez aucune opportunité de surenchérir.',
    icon: Clock,
  },
  {
    title: 'Transactions sécurisées',
    description:
      'Paiement et livraison gérés de bout en bout pour votre tranquillité d’esprit.',
    icon: Lock,
  },
  {
    title: 'Soutien à la filière',
    description:
      'Valorisez les producteurs locaux et contribuez au développement durable.',
    icon: Handshake,
  },
]

export function FeaturesSection() {
  return (
    <section className="bg-gradient-to-br from-green-50 to-yellow-50 py-20">
      <div className="container mx-auto px-6">
        <div className="mb-16 text-center">
          <h2 className="mb-4 text-4xl font-extrabold text-gray-900 sm:text-5xl">
            Pourquoi choisir <span className="text-green-600">e‑Annacarde</span>
             ?
          </h2>
          <p className="mx-auto max-w-2xl text-lg text-gray-700">
            Découvrez comment notre plateforme révolutionne les enchères de noix
            de cajou avec technologie, sécurité et impact social.
          </p>
        </div>

        <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          {features.map(({ title, description, icon: Icon }, idx) => (
            <div
              key={title}
              className={`animate-appear-on-view animate-in fade-in slide-in-from-bottom duration-500 ease-out delay-${idx * 150} fill-mode-forwards`}
            >
              <Card className="intersect-half intersect:scale-100 intersect:opacity-100 h-full scale-50 transform rounded-2xl bg-white opacity-0 shadow-lg transition duration-1000 hover:scale-105 hover:shadow-2xl">
                <CardHeader className="flex flex-col items-center space-y-4 pt-6">
                  <div className="rounded-full bg-green-100 p-4 text-green-600">
                    <Icon className="h-8 w-8" />
                  </div>
                  <CardTitle className="text-xl font-semibold text-gray-900">
                    {title}
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="px-4 pb-6 text-base leading-relaxed text-gray-600">
                    {description}
                  </p>
                </CardContent>
              </Card>
            </div>
          ))}
        </div>

        {/* Call to Action */}
        <div className="mt-12 text-center">
          <Link to="/register">
            <Button size="lg" className="px-8">
              Créer un compte
            </Button>
          </Link>
        </div>
      </div>
    </section>
  )
}
