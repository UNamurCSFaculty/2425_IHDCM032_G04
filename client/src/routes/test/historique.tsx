import type { AuctionDtoReadable } from '@/api/generated'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { SellerAuctions } from '@/components/test/Auctions/SellerAuctions'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/test/historique')({
  component: RouteComponent,
})

const auctions: AuctionDtoReadable[] = [
  {
    id: 1,
    expirationDate: '2025-10-15T12:00:00Z',
    active: true,
    price: 200000,
    productQuantity: 5,
    product: {
      id: 1,
      type: 'harvest',
      store: { location: 'Dallas', userId: 1 },
      producer: {
        id: 1,
        email: 'fabricecipolla@gmail.com',
        type: 'producer',
        firstName: 'Fabrice',
        lastName: 'Cipolla',
        phone: '0612345678',
        language: { id: 1, code: 'fr', name: 'Français' },
        agriculturalIdentifier: '123456789',
      },
      field: { id: 1, location: 'Texas' },
    },

    strategy: {
      id: 1,
      name: 'Strategy 1',
    },
    bids: [
      {
        amount: 1000,
        auctionDate: '2025-10-01T12:00:00Z',
        status: {
          id: 1,
          name: 'Pending',
        },
        trader: {
          id: 1,
          type: 'exporter',
          firstName: 'Jean',
          lastName: 'Louis',
          email: 'jeanLouis@gmail.com',
          phone: '0612345678',
          language: { id: 1, code: 'fr', name: 'Français' },
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        auctionDate: '2025-10-01T12:00:00Z',
        status: {
          id: 1,
          name: 'Pending',
        },
        trader: {
          id: 1,
          type: 'exporter',
          firstName: 'Jean',
          lastName: 'Louis',
          email: 'jeanLouis@gmail.com',
          phone: '0612345678',
          language: { id: 1, code: 'fr', name: 'Français' },
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        auctionDate: '2025-10-01T12:00:00Z',
        status: {
          id: 1,
          name: 'Pending',
        },
        trader: {
          id: 1,
          type: 'exporter',
          firstName: 'Jean',
          lastName: 'Louis',
          email: 'jeanLouis@gmail.com',
          phone: '0612345678',
          language: { id: 1, code: 'fr', name: 'Français' },
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        auctionDate: '2025-10-01T12:00:00Z',
        status: {
          id: 1,
          name: 'Pending',
        },
        trader: {
          id: 1,
          type: 'exporter',
          firstName: 'Jean',
          lastName: 'Louis',
          email: 'jeanLouis@gmail.com',
          phone: '0612345678',
          language: { id: 1, code: 'fr', name: 'Français' },
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        auctionDate: '2025-10-01T12:00:00Z',
        status: {
          id: 1,
          name: 'Pending',
        },
        trader: {
          id: 1,
          type: 'exporter',
          firstName: 'Jean',
          lastName: 'Louis',
          email: 'jeanLouis@gmail.com',
          phone: '0612345678',
          language: { id: 1, code: 'fr', name: 'Français' },
        },
        auctionId: 1,
        id: 1,
      },
    ],
  },
  {
    id: 2,
    expirationDate: '2026-01-15T12:00:00Z',
    active: true,
    price: 100000,
    productQuantity: 2,
    product: {
      id: 1,
      type: 'harvest',
      store: { location: 'Paris', userId: 1 },
      producer: {
        id: 1,
        email: 'fabricecipolla@gmail.com',
        type: 'producer',
        firstName: 'Fabrice',
        lastName: 'Cipolla',
        phone: '0612345678',
        language: { id: 1, code: 'fr', name: 'Français' },
        agriculturalIdentifier: '123456789',
      },
      field: { id: 1, location: 'Texas' },
    },
    strategy: {
      id: 1,
      name: 'Strategy 1',
    },
    bids: [],
  },
  {
    id: 3,
    expirationDate: '2026-01-15T12:00:00Z',
    active: true,
    price: 100000,
    productQuantity: 2,
    product: {
      id: 1,
      type: 'harvest',
      store: { location: 'Paris', userId: 1 },
      producer: {
        id: 1,
        email: 'fabricecipolla@gmail.com',
        type: 'producer',
        firstName: 'Fabrice',
        lastName: 'Cipolla',
        phone: '0612345678',
        language: { id: 1, code: 'fr', name: 'Français' },
        agriculturalIdentifier: '123456789',
      },
      field: { id: 1, location: 'Texas' },
    },
    strategy: {
      id: 1,
      name: 'Strategy 1',
    },
    bids: [],
  },
  {
    id: 4,
    expirationDate: '2026-01-15T12:00:00Z',
    active: true,
    price: 100000,
    productQuantity: 2,
    product: {
      id: 1,
      type: 'harvest',
      store: { location: 'Paris', userId: 1 },
      producer: {
        id: 1,
        email: 'fabricecipolla@gmail.com',
        type: 'producer',
        firstName: 'Fabrice',
        lastName: 'Cipolla',
        phone: '0612345678',
        language: { id: 1, code: 'fr', name: 'Français' },
        agriculturalIdentifier: '123456789',
      },
      field: { id: 1, location: 'Texas' },
    },
    strategy: {
      id: 1,
      name: 'Strategy 1',
    },
    bids: [],
  },
  {
    id: 5,
    expirationDate: '2026-01-15T12:00:00Z',
    active: true,
    price: 100000,
    productQuantity: 2,
    product: {
      id: 1,
      type: 'harvest',
      store: { location: 'Paris', userId: 1 },
      producer: {
        id: 1,
        email: 'fabricecipolla@gmail.com',
        type: 'producer',
        firstName: 'Fabrice',
        lastName: 'Cipolla',
        phone: '0612345678',
        language: { id: 1, code: 'fr', name: 'Français' },
        agriculturalIdentifier: '123456789',
      },
      field: { id: 1, location: 'Texas' },
    },
    strategy: {
      id: 1,
      name: 'Strategy 1',
    },
    bids: [],
  },
  {
    id: 6,
    expirationDate: '2026-01-15T12:00:00Z',
    active: true,
    price: 100000,
    productQuantity: 2,
    product: {
      id: 1,
      type: 'harvest',
      store: { location: 'Paris', userId: 1 },
      producer: {
        id: 1,
        email: 'fabricecipolla@gmail.com',
        type: 'producer',
        firstName: 'Fabrice',
        lastName: 'Cipolla',
        phone: '0612345678',
        language: { id: 1, code: 'fr', name: 'Français' },
        agriculturalIdentifier: '123456789',
      },
      field: { id: 1, location: 'Texas' },
    },
    strategy: {
      id: 1,
      name: 'Strategy 1',
    },
    bids: [],
  },
]

function RouteComponent() {
  return (
    <>
      <BreadcrumbSection
        titleKey="app.signup.titre"
        subtitleKey="app.signup.sous_titre"
        breadcrumbs={[{ labelKey: 'breadcrumb.contact' }]}
        className="border-b border-gray-200 dark:border-gray-700"
      />
      <div className="container mx-auto px-4 py-8">
        <SellerAuctions auctions={auctions} />
      </div>
    </>
  )
}
