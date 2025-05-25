import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { createFileRoute } from '@tanstack/react-router'
import { CheckCircle, Mail } from 'lucide-react'
import { useTranslation } from 'react-i18next'

export const Route = createFileRoute('/signup-success')({
  component: SignupSuccessComponent,
})

function SignupSuccessComponent() {
  const { t } = useTranslation()

  return (
    <div className="container mx-auto flex min-h-[calc(100vh-200px)] items-center justify-center px-5 py-24">
      <Card className="w-full max-w-lg shadow-xl">
        <CardHeader className="items-center text-center">
          <div className="flex items-center justify-center">
            <CheckCircle className="mb-4 h-16 w-16 text-green-500" />
          </div>
          <CardTitle className="text-2xl font-bold">
            {t('app.signup_success.title')}
          </CardTitle>
          <CardDescription className="text-md pt-2">
            {t('app.signup_success.description_line1')}
            <br />
            {t('app.signup_success.description_line2')}
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col items-center space-y-6">
          <div className="flex items-center space-x-2 rounded-md border border-yellow-300 bg-yellow-50 p-4 text-yellow-700">
            <Mail className="h-6 w-6" />
            <p className="text-sm">
              {t('app.signup_success.email_notification')}
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
