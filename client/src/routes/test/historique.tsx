import type { AuctionDto } from '@/api/generated'
import { BreadcrumbSection } from '@/components/BreadcrumbSection'
import { SellerAuctions } from '@/components/test/Auctions/SellerAuctions'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/test/historique')({
  component: RouteComponent,
})

const language = { id: 1, code: 'fr', name: 'Français' }

const auctions: AuctionDto[] = [
  {
    id: 1,
    expirationDate: '2025-10-15T12:00:00Z',
    active: true,
    price: 200000,
    productQuantity: 5,
    trader: {
      id: 1,
      type: 'exporter',
      firstName: 'Jean',
      lastName: 'Louis',
      email: 'jeanlou@gmail.com',
      phone: '+32400000000',
      language: language,
    },
    status: { id: 1, name: 'En cours' },
    creationDate: '2025-10-01T12:00:00Z',
    product: {
      id: 1,
      type: 'harvest',
      store: { id: 2, name: 'Chez Stéphane', location: 'Dallas', userId: 1 },
      weightKg: 20000,
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
        creationDate: '2025-10-01T12:00:00Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 5000,
        creationDate: '2025-10-01T12:00:10Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 12000,
        creationDate: '2025-10-01T12:00:20Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 20000,
        creationDate: '2025-10-01T12:00:60Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 25000,
        creationDate: '2025-10-01T12:00:60Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 30220,
        creationDate: '2025-10-01T12:00:60Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
    ],
  },
  {
    id: 1,
    expirationDate: '2025-10-15T12:00:00Z',
    active: true,
    price: 100000,
    productQuantity: 5,
    trader: {
      id: 1,
      type: 'exporter',
      firstName: 'Jean',
      lastName: 'Louis',
      email: 'jeanlou@gmail.com',
      phone: '+32400000000',
      language: language,
    },
    status: { id: 1, name: 'En cours' },
    creationDate: '2025-10-01T12:00:00Z',
    product: {
      id: 1,
      type: 'harvest',
      weightKg: 10000,
      store: { id: 2, name: 'Chez Fab', location: 'Bali', userId: 1 },
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
        creationDate: '2025-10-01T12:00:00Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        creationDate: '2025-10-01T12:00:10Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        creationDate: '2025-10-01T12:00:20Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        creationDate: '2025-10-01T12:00:60Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
    ],
  },
  {
    id: 1,
    expirationDate: '2025-10-15T12:00:00Z',
    active: true,
    price: 10000,
    productQuantity: 5,
    trader: {
      id: 1,
      type: 'exporter',
      firstName: 'Jean',
      lastName: 'Louis',
      email: 'jeanlou@gmail.com',
      phone: '+32400000000',
      language: language,
    },
    status: { id: 1, name: 'En cours' },
    creationDate: '2025-10-01T12:00:00Z',
    product: {
      id: 1,
      type: 'harvest',
      weightKg: 1000,
      store: { id: 2, name: 'Chez Zak', location: 'Liège', userId: 1 },
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
        creationDate: '2025-10-01T12:00:00Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        creationDate: '2025-10-01T12:00:10Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        creationDate: '2025-10-01T12:00:20Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
      {
        amount: 1000,
        creationDate: '2025-10-01T12:00:60Z',
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
          language: language,
        },
        auctionId: 1,
        id: 1,
      },
    ],
  },
]

function RouteComponent() {
  return (
    <>
      <BreadcrumbSection
        titleKey="app.signup.title"
        subtitleKey="app.signup.subtitle"
        breadcrumbs={[{ labelKey: 'breadcrumb.contact' }]}
        className="border-b border-gray-200 dark:border-gray-700"
      />
      <div className="container mx-auto px-4 py-8">
        <SellerAuctions auctions={auctions} />
      </div>
    </>
  )
}
