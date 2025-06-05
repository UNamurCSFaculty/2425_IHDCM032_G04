import { TradeStatus } from '@/lib/utils'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'

interface CountdownTimerProps {
  endDate: Date
  status?: string
}

function calculateTimeLeft(endDate: Date) {
  const nowTime = Date.now()
  const diffSeconds = Math.round((endDate.getTime() - nowTime) / 1000)

  if (diffSeconds <= 0) {
    return {
      days: 0,
      hours: 0,
      minutes: 0,
      seconds: 0,
      difference: 0,
    }
  }

  const nbSecondsPerDay = 24 * 60 * 60
  const nbSecondsPerHour = 60 * 60
  const nbSecondsPerMinute = 60

  const days = Math.floor(diffSeconds / nbSecondsPerDay)
  const hours = Math.floor(
    (diffSeconds - days * nbSecondsPerDay) / nbSecondsPerHour
  )
  const minutes = Math.floor(
    (diffSeconds - days * nbSecondsPerDay - hours * nbSecondsPerHour) /
      nbSecondsPerMinute
  )
  const seconds =
    diffSeconds -
    days * nbSecondsPerDay -
    hours * nbSecondsPerHour -
    minutes * nbSecondsPerMinute

  return {
    days: days,
    hours: hours,
    minutes: minutes,
    seconds: seconds,
    difference: diffSeconds,
  }
}

export function CountdownTimer({
  endDate,
  status = 'Unknown',
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

  if (difference <= 0 || status !== TradeStatus.OPEN) {
    // display only status, without countdown
    return <div className={`font-bold ${statusColor}`}>{getStatusLabel()}</div>
  }

  const isLast24h = difference <= 24 * 60 * 60

  return (
    <div className={isLast24h ? 'font-semibold text-red-500' : 'font-semibold'}>
      {days > 0 && `${days}d `}
      {hours.toString().padStart(2, '0')}:{minutes.toString().padStart(2, '0')}:
      {seconds.toString().padStart(2, '0')}
    </div>
  )
}
