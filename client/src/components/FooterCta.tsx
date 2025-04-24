import { Link } from '@tanstack/react-router'
import { Button } from './ui/button'

export function FooterCta() {
  return (
    <section className="body-font font-primary bg-gradient-to-r from-green-500 to-yellow-400 text-white">
      <div className="container mx-auto px-5 py-20">
        <div className="mx-auto flex flex-col items-start sm:flex-row sm:items-center lg:w-2/3">
          <h1 className="title-font text-primary-600 flex-grow text-4xl font-medium sm:pr-16">
            Rejoignez e-Annacarde dès aujourd’hui et optimisez la vente de vos
            noix de cajou !
          </h1>
          <Link to="/inscription">
            <Button variant={'outline'} className="text-black" size={'lg'}>
              S’inscrire
            </Button>
          </Link>
        </div>
      </div>
    </section>
  )
}
