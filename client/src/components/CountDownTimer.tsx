import { TradeStatus } from '@/lib/utils'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

interface CountdownTimerProps {
  endDate: Date
  status?: string
}

function calculateTimeLeft(endDate: Date) {
  const difference = endDate.getTime() - Date.now()

  if (difference <= 0) {
    return {
      days: 0,
      hours: 0,
      minutes: 0,
      seconds: 0,
      milliseconds: 0,
      difference: 0,
    }
  }

  return {
    days: Math.floor(difference / (1000 * 60 * 60 * 24)),
    hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
    minutes: Math.floor((difference / 1000 / 60) % 60),
    seconds: Math.floor((difference / 1000) % 60),
    milliseconds: Math.floor(difference % 1000),
    difference,
  }
}

export function CountdownTimer({
  endDate,
  status = 'Expiré',
}: CountdownTimerProps) {
  const { t } = useTranslation()
  const [timeLeft, setTimeLeft] = useState(() => calculateTimeLeft(endDate))

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft(endDate))
    }, 100)

    return () => clearInterval(timer)
  }, [endDate])

  const { days, hours, minutes, seconds, difference } = timeLeft

  const statusColor =
    status === TradeStatus.ACCEPTED ? 'text-green-600' : 'text-red-500'

  const getStatusLabel = () => {
    if (status === TradeStatus.ACCEPTED) {
      return t('auction.status_accepted')
    } else if (status === TradeStatus.REJECTED) {
      return t('auction.status_rejected')
    } else if (status === TradeStatus.EXPIRED) {
      return t('auction.status_expired')
    } else if (status === TradeStatus.OPEN) {
      return t('auction.status_open')
    } else {
      return t('auction.status_unknown')
    }
  }

  if (difference <= 0) {
    return <div className={`font-bold ${statusColor}`}>{getStatusLabel()}</div>
  }

  const isLast24h = difference <= 24 * 60 * 60 * 1000

  return (
    <div className={isLast24h ? 'font-semibold text-red-500' : 'font-semibold'}>
      {days > 0 && `${days}d `}
      {hours.toString().padStart(2, '0')}:{minutes.toString().padStart(2, '0')}:
      {seconds.toString().padStart(2, '0')}
    </div>
  )
}
