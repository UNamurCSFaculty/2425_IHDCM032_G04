'use client'

import { useEffect, useState } from 'react'

interface CountdownTimerProps {
  endDate: Date
}

function calculateTimeLeft(endDate: Date) {
  const difference = endDate.getTime() - Date.now()

  if (difference <= 0) {
    return { days: 0, hours: 0, minutes: 0, seconds: 0, difference: 0 }
  }

  return {
    days: Math.floor(difference / (1000 * 60 * 60 * 24)),
    hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
    minutes: Math.floor((difference / 1000 / 60) % 60),
    seconds: Math.floor((difference / 1000) % 60),
    difference,
  }
}

export function CountdownTimer({ endDate }: CountdownTimerProps) {
  const [timeLeft, setTimeLeft] = useState(() => calculateTimeLeft(endDate))

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft(endDate))
    }, 1000)

    return () => clearInterval(timer)
  }, [endDate])

  const { days, hours, minutes, seconds, difference } = timeLeft

  // Si terminé
  if (difference <= 0) {
    return <div className="font-bold text-red-500">Auction Ended</div>
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
