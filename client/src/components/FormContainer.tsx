import { Card, CardContent } from './ui/card'
import React from 'react'

interface FormContainerProps {
  title: string
  children: React.ReactNode
}

const FormContainer: React.FC<FormContainerProps> = ({ title, children }) => {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-screen-lg">
        <div className="flex flex-col gap-6">
          <Card className="overflow-hidden">
            <CardContent>
              <section className="body-font relative text-gray-600">
                <h2 className="text-2xl font-bold">{title}</h2>
                <div className="container mx-auto px-5 py-10">{children}</div>
              </section>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}

export default FormContainer
