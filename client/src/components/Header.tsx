import { useUserStore } from '@/store/userStore'
import logo from '@/assets/logo.svg'
import avatar from '@/assets/avatar.webp'
import { Link, useNavigate } from '@tanstack/react-router'
import { Menu, ShoppingCart, CircleDollarSign, History, ArrowLeftRight } from 'lucide-react'
import { logout as logoutApiCall } from '@/api/generated/sdk.gen'

import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import { Button } from '@/components/ui/button'
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
} from '@/components/ui/navigation-menu'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

interface MenuItem {
  title: string
  url: string
  description?: string
  icon?: React.ReactNode
  items?: MenuItem[]
}

const activeStyle = {
  style: {
    color: 'var(--anacarde-dark-green)',
  },
}

export function Header() {
  const user = useUserStore(state => state.user)
  const logout = useUserStore(state => state.logout)
  const isLoggedIn = Boolean(user)
  const navigate = useNavigate()

  const handleLogout = () => {
    logoutApiCall()
    logout()
    navigate({ to: '/', replace: true })
  }

  const menu: MenuItem[] = [
    { title: 'Accueil', url: '/' },
    {
      title: 'Acheter',
      url: '#',
      items: [
        {
          title: 'Acheter un produit',
          description: 'Créer une nouvelle offre d\'achat',
          icon: <ShoppingCart className="size-5 shrink-0" />,
          url: '/acheter',
        },
        {
          title: 'Mes achats',
          description: 'Consulter mes achats en cours',
          icon: <ArrowLeftRight className="size-5 shrink-0" />,
          url: '/achats',
        },
        {
          title: 'Mon historique',
          description: 'Détails de mes achats passés',
          icon: <History className="size-5 shrink-0" />,
          url: '/achats/historique',
        },
      ],
    },
    {
      title: 'Vendre',
      url: '#',
      items: [
        {
          title: 'Vendre un produit',
          description: 'Créer une nouvelle vente',
          icon: <CircleDollarSign className="size-5 shrink-0" />,
          url: '/vendre',
        },
        {
          title: 'Mes ventes',
          description: 'Consulter mes ventes en cours',
          icon: <ArrowLeftRight className="size-5 shrink-0" />,
          url: '/ventes',
        },
        {
          title: 'Mon historique',
          description: 'Détails de mes ventes passées',
          icon: <History className="size-5 shrink-0" />,
          url: '/ventes/historique',
        },
      ],
    },
    {
      title: 'Contrats',
      url: '#',
      items: [
        {
          title: 'Créer un contrat',
          description: 'Voir toutes les enchères',
          icon: <ArrowLeftRight className="size-5 shrink-0" />,
          url: '/contrats',
        },
        {
          title: 'Historique',
          description: 'Détail mes contrats',
          icon: <History className="size-5 shrink-0" />,
          url: '/contrats',
        },
      ],
    },
    { title: 'À propos', url: '/a-propos' },
    { title: 'Contact', url: '/contact' },
  ]

  return (
    <section className="py-2">
      <div className="container mx-auto px-5 md:px-0">
        {/* Desktop Menu */}
        <nav className="hidden items-center justify-between lg:flex">
          <div className="flex items-center gap-6">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2">
              <img src={logo} alt="Logo e‑Anacarde" className="h-20" />
            </Link>
            <div className="flex items-center">
              <NavigationMenu delayDuration={0}>
                <NavigationMenuList>
                  {menu.map(item => renderMenuItem(item))}
                </NavigationMenuList>
              </NavigationMenu>
            </div>
          </div>

          <div className="flex gap-2">
            {!isLoggedIn ? (
              <>
                <Button asChild variant="outline" size="sm">
                  <Link to="/login">Connexion</Link>
                </Button>
                <Button asChild size="sm">
                  <Link to="/signup">Inscription</Link>
                </Button>
              </>
            ) : (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <button className="flex items-center gap-2">
                    <Avatar>
                      <AvatarImage
                        src={user?.avatarUrl || avatar}
                        alt="Avatar utilisateur"
                      />
                      <AvatarFallback>{user?.firstName?.[0]}</AvatarFallback>
                    </Avatar>
                    <span className="font-medium">{user?.firstName}</span>
                  </button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem asChild>
                    <Link to="/profil">Profil</Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to="/parametres">Paramètres</Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem onSelect={handleLogout}>
                    Déconnexion
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>
        </nav>

        {/* Mobile Menu */}
        <div className="block lg:hidden">
          <div className="flex items-center justify-between">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2">
              <img src={logo} alt="Logo e‑Anacarde" className="h-20" />
            </Link>

            <Sheet>
              <SheetTrigger asChild>
                <Button variant="outline" size="icon">
                  <Menu className="size-5" />
                </Button>
              </SheetTrigger>
              <SheetContent className="overflow-y-auto">
                <SheetHeader>
                  <SheetTitle>
                    <Link to="/" className="flex justify-center gap-2">
                      <img src={logo} alt="Logo e‑Anacarde" className="h-20" />
                    </Link>
                  </SheetTitle>
                </SheetHeader>

                <div className="flex flex-col gap-6 p-4">
                  <Accordion
                    type="single"
                    collapsible
                    className="flex w-full flex-col gap-4"
                  >
                    {menu.map(item => renderMobileMenuItem(item))}
                  </Accordion>

                  <div className="flex flex-col gap-3">
                    {!isLoggedIn ? (
                      <>
                        <Button asChild variant="outline">
                          <Link to="/login">Connexion</Link>
                        </Button>
                        <Button asChild>
                          <Link to="/signup">Inscription</Link>
                        </Button>
                      </>
                    ) : (
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <button className="flex items-center gap-2">
                            <Avatar>
                              <AvatarImage
                                src={
                                  user?.avatarUrl ||
                                  'https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp'
                                }
                                alt="Avatar utilisateur"
                              />
                              <AvatarFallback>{user?.name?.[0]}</AvatarFallback>
                            </Avatar>
                            <span className="font-medium">{user?.name}</span>
                          </button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent>
                          <DropdownMenuItem asChild>
                            <Link to="/profil">Profil</Link>
                          </DropdownMenuItem>
                          <DropdownMenuItem asChild>
                            <Link to="/parametres">Paramètres</Link>
                          </DropdownMenuItem>
                          <DropdownMenuItem onSelect={handleLogout}>
                            Déconnexion
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    )}
                  </div>
                </div>
              </SheetContent>
            </Sheet>
          </div>
        </div>
      </div>
    </section>
  )
}

const renderMenuItem = (item: MenuItem) => {
  if (item.items) {
    return (
      <NavigationMenuItem key={item.title}>
        <NavigationMenuTrigger>{item.title}</NavigationMenuTrigger>
        <NavigationMenuContent className="bg-popover text-popover-foreground">
          {item.items.map(subItem => (
            <NavigationMenuLink asChild key={subItem.title}>
              <SubMenuLink item={subItem} />
            </NavigationMenuLink>
          ))}
        </NavigationMenuContent>
      </NavigationMenuItem>
    )
  }

  return (
    <NavigationMenuItem key={item.title}>
      <NavigationMenuLink asChild>
        <Link
          to={item.url}
          activeProps={activeStyle}
          className="group bg-background hover:bg-muted hover:text-accent-foreground inline-flex h-10 w-max items-center justify-center rounded-md px-4 py-2 text-sm font-medium transition-colors"
        >
          {item.title}
        </Link>
      </NavigationMenuLink>
    </NavigationMenuItem>
  )
}

const renderMobileMenuItem = (item: MenuItem) => {
  if (item.items) {
    return (
      <AccordionItem key={item.title} value={item.title} className="border-b-0">
        <AccordionTrigger className="text-md py-0 font-semibold hover:no-underline">
          {item.title}
        </AccordionTrigger>
        <AccordionContent className="mt-2">
          {item.items.map(subItem => (
            <SubMenuLink key={subItem.title} item={subItem} />
          ))}
        </AccordionContent>
      </AccordionItem>
    )
  }

  return (
    <Link key={item.title} to={item.url} className="text-md font-semibold">
      {item.title}
    </Link>
  )
}

const SubMenuLink = ({ item }: { item: MenuItem }) => {
  return (
    <Link
      to={item.url}
      className="hover:bg-muted hover:text-accent-foreground flex flex-row gap-4 rounded-md p-3 leading-none no-underline transition-colors outline-none select-none"
    >
      <div className="w-80">
        <div className="text-foreground">{item.icon}</div>
        <div>
          <div className="text-sm font-semibold">{item.title}</div>
          {item.description && (
            <p className="text-muted-foreground text-sm leading-snug">
              {item.description}
            </p>
          )}
        </div>
      </div>
    </Link>
  )
}
