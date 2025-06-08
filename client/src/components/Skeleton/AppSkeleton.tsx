import { ContentSkeleton } from '../Skeleton/../Skeleton/ContentSkeleton'
import { Skeleton } from '../ui/skeleton'

/**
 * Un composant de squelette pour l'application, utilisé pour afficher un état de chargement.
 */
export function AppSkeleton() {
  return (
    <div className="">
      <Skeleton className="radius mb-10 h-25 w-full" />

      <Skeleton className="radius mb-20 h-100 w-full" />
      <ContentSkeleton />
    </div>
  )
}
