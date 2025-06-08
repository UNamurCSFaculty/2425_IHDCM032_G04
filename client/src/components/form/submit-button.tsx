import { useFormContext } from '.'
import { Button } from '../ui/button'
import { useStore } from '@tanstack/react-form'
import { LoaderCircle } from 'lucide-react'

type SubmitButtonProps = {
  children: React.ReactNode
} & React.ButtonHTMLAttributes<HTMLButtonElement>

/**
 *
 * Composant de bouton de soumission pour les formulaires.
 * Affiche un indicateur de chargement pendant la soumission.
 */
export const SubmitButton = ({ children, ...props }: SubmitButtonProps) => {
  const form = useFormContext()

  const [isSubmitting, canSubmit] = useStore(form.store, state => [
    state.isSubmitting,
    state.canSubmit,
  ])

  return (
    <Button type="submit" disabled={isSubmitting || !canSubmit} {...props}>
      {isSubmitting ? <LoaderCircle className="animate-spin" /> : children}
    </Button>
  )
}
