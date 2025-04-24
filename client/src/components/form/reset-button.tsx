import { useFormContext } from '.'
import { Button } from '../ui/button'

type ResetButtonProps = {
  children: React.ReactNode
} & React.ButtonHTMLAttributes<HTMLButtonElement>

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
