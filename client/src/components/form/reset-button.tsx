import { useFormContext } from '.'
import { Button } from '../ui/button'

type ResetButtonProps = {
  children: React.ReactNode
} & React.ButtonHTMLAttributes<HTMLButtonElement>

/**
 * Composant de bouton de réinitialisation pour les formulaires.
 * Permet de réinitialiser les valeurs du formulaire aux valeurs par défaut.
 */
export const ResetButton = ({ children }: ResetButtonProps) => {
  const form = useFormContext()

  return (
    <Button
      type="button"
      variant="outline"
      onClick={e => {
        e.preventDefault()
        form.reset()
      }}
    >
      {children}
    </Button>
  )
}
