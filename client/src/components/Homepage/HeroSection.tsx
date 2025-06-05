import logo from '@/assets/logo.svg'
import { Button } from '@/components/ui/button'
import { ArrowRight } from 'lucide-react'
import { useTranslation } from 'react-i18next' // Added

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

const HeroSection = (props: HeroSectionProps) => {
  const { t } = useTranslation()
  const {
    heading: headingProp,
    description: descriptionProp,
    buttons: buttonsProp,
    image: imageProp,
  } = props

  const heading = headingProp ?? t('homepage.hero.heading')
  const description = descriptionProp ?? t('homepage.hero.description')

  const buttons = buttonsProp ?? {
    primary: {
      text: t('homepage.hero.buttons.primary_text'),
      url: '/achats/marche', // Default URL
    },
    secondary: {
      text: t('homepage.hero.buttons.secondary_text'),
      url: '/a-propos', // Default URL
    },
  }

  const image = imageProp ?? {
    src: logo, // Default image source
    alt: t('homepage.hero.image_alt'),
  }

  return (
    <section className="bg-yellow-50 px-6 py-32">
      <div className="container mx-auto">
        <div className="intersect-once intersect-half intersect:scale-100 intersect:opacity-100 container scale-50 transform opacity-0 transition duration-500 ease-out">
          <div className="grid items-center gap-8 lg:grid-cols-2">
            <div className="flex flex-col items-center text-center lg:items-start lg:text-left">
              {/* Badge can be rendered here if needed, e.g., {badge && <span>{badge}</span>} */}
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
                      <ArrowRight className="ml-2 size-4" />
                    </a>
                  </Button>
                )}
              </div>
            </div>
            <img
              src={image.src}
              alt={image.alt}
              className="max-h-96 w-full rounded-md object-contain" // Changed object-cover to object-contain for logo
            />
          </div>
        </div>
      </div>
    </section>
  )
}

export { HeroSection }
