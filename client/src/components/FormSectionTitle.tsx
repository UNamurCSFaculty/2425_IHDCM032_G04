import React from 'react'

/**
 * Composant React pour afficher un titre de section dans un formulaire.
 */
const FormSectionTitle: React.FC<{ text: string }> = ({ text }) => {
  return (
    <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
      <span className="bg-background text-muted-foreground relative z-10 px-2">
        {text}
      </span>
    </div>
  )
}

export default FormSectionTitle
