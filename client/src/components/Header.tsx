import { logout as logoutApiCall } from '@/api/generated/sdk.gen'
import logo from '@/assets/logo.svg'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  navigationMenuTriggerStyle,
} from '@/components/ui/navigation-menu'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTrigger,
} from '@/components/ui/sheet'
import { cn } from '@/lib/utils'
import { useUserStore } from '@/store/userStore'
import { Link, useNavigate } from '@tanstack/react-router'
import {
  ArrowLeftRight,
  CircleDollarSign,
  FileText,
  History,
  Menu as MenuIcon,
  Package,
  ShoppingCart,
} from 'lucide-react'
import React, { useState } from 'react'

interface MenuItem {
  title: string
  url: string
  description?: string
  icon?: React.ReactNode
  items?: MenuItem[]
}

const activeStyle = { style: { color: 'var(--anacarde-dark-green)' } }

const menu: MenuItem[] = [
  { title: 'Accueil', url: '/' },
  {
    title: 'Achats',
    url: '#',
    items: [
      {
        title: 'Acheter un produit',
        description: 'Acheter un produit à la vente directe',
        icon: <ShoppingCart className="size-5 shrink-0" />,
        url: '/achats/marche',
      },
      {
        title: 'Mes achats',
        description: 'Consulter tous mes achats',
        icon: <ArrowLeftRight className="size-5 shrink-0" />,
        url: '/achats/mes-encheres',
      },
    ],
  },
  {
    title: 'Ventes',
    url: '#',
    items: [
      {
        title: 'Vendre un produit',
        description: 'Créer une nouvelle offre de vente',
        icon: <CircleDollarSign className="size-5 shrink-0" />,
        url: '/ventes/nouvelle-enchere',
      },
      {
        title: 'Mes ventes',
        description: 'Consulter toutes mes ventes',
        icon: <ArrowLeftRight className="size-5 shrink-0" />,
        url: '/ventes/mes-encheres',
      },
    ],
  },
  {
    title: 'Dépôts',
    url: '#',
    items: [
      {
        title: 'Déposer un produit',
        description: 'Encoder un dépôt en magasin',
        icon: <Package className="size-5 shrink-0" />,
        url: '/depots/nouveau-produit',
      },
      {
        title: 'Mes produits',
        description: 'Consulter tous mes dépôts',
        icon: <History className="size-5 shrink-0" />,
        url: '/depots/mes-produits',
      },
    ],
  },
  {
    title: 'Contrats',
    url: '#',
    items: [
      {
        title: 'Créer un contrat',
        description: 'Créer un nouveau contrat',
        icon: <FileText className="size-5 shrink-0" />,
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
  { title: 'Contact', url: '/contact' },
]

export function Header() {
  const user = useUserStore(state => state.user)
  const logout = useUserStore(state => state.logout)
  const isLoggedIn = Boolean(user)
  const navigate = useNavigate()

  // Contrôle du drawer mobile
  const [isSheetOpen, setIsSheetOpen] = useState(false)
  // Pour les sous-menus mobile
  const [openMobileMenuItems, setOpenMobileMenuItems] = useState<
    Record<string, boolean>
  >({})

  const handleLogout = () => {
    logoutApiCall()
    logout()
    navigate({ to: '/', replace: true })
  }

  const toggleMobileSubmenu = (title: string) =>
    setOpenMobileMenuItems(prev => ({ ...prev, [title]: !prev[title] }))

  const handleMobileLinkClick = () => setIsSheetOpen(false)

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-white shadow-sm">
      <div className="container mx-auto flex h-18 items-center justify-between px-5 lg:px-0">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2">
          <img src={logo} alt="Logo e-Anacarde" className="h-16" />
        </Link>

        {/* Desktop Nav */}
        <div className="hidden lg:flex lg:flex-1 lg:justify-center">
          <NavigationMenu delayDuration={0}>
            <NavigationMenuList>
              {menu.map(item => (
                <NavigationMenuItem key={item.title}>
                  {item.items ? (
                    <>
                      <NavigationMenuTrigger>
                        {item.title}
                      </NavigationMenuTrigger>
                      <NavigationMenuContent>
                        <ul className="grid w-[400px] gap-3 p-4 lg:w-[500px] lg:grid-cols-2 lg:w-[600px]">
                          {item.items.map(subItem => (
                            <ListItem
                              key={subItem.title}
                              title={subItem.title}
                              href={subItem.url}
                              icon={subItem.icon}
                            >
                              {subItem.description}
                            </ListItem>
                          ))}
                        </ul>
                      </NavigationMenuContent>
                    </>
                  ) : (
                    <NavigationMenuLink asChild>
                      <Link
                        to={item.url}
                        activeProps={activeStyle}
                        className={navigationMenuTriggerStyle()}
                      >
                        {item.title}
                      </Link>
                    </NavigationMenuLink>
                  )}
                </NavigationMenuItem>
              ))}
            </NavigationMenuList>
          </NavigationMenu>
        </div>

        {/* Desktop User / Auth */}
        <div className="hidden lg:block">
          {!isLoggedIn ? (
            <>
              <Button asChild variant="outline" size="sm" className="mr-2">
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
                  <span className="font-medium">
                    {user?.firstName} {user?.lastName}
                  </span>
                  <Avatar>
                    <AvatarFallback>
                      {user?.firstName?.[0]}
                      {user?.lastName?.[0]}
                    </AvatarFallback>
                  </Avatar>
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

        {/* Mobile Nav */}
        <div className="lg:hidden">
          <Sheet open={isSheetOpen} onOpenChange={setIsSheetOpen}>
            <SheetTrigger asChild>
              <Button variant="ghost" size="icon" className="lg:hidden">
                <MenuIcon className="h-6 w-6" />
                <span className="sr-only">Menu</span>
              </Button>
            </SheetTrigger>

            <SheetContent side="right" className="w-[300px] sm:w-[350px]">
              <SheetHeader className="pb-5 w-full flex items-center justify-center">
                <Link
                  to="/"
                  className="flex items-center gap-2 w-full justify-center"
                >
                  <img src={logo} alt="Logo e-Anacarde" className="h-16" />
                </Link>
              </SheetHeader>

              <nav className="flex flex-col space-y-4 pl-5 pr-5">
                {menu.map(item => (
                  <div key={item.title} className="py-1">
                    {item.items ? (
                      <>
                        <button
                          className="flex w-full items-center justify-between text-lg font-medium"
                          onClick={() => toggleMobileSubmenu(item.title)}
                        >
                          {item.title}
                          <History
                            className={`h-5 w-5 transform transition-transform ${
                              openMobileMenuItems[item.title]
                                ? 'rotate-180'
                                : ''
                            }`}
                          />
                        </button>
                        {openMobileMenuItems[item.title] && (
                          <div className="space-y-3 pl-4">
                            {item.items.map(subItem => (
                              <Link
                                key={subItem.title}
                                to={subItem.url}
                                className="flex items-start gap-3 py-2"
                                onClick={handleMobileLinkClick}
                              >
                                <div className="mt-0.5">{subItem.icon}</div>
                                <div>
                                  <div className="font-medium">
                                    {subItem.title}
                                  </div>
                                  <div className="text-sm text-muted-foreground">
                                    {subItem.description}
                                  </div>
                                </div>
                              </Link>
                            ))}
                          </div>
                        )}
                      </>
                    ) : (
                      <Link
                        to={item.url}
                        className="text-lg font-medium"
                        onClick={handleMobileLinkClick}
                      >
                        {item.title}
                      </Link>
                    )}
                  </div>
                ))}
                {!isLoggedIn && (
                  <>
                    <Button asChild variant="outline" size="sm">
                      <Link to="/login">Connexion</Link>
                    </Button>
                    <Button asChild size="sm">
                      <Link to="/signup">Inscription</Link>
                    </Button>
                  </>
                )}
              </nav>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </header>
  )
}

/* -------------------------------- ListItem ------------------------------- */

const ListItem = React.forwardRef<
  React.ElementRef<'a'>,
  React.ComponentPropsWithoutRef<'a'> & {
    title: string
    icon?: React.ReactNode
  }
>(({ className, title, children, icon, ...props }, ref) => (
  <li>
    <NavigationMenuLink asChild>
      <a
        ref={ref}
        className={cn(
          'block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground',
          className
        )}
        {...props}
      >
        <div className="flex items-center gap-2">
          {icon}
          <div className="text-sm font-medium leading-none">{title}</div>
        </div>
        {children && (
          <p className="line-clamp-2 text-sm leading-snug text-muted-foreground mt-1">
            {children}
          </p>
        )}
      </a>
    </NavigationMenuLink>
  </li>
))
ListItem.displayName = 'ListItem'
