import { cn } from '@/lib/utils'
import { Loader2 } from 'lucide-react'
import { useLayoutEffect, useState } from 'react'

export function PageLoader() {
  const [visible, setVisible] = useState(false)

  // Au montage, on attend un court délai avant d'activer l'opacité,
  // pour que la transition CSS ait le temps de se préparer.
  useLayoutEffect(() => {
    const id = requestAnimationFrame(() => setVisible(true))
    return () => cancelAnimationFrame(id)
  }, [])

  return (
    <div
      className={cn(
        'bg-base-200 flex h-100 w-screen items-center justify-center bg-green-100/50 transition duration-[2000ms] ease-out',
        visible ? 'opacity-100' : 'opacity-0'
      )}
    >
      <Loader2 size={48} className="text-primary animate-spin" />
    </div>
  )
}
