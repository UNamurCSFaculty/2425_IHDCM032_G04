import React, { useEffect, useRef, useCallback } from 'react'
import { useUserStore } from '@/store/userStore'
import { formatPrice } from '@/utils/formatter'
import { client } from '@/api/generated/client.gen.ts'
import { toast } from 'sonner'
import { useNavigate } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'

interface SseNotificationsProviderProps {
  children: React.ReactNode
}

/**
 * Fournisseur de notifications en temps réel via Server-Sent Events (SSE)
 * Gère les notifications pour les nouvelles offres et enchères clôturées
 * Se connecte automatiquement quand un utilisateur est connecté
 */
export const SseNotificationsProvider: React.FC<
  SseNotificationsProviderProps
> = ({ children }) => {
  const user = useUserStore(s => s.user)
  const eventSourceRef = useRef<EventSource | null>(null)
  const notifiedBids = useRef<Set<number>>(new Set())
  const navigate = useNavigate()
  const { t } = useTranslation()

  const showBidNotification = useCallback(
    (newBid: {
      id: number
      amount: number
      auctionId: number
      trader: { firstName: string; lastName: string }
    }) => {
      toast(
        <span>
          <b>💸 {t('notification.new_bid')}</b>
          <br />
          {newBid.trader.firstName} {newBid.trader.lastName} propose{' '}
          <b>{formatPrice.format(newBid.amount)}</b>
        </span>,
        {
          description: t('notification.list_description'),
          duration: 5000,
          action: {
            label: t('notification.see_auction'),
            onClick: () => {
              navigate({
                to: '/ventes/mes-encheres',
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
    [navigate, t]
  )

  const showAuctionClosedNotification = useCallback(
    (auction: { id: number }) => {
      toast(
        <span>
          <b>⏰ {t('notification.closed_auction')}</b>
          <br />
          L’enchère n°{auction.id} est terminée.
        </span>,
        {
          description: t('notification.historic'),
          duration: 5000,
          action: {
            label: t('notification.see_auction'),
            onClick: () => {
              navigate({ to: '/ventes/mes-encheres' })
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
    [navigate, t]
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
        showBidNotification(newBid)
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
        showAuctionClosedNotification(auction)
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
