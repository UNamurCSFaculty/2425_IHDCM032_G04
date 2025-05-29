import React, { useEffect, useRef, useCallback } from 'react'
import { useUserStore } from '@/store/userStore'
import { formatPrice } from '@/utils/formatter'
import { client } from '@/api/generated/client.gen.ts'
import { toast } from 'sonner'
import { useNavigate } from '@tanstack/react-router'

interface SseNotificationsProviderProps {
  children: React.ReactNode
}

export const SseNotificationsProvider: React.FC<
  SseNotificationsProviderProps
> = ({ children }) => {
  const user = useUserStore(s => s.user)
  const eventSourceRef = useRef<EventSource | null>(null)
  const notifiedBids = useRef<Set<number>>(new Set())
  const navigate = useNavigate() // useNavigate is called inside the component

  // Define notification handlers inside the component and memoize them
  const showBidNotification = useCallback(
    (newBid: {
      id: number
      amount: number
      auctionId: number
      trader: { firstName: string; lastName: string }
    }) => {
      toast(
        <span>
          <b>💸 Nouvelle offre :</b>
          <br />
          {newBid.trader.firstName} {newBid.trader.lastName} propose{' '}
          <b>{formatPrice.format(newBid.amount)}</b>
        </span>,
        {
          description: 'Voir la liste des offres pour plus de détails.',
          duration: 10000,
          action: {
            label: 'Voir l’enchère',
            onClick: () => {
              navigate({
                to: '/achats/marche',
              })
              setTimeout(() => {
                window.dispatchEvent(
                  new CustomEvent('auction:showInlineAuction', {
                    detail: { auctionId: newBid.auctionId },
                  })
                )
              }, 500)
            },
          },
        }
      )
    },
    [navigate]
  )

  const showAuctionClosedNotification = useCallback(
    (auction: { id: number }) => {
      toast(
        <span>
          <b>⏰ Enchère clôturée</b>
          <br />
          L’enchère n°{auction.id} est terminée.
        </span>,
        {
          description: 'Consultez l’historique pour plus de détails.',
          duration: 10000,
          action: {
            label: 'Voir l’enchère',
            onClick: () => {
              // navigate is now accessible from the closure
              navigate({ to: '/achats/marche' })
              setTimeout(() => {
                window.dispatchEvent(
                  new CustomEvent('auction:showInlineAuction', {
                    detail: { auctionId: auction.id },
                  })
                )
              }, 500)
            },
          },
        }
      )
    },
    [navigate]
  )

  useEffect(() => {
    if (!user?.id) {
      if (eventSourceRef.current) {
        eventSourceRef.current.close()
        eventSourceRef.current = null
      }
      return
    }
    const baseUrl = client.getConfig().baseUrl?.replace(/\/$/, '') ?? ''
    const sseUrl = `${baseUrl}/api/notifications/stream`
    const es = new window.EventSource(sseUrl, { withCredentials: true })
    eventSourceRef.current = es

    es.addEventListener('newBid', evt => {
      try {
        const newBid = JSON.parse((evt as MessageEvent).data)
        if (notifiedBids.current.has(newBid.id)) return
        notifiedBids.current.add(newBid.id)
        showBidNotification(newBid) // Call the memoized function
        // Toujours notifier pour le rafraîchissement des données
        window.dispatchEvent(
          new CustomEvent('auction:newBid', {
            detail: { auctionId: newBid.auctionId },
          })
        )
      } catch (err) {
        console.error('[SSE] Erreur:', err, evt)
      }
    })

    es.addEventListener('auctionClosed', evt => {
      try {
        const auction = JSON.parse((evt as MessageEvent).data)
        showAuctionClosedNotification(auction) // Call the memoized function
      } catch (err) {
        console.error('[SSE] Erreur auctionClosed:', err, evt)
      }
    })

    es.onerror = err => {
      console.error('[SSE] Erreur EventSource', err)
    }

    return () => {
      es.close()
      eventSourceRef.current = null
    }
  }, [user?.id, showBidNotification, showAuctionClosedNotification])

  return <>{children}</>
}
