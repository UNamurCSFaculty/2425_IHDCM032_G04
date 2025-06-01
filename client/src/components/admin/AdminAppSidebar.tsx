import logo from '@/assets/logo.svg'
import { NavMain } from '@/components/admin/AdminNavMain'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar'
import {
  IconChartBar,
  IconDashboard,
  IconDatabase,
  IconFileWord,
  IconHelp,
  IconNews,
  IconReport,
  IconSearch,
  IconSettings,
  IconUsers,
  IconBuilding,
} from '@tabler/icons-react'
import { Link } from '@tanstack/react-router'
import * as React from 'react'
import { Button } from '../ui/button'
import { LogOut } from 'lucide-react'

const data = {
  user: {
    name: 'shadcn',
    email: 'm@example.com',
    avatar: '/avatars/shadcn.jpg',
  },
  navMain: [
    {
      title: 'Dashboard',
      url: '/admin',
      icon: IconDashboard,
    },
    {
      title: 'Utilisateurs',
      url: '/admin/users',
      icon: IconUsers,
    },
    {
      title: 'Blog',
      url: '/admin/blog',
      icon: IconNews,
    },
    {
      title: 'Analyse Business',
      url: '/admin/analytics',
      icon: IconChartBar,
    },
    {
      title: 'Coopératives',
      url: '/admin/cooperatives',
      icon: IconBuilding,
    },
    {
      title: 'Paramètres',
      url: '/admin/global-settings',
      icon: IconSettings,
    },
  ],

  navSecondary: [
    {
      title: 'Settings',
      url: '#',
      icon: IconSettings,
    },
    {
      title: 'Get Help',
      url: '#',
      icon: IconHelp,
    },
    {
      title: 'Search',
      url: '#',
      icon: IconSearch,
    },
  ],
  documents: [
    {
      name: 'Data Library',
      url: '#',
      icon: IconDatabase,
    },
    {
      name: 'Reports',
      url: '#',
      icon: IconReport,
    },
    {
      name: 'Word Assistant',
      url: '#',
      icon: IconFileWord,
    },
  ],
}

export function AdminAppSidebar({
  ...props
}: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="offcanvas" {...props}>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton
              asChild
              className="relative h-20 data-[slot=sidebar-menu-button]:!p-1.5"
            >
              <Link
                to="/admin"
                className="flex flex-wrap items-center justify-center gap-2"
              >
                <img src={logo} alt="Logo e‑Anacarde" className="" />
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarFooter>
        <div className="border-t p-4">
          <Button variant="outline" className="w-full justify-start" asChild>
            <Link to="/">
              <LogOut className="mr-2 h-4 w-4" />
              Quitter
            </Link>
          </Button>
        </div>
      </SidebarFooter>
    </Sidebar>
  )
}
