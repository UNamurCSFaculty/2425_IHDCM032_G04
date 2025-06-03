import { logout as logoutApiCall } from '@/api/generated/sdk.gen'
import logo from '@/assets/logo.svg'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
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
import { LanguageSwitcher } from '@/components/LanguageSwitcher'
import {
  ArrowLeftRight,
  Menu as MenuIcon,
  Package,
  ShoppingCart,
  ChevronDown,
  UserCircle,
  LogOut,
  HelpCircle,
  PackagePlus,
  DollarSign,
  ScrollText,
} from 'lucide-react'
import React, { useState } from 'react'
import { useTranslation } from 'react-i18next'
import type { UserDetailDto } from '@/api/generated/types.gen'

interface MenuItemDefinition {
  titleKey: string
  url: string
  descriptionKey?: string
  icon?: React.ReactNode
  items?: MenuItemDefinition[]
  requiresAuth?: boolean
  hideWhenAuth?: boolean
  allowedUserTypes?: UserDetailDto['type'][]
}

const activeStyle = { style: { color: 'var(--anacarde-dark-green)' } }

export function Header() {
  const { t } = useTranslation()
  const user = useUserStore(state => state.user)
  const logoutUserStore = useUserStore(state => state.logout)
  const isLoggedIn = Boolean(user)
  const navigate = useNavigate()

  const [isSheetOpen, setIsSheetOpen] = useState(false)
  const [openMobileMenuItems, setOpenMobileMenuItems] = useState<
    Record<string, boolean>
  >({})

  const handleLogout = () => {
    logoutApiCall()
    logoutUserStore()
    navigate({ to: '/', replace: true })
    if (isSheetOpen) setIsSheetOpen(false)
  }

  const toggleMobileSubmenu = (titleKey: string) =>
    setOpenMobileMenuItems(prev => ({ ...prev, [titleKey]: !prev[titleKey] }))

  const handleMobileLinkClick = (url?: string) => {
    setIsSheetOpen(false)
    if (url) navigate({ to: url })
  }

  const authenticatedSeller: UserDetailDto['type'][] = [
    'producer',
    'admin',
    'transformer',
  ]
  const authenticatedBuyer: UserDetailDto['type'][] = [
    'admin',
    'transformer',
    'exporter',
  ]
  const authenticatedTrader: UserDetailDto['type'][] = [
    'producer',
    'admin',
    'transformer',
    'exporter',
    'trader',
  ]
  const allAuthenticatedButCarrierQualityInspector: UserDetailDto['type'][] = [
    'producer',
    'admin',
    'transformer',
    'exporter',
    'trader',
  ]

  const menuItems: MenuItemDefinition[] = [
    { titleKey: 'header.menu.home', url: '/' },
    {
      titleKey: 'breadcrumb.buy',
      url: '#',
      items: [
        {
          titleKey: 'header.menu.marketplace.browse',
          descriptionKey: 'header.menu.marketplace.browse_desc',
          icon: <ShoppingCart className="size-5 shrink-0" />,
          url: '/achats/marche',
          requiresAuth: true,
          allowedUserTypes: authenticatedTrader,
        },
        {
          titleKey: 'header.menu.my_space.my_purchases',
          descriptionKey: 'header.menu.my_space.my_purchases_marketplace_desc',
          icon: <ArrowLeftRight className="size-5 shrink-0" />,
          url: '/achats/mes-encheres',
          requiresAuth: true,
          allowedUserTypes: authenticatedBuyer,
        },
        {
          titleKey: 'header.menu.my_space.my_contracts',
          descriptionKey: 'header.menu.my_space.my_contracts_marketplace_desc',
          icon: <ScrollText className="size-5 shrink-0" />,
          url: '/contrats/mes-contrats',
          requiresAuth: true,
          allowedUserTypes: authenticatedBuyer,
        },
      ],
    },
    {
      titleKey: 'breadcrumb.sell',
      url: '#',
      items: [
        {
          titleKey: 'header.menu.marketplace.new_sale',
          descriptionKey: 'header.menu.marketplace.new_sale_desc',
          icon: <DollarSign className="size-5 shrink-0" />,
          url: '/ventes/nouvelle-enchere',
          requiresAuth: true,
          allowedUserTypes: authenticatedSeller,
        },
        {
          titleKey: 'header.menu.my_space.my_sales',
          descriptionKey: 'header.menu.my_space.my_sales_marketplace_desc',
          icon: <ArrowLeftRight className="size-5 shrink-0" />,
          url: '/ventes/mes-encheres',
          requiresAuth: true,
          allowedUserTypes: authenticatedSeller,
        },
        {
          titleKey: 'header.menu.marketplace.new_deposit',
          descriptionKey: 'header.menu.marketplace.new_deposit_desc',
          icon: <PackagePlus className="size-5 shrink-0" />,
          url: '/depots/nouveau-produit',
          requiresAuth: true,
          allowedUserTypes: authenticatedSeller,
        },
        {
          titleKey: 'header.menu.my_space.my_deposits',
          descriptionKey: 'header.menu.my_space.my_deposits_marketplace_desc',
          icon: <Package className="size-5 shrink-0" />,
          url: '/depots/mes-produits',
          requiresAuth: true,
          allowedUserTypes: authenticatedSeller,
        },
        {
          titleKey: 'header.menu.my_space.my_contracts',
          descriptionKey: 'header.menu.my_space.my_contracts_marketplace_desc',
          icon: <ScrollText className="size-5 shrink-0" />,
          url: '/contrats/mes-contrats',
          requiresAuth: true,
          allowedUserTypes: authenticatedSeller,
        },
      ],
    },
    {
      titleKey: 'header.menu.marketplace.title',
      url: '#',
      items: [
        {
          titleKey: 'header.menu.marketplace.how_it_works',
          descriptionKey: 'header.menu.marketplace.how_it_works_desc',
          icon: <HelpCircle className="size-5 shrink-0" />,
          url: '/connexion',
          hideWhenAuth: true,
        },
      ],
    },
    {
      titleKey: 'header.menu.news',
      url: '/actualites',
    },
    {
      titleKey: 'header.menu.about',
      url: '/a-propos',
    },
    {
      titleKey: 'header.menu.contact',
      url: '/contact',
    },
  ]

  const userMenuItemsBase: MenuItemDefinition[] = [
    {
      titleKey: 'header.menu.my_space.my_purchases',
      url: '/achats/mes-encheres',
      icon: <ShoppingCart className="mr-2 size-4" />,
      requiresAuth: true,
      allowedUserTypes: allAuthenticatedButCarrierQualityInspector,
    },
    {
      titleKey: 'header.menu.my_space.my_sales',
      url: '/ventes/mes-encheres',
      icon: <ArrowLeftRight className="mr-2 size-4" />,
      requiresAuth: true,
      allowedUserTypes: authenticatedTrader,
    },
    {
      titleKey: 'header.menu.my_space.my_deposits',
      url: '/depots/mes-produits',
      icon: <Package className="mr-2 size-4" />,
      requiresAuth: true,
      allowedUserTypes: authenticatedSeller,
    },
    {
      titleKey: 'header.menu.my_space.my_contracts',
      url: '/contrats/mes-contrats',
      icon: <ScrollText className="mr-2 size-4" />,
      requiresAuth: true,
      allowedUserTypes: allAuthenticatedButCarrierQualityInspector,
    },
  ]

  const filteredMenuItems = menuItems
    .map(item => {
      if (item.items) {
        const filteredSubItems = item.items.filter(subItem => {
          const generalAccess = !subItem.requiresAuth && !subItem.hideWhenAuth
          const hiddenWhenAuthAccess = subItem.hideWhenAuth && !isLoggedIn
          let authRequiredAccess =
            subItem.requiresAuth &&
            isLoggedIn &&
            (!subItem.allowedUserTypes ||
              (user &&
                user.type &&
                subItem.allowedUserTypes.includes(user.type)))

          if (authRequiredAccess && subItem.url === '/depots/nouveau-produit') {
            authRequiredAccess =
              authRequiredAccess && user && user.storeAssociated === true
          }

          return generalAccess || hiddenWhenAuthAccess || authRequiredAccess
        })
        return { ...item, items: filteredSubItems }
      }
      return item
    })
    .filter(
      item => !(item.items && item.items.length === 0 && item.url === '#')
    ) // Cache les triggers de menu si tous leurs enfants sont filtrés

  const filteredUserMenuItems = userMenuItemsBase.filter(item => {
    if (!isLoggedIn || !user || !user.type) return false
    // Additional check for '/depots/nouveau-produit' if it were in userMenuItemsBase
    // For now, it's in the main menu, so the logic above handles it.
    // If '/depots/mes-produits' also needs this check for userMenuItemsBase:
    if (item.url === '/depots/mes-produits') {
      // Assuming authenticatedSeller implies storeAssociated for this specific item in this list
      // or add a specific check: return user.storeAssociated === true && (!item.allowedUserTypes || item.allowedUserTypes.includes(user.type))
    }
    return !item.allowedUserTypes || item.allowedUserTypes.includes(user.type)
  })

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-white shadow-sm dark:border-gray-700 dark:bg-gray-900">
      <div className="container mx-auto flex h-18 items-center justify-between px-5 lg:px-0">
        <Link to="/" className="flex items-center gap-2">
          <img src={logo} alt={t('header.logo_alt')} className="h-16" />
        </Link>

        {/* Desktop Nav */}
        <div className="hidden lg:flex lg:flex-1 lg:justify-center">
          <NavigationMenu delayDuration={0}>
            <NavigationMenuList>
              {filteredMenuItems.map(item => (
                <NavigationMenuItem key={item.titleKey}>
                  {item.items && item.items.length > 0 ? (
                    <>
                      <NavigationMenuTrigger>
                        {item.icon && <span className="mr-2">{item.icon}</span>}{' '}
                        {t(item.titleKey)}
                      </NavigationMenuTrigger>
                      <NavigationMenuContent>
                        <ul className="grid w-[400px] gap-3 p-4 md:w-[500px] lg:w-[600px] lg:grid-cols-2">
                          {item.items.map(subItem => (
                            <ListItem
                              key={subItem.titleKey}
                              title={t(subItem.titleKey)}
                              href={subItem.url}
                              icon={subItem.icon}
                            >
                              {subItem.descriptionKey &&
                                t(subItem.descriptionKey)}
                            </ListItem>
                          ))}
                        </ul>
                      </NavigationMenuContent>
                    </>
                  ) : !item.items ? (
                    <NavigationMenuLink asChild>
                      <Link
                        to={item.url}
                        activeProps={activeStyle}
                        className={navigationMenuTriggerStyle()}
                      >
                        {t(item.titleKey)}
                      </Link>
                    </NavigationMenuLink>
                  ) : null}
                </NavigationMenuItem>
              ))}
            </NavigationMenuList>
          </NavigationMenu>
        </div>

        <div className="hidden items-center gap-2 lg:flex">
          {!isLoggedIn ? (
            <>
              <Button asChild size="sm">
                <Link to="/connexion">{t('header.auth.login')}</Link>
              </Button>
            </>
          ) : (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="flex items-center gap-2">
                  <Avatar className="size-8">
                    <AvatarFallback>
                      {user?.firstName?.[0]?.toUpperCase()}
                      {user?.lastName?.[0]?.toUpperCase()}
                    </AvatarFallback>
                  </Avatar>
                  <span className="font-medium">
                    {user?.firstName} {user?.lastName}
                  </span>
                  <ChevronDown className="text-muted-foreground size-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel className="font-normal">
                  <div className="flex flex-col space-y-1">
                    <p className="text-sm leading-none font-medium">
                      {user?.firstName} {user?.lastName}
                    </p>
                    <p className="text-muted-foreground text-xs leading-none">
                      {user?.email}
                    </p>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                {filteredUserMenuItems.map(item => (
                  <DropdownMenuItem key={item.titleKey} asChild>
                    <Link to={item.url}>
                      {item.icon}
                      {t(item.titleKey)}
                    </Link>
                  </DropdownMenuItem>
                ))}
                {filteredUserMenuItems.length > 0 && <DropdownMenuSeparator />}
                <DropdownMenuItem asChild>
                  <Link to="/profil">
                    <UserCircle className="mr-2 size-4" />
                    {t('header.auth.profile')}
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuItem onSelect={handleLogout}>
                  <LogOut className="mr-2 size-4" />
                  {t('header.auth.logout')}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          )}
          <LanguageSwitcher />
        </div>

        {/* Mobile Nav */}
        <div className="lg:hidden">
          <Sheet open={isSheetOpen} onOpenChange={setIsSheetOpen}>
            <SheetTrigger asChild>
              <Button variant="ghost" size="icon">
                <MenuIcon className="h-6 w-6" />
                <span className="sr-only">
                  {t('header.mobile.toggle_menu_sr')}
                </span>
              </Button>
            </SheetTrigger>
            <SheetContent
              side="right"
              className="w-[300px] overflow-y-auto p-0 sm:w-[350px]"
            >
              <SheetHeader className="border-b p-4">
                <Link
                  to="/"
                  className="flex items-center justify-center gap-2"
                  onClick={() => handleMobileLinkClick('/')}
                >
                  <img src={logo} alt={t('header.logo_alt')} className="h-12" />
                </Link>
              </SheetHeader>

              <nav className="flex flex-col divide-y text-base">
                {filteredMenuItems.map(item => (
                  <div key={item.titleKey}>
                    {item.items && item.items.length > 0 ? (
                      <>
                        <button
                          className="hover:bg-accent flex w-full items-center justify-between px-4 py-3 text-left font-medium"
                          onClick={() => toggleMobileSubmenu(item.titleKey)}
                        >
                          <span className="flex items-center">
                            {item.icon /* Pour icône sur le trigger principal mobile si besoin */ && (
                              <span className="text-muted-foreground mr-3 size-5">
                                {item.icon}
                              </span>
                            )}
                            {t(item.titleKey)}
                          </span>
                          <ChevronDown
                            className={`text-muted-foreground size-5 transform transition-transform ${
                              openMobileMenuItems[item.titleKey]
                                ? 'rotate-180'
                                : ''
                            }`}
                          />
                        </button>
                        {openMobileMenuItems[item.titleKey] && (
                          <div className="bg-muted/50">
                            {item.items.map(subItem => (
                              <Link
                                key={subItem.titleKey}
                                to={subItem.url}
                                className="hover:bg-accent flex items-start gap-3 px-4 py-3 pl-8"
                                onClick={() =>
                                  handleMobileLinkClick(subItem.url)
                                }
                              >
                                {subItem.icon && (
                                  <div className="text-muted-foreground mt-0.5 size-5">
                                    {subItem.icon}
                                  </div>
                                )}
                                <div className="flex flex-col">
                                  <span className="font-medium">
                                    {t(subItem.titleKey)}
                                  </span>
                                  {subItem.descriptionKey && (
                                    <span className="text-muted-foreground text-sm">
                                      {t(subItem.descriptionKey)}
                                    </span>
                                  )}
                                </div>
                              </Link>
                            ))}
                          </div>
                        )}
                      </>
                    ) : !item.items ? (
                      <Link
                        to={item.url}
                        className="hover:bg-accent flex items-center px-4 py-3 font-medium"
                        onClick={() => handleMobileLinkClick(item.url)}
                      >
                        {/* Icônes pour Actualités, A Propos, Contact sont supprimées ici aussi si besoin, sinon elles s'affichent si définies */}
                        {item.icon && (
                          <span className="text-muted-foreground mr-3 size-5">
                            {item.icon}
                          </span>
                        )}
                        {t(item.titleKey)}
                      </Link>
                    ) : null}
                  </div>
                ))}

                <div className="p-4">
                  <LanguageSwitcher inMobileNav={true} />
                </div>

                {isLoggedIn ? (
                  <div className="pt-2">
                    <div className="px-4 py-3">
                      <div className="font-semibold">
                        {user?.firstName} {user?.lastName}
                      </div>
                      <div className="text-muted-foreground text-sm">
                        {user?.email}
                      </div>
                    </div>
                    {filteredUserMenuItems.map(item => (
                      <Link
                        key={item.titleKey}
                        to={item.url}
                        className="hover:bg-accent flex items-center px-4 py-3 font-medium"
                        onClick={() => handleMobileLinkClick(item.url)}
                      >
                        {item.icon && (
                          <span className="text-muted-foreground mr-3 size-5">
                            {item.icon}
                          </span>
                        )}
                        {t(item.titleKey)}
                      </Link>
                    ))}
                    <Link
                      to="/profil"
                      className="hover:bg-accent flex items-center px-4 py-3 font-medium"
                      onClick={() => handleMobileLinkClick('/profil')}
                    >
                      <UserCircle className="text-muted-foreground mr-3 size-5" />
                      {t('header.auth.profile')}
                    </Link>
                    <button
                      onClick={handleLogout}
                      className="hover:bg-accent flex w-full items-center px-4 py-3 text-left font-medium"
                    >
                      <LogOut className="text-muted-foreground mr-3 size-5" />
                      {t('header.auth.logout')}
                    </button>
                  </div>
                ) : (
                  <div className="space-y-2 p-4">
                    <Button asChild className="w-full" size="lg">
                      <Link
                        to="/connexion"
                        onClick={() => handleMobileLinkClick('/connexion')}
                      >
                        {t('header.auth.login')}
                      </Link>
                    </Button>
                    <Button
                      asChild
                      variant="outline"
                      className="w-full"
                      size="lg"
                    >
                      <Link
                        to="/inscription"
                        onClick={() => handleMobileLinkClick('/inscription')}
                      >
                        {t('header.auth.register')}
                      </Link>
                    </Button>
                  </div>
                )}
              </nav>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </header>
  )
}

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
          'focus:bg-accent focus:text-accent-foreground hover:bg-accent hover:text-accent-foreground block space-y-1 rounded-md p-3 leading-none no-underline transition-colors outline-none select-none',
          className
        )}
        {...props}
      >
        <div className="flex items-center gap-2">
          {icon}
          <div className="text-sm leading-none font-medium">{title}</div>
        </div>
        {children && (
          <p className="text-muted-foreground mt-1 line-clamp-2 text-sm leading-snug">
            {children}
          </p>
        )}
      </a>
    </NavigationMenuLink>
  </li>
))
ListItem.displayName = 'ListItem'
