import logoSvg from '@/assets/logo.svg'
import { Link } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

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
  logo: logoProp,
  tagline: taglineProp,
  menuItems: menuItemsProp,
  copyright: copyrightProp,
  bottomLinks: bottomLinksProp,
}: FooterProps) => {
  const { t } = useTranslation()

  const logo = logoProp ?? {
    url: '/',
    src: logoSvg,
    alt: t('footer.logo.alt'),
    title: t('footer.logo.title'),
  }
  const tagline = taglineProp ?? t('footer.tagline')
  const menuItems = menuItemsProp ?? [
    {
      title: t('footer.menu.auctions.title'),
      links: [
        { text: t('footer.menu.auctions.links.raw_materials'), url: '#' },
        { text: t('footer.menu.auctions.links.processed_products'), url: '#' },
        { text: t('footer.menu.auctions.links.my_offers'), url: '#' },
      ],
    },
    {
      title: t('footer.menu.company.title'),
      links: [
        { text: t('footer.menu.company.links.about_us'), url: '#' },
        { text: t('footer.menu.company.links.team'), url: '#' },
        { text: t('footer.menu.company.links.careers'), url: '#' },
        { text: t('footer.menu.company.links.contact'), url: '#' },
      ],
    },
    {
      title: t('footer.menu.resources.title'),
      links: [
        { text: t('footer.menu.resources.links.faq'), url: '#' },
        { text: t('footer.menu.resources.links.support'), url: '#' },
        { text: t('footer.menu.resources.links.blog'), url: '#' },
      ],
    },
    {
      title: t('footer.menu.social_media.title'),
      links: [
        { text: t('footer.menu.social_media.links.facebook'), url: '#' },
        { text: t('footer.menu.social_media.links.twitter'), url: '#' },
        { text: t('footer.menu.social_media.links.linkedin'), url: '#' },
      ],
    },
  ]
  const copyright = copyrightProp ?? t('footer.copyright')
  const bottomLinks = bottomLinksProp ?? [
    { text: t('footer.bottom_links.terms'), url: '#' },
    { text: t('footer.bottom_links.privacy_policy'), url: '#' },
  ]

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
                <h2 className="mb-4 font-bold text-gray-900">
                  {section.title}
                </h2>
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
