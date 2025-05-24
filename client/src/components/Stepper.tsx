import React from 'react'
import { useTranslation } from 'react-i18next'

interface StepperProps {
  step: number
  totalSteps?: number
}

const Stepper: React.FC<StepperProps> = ({ step, totalSteps = 3 }) => {
  const { t } = useTranslation()

  // 1) Génération dynamique du tableau des étapes
  const steps = Array.from({ length: totalSteps }, (_, i) => i + 1)

  return (
    <div className="mb-6">
      <div className="flex items-center justify-between">
        {steps.map((stepNumber, idx) => (
          <React.Fragment key={stepNumber}>
            {/* Cercle de l'étape */}
            <div
              className={`flex h-8 w-8 items-center justify-center rounded-full transition-colors duration-300 ${
                step >= stepNumber
                  ? 'bg-primary text-primary-foreground'
                  : 'border-muted bg-background text-muted-foreground border'
              } `}
            >
              {stepNumber}
            </div>

            {/* Barre de progression avec animation */}
            {idx < steps.length - 1 && (
              <div className="bg-muted relative mx-2 h-1 flex-1 overflow-hidden rounded">
                <div
                  className={`bg-primary h-full transition-all duration-300 ${step > idx + 1 ? 'w-full' : 'w-0'} `}
                />
              </div>
            )}
          </React.Fragment>
        ))}
      </div>

      {/* Labels sous chaque étape */}
      <div className="mt-2 flex justify-between text-sm">
        {steps.map(stepNumber => (
          <span
            key={stepNumber}
            className={`transition-colors duration-300 ${
              step >= stepNumber ? 'text-primary' : 'text-muted-foreground'
            }`}
          >
            {`${t('common.step')} ${stepNumber}`}
          </span>
        ))}
      </div>
    </div>
  )
}

export default Stepper
