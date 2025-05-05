import { Skeleton } from '../ui/skeleton'

export function ContentSkeleton() {
  return (
    <div className="mx-auto max-w-7xl space-y-6 p-4">
      {/* En-tête */}
      <Skeleton className="h-8 w-1/3 rounded-full" />
      <Skeleton className="h-6 w-1/4 rounded" />

      {/* Grille d'enchères */}
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: 3 }).map((_, idx) => (
          <div key={idx} className="space-y-4">
            <Skeleton className="h-48 w-full rounded-lg" />
            <Skeleton className="h-4 w-3/4 rounded" />
            <Skeleton className="h-6 w-1/2 rounded-full" />
          </div>
        ))}
      </div>
    </div>
  )
}
