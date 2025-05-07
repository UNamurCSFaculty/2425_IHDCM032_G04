import logo from '@/assets/logo.svg'
import { Button } from '@/components/ui/button'
import { ArrowRight } from 'lucide-react'

interface HeroSectionProps {
  badge?: string
  heading?: string
  description?: string
  buttons?: {
    primary?: {
      text: string
      url: string
    }
    secondary?: {
      text: string
      url: string
    }
  }
  image?: {
    src: string
    alt: string
  }
}

const HeroSection = ({
  heading = 'Enchères en ligne de noix de cajou',
  description = 'Participez aux enchères en temps réel pour la noix de cajou et ses produits dérivés au Bénin. Surveillez chaque offre, surenchérissez et soutenez les producteurs locaux en toute confiance.',
  buttons = {
    primary: {
      text: 'Voir les enchères',
      url: '/enchères',
    },
    secondary: {
      text: 'En savoir plus',
      url: '/a-propos',
    },
  },
  image = {
    src: logo,
    alt: 'logo de la plateforme d’enchères de noix de cajou',
  },
}: HeroSectionProps) => {
  return (
    <section className="bg-yellow-50 py-32">
      <div className="container mx-auto">
        <div className="intersect-once intersect-half intersect:scale-100 intersect:opacity-100 container scale-50 transform opacity-0 transition duration-500 ease-out">
          <div className="grid items-center gap-8 lg:grid-cols-2">
            <div className="flex flex-col items-center text-center lg:items-start lg:text-left">
              <h1 className="my-6 text-4xl font-bold text-pretty lg:text-6xl">
                {heading}
              </h1>
              <p className="text-muted-foreground mb-8 max-w-xl lg:text-xl">
                {description}
              </p>
              <div className="flex w-full flex-col justify-center gap-2 sm:flex-row lg:justify-start">
                {buttons.primary && (
                  <Button asChild className="w-full sm:w-auto">
                    <a href={buttons.primary.url}>{buttons.primary.text}</a>
                  </Button>
                )}
                {buttons.secondary && (
                  <Button
                    asChild
                    variant="outline"
                    className="w-full sm:w-auto"
                  >
                    <a href={buttons.secondary.url}>
                      {buttons.secondary.text}
                      <ArrowRight className="size-4" />
                    </a>
                  </Button>
                )}
              </div>
            </div>
            <img
              src={image.src}
              alt={image.alt}
              className="max-h-96 w-full rounded-md object-cover"
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export { HeroSection }
