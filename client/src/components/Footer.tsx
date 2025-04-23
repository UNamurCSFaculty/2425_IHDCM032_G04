import logoSvg from '@/assets/logo.svg'
import { Link } from '@tanstack/react-router'

interface MenuItem {
  title: string
  links: {
    text: string
    url: string
  }[]
}

interface FooterProps {
  logo?: {
    url: string
    src: string
    alt: string
    title: string
  }
  tagline?: string
  menuItems?: MenuItem[]
  copyright?: string
  bottomLinks?: {
    text: string
    url: string
  }[]
}

const Footer = ({
  logo = {
    url: '/',
    src: logoSvg,
    alt: 'Logo Cashew Auction Bénin',
    title: 'Cashew Auction Bénin',
  },
  tagline = "Votre plateforme d'enchères de noix de cajou au Bénin — matières premières et produits transformés",
  menuItems = [
    {
      title: 'Enchères',
      links: [
        { text: 'Matières premières', url: '#' },
        { text: 'Produits transformés', url: '#' },
        { text: 'Mes offres', url: '#' },
      ],
    },
    {
      title: 'Entreprise',
      links: [
        { text: 'À propos de nous', url: '#' },
        { text: 'Équipe', url: '#' },
        { text: 'Carrières', url: '#' },
        { text: 'Contact', url: '#' },
      ],
    },
    {
      title: 'Ressources',
      links: [
        { text: 'FAQ', url: '#' },
        { text: 'Support', url: '#' },
        { text: 'Blog', url: '#' },
      ],
    },
    {
      title: 'Réseaux sociaux',
      links: [
        { text: 'Facebook', url: '#' },
        { text: 'Twitter', url: '#' },
        { text: 'LinkedIn', url: '#' },
      ],
    },
  ],
  copyright = '© 2025 Groupe 4. Tous droits réservés.',
  bottomLinks = [
    { text: 'Conditions générales', url: '#' },
    { text: 'Politique de confidentialité', url: '#' },
  ],
}: FooterProps) => {
  return (
    <section className="py-14">
      <div className="container mx-auto px-4">
        <footer>
          <div className="grid grid-cols-2 gap-8 lg:grid-cols-6">
            <div className="col-span-2 mb-8 lg:mb-0">
              <Link to={logo.url} className="flex items-center gap-2">
                <img
                  src={logo.src}
                  alt={logo.alt}
                  title={logo.title}
                  className="h-30"
                />
              </Link>
              <p className="mt-4 text-sm font-medium text-gray-700">
                {tagline}
              </p>
            </div>
            {menuItems.map((section, idx) => (
              <div key={idx}>
                <h3 className="mb-4 font-bold text-gray-900">
                  {section.title}
                </h3>
                <ul className="space-y-2 text-gray-600">
                  {section.links.map((link, linkIdx) => (
                    <li key={linkIdx} className="hover:text-green-600">
                      <Link to={link.url}>{link.text}</Link>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>

          <div className="mt-8 flex flex-col border-t pt-6 text-sm text-gray-500 md:flex-row md:justify-between">
            <p>{copyright}</p>
            <ul className="mt-4 flex space-x-4 md:mt-0">
              {bottomLinks.map((link, i) => (
                <li key={i} className="hover:underline">
                  <Link to={link.url}>{link.text}</Link>
                </li>
              ))}
            </ul>
          </div>
        </footer>
      </div>
    </section>
  )
}

export { Footer }
