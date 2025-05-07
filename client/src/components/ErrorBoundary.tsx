import { Button } from './ui/button'
import { Link } from '@tanstack/react-router'
import { Component, type ErrorInfo, type ReactNode } from 'react'

export class ErrorBoundary extends Component<
  { children: ReactNode },
  { hasError: boolean }
> {
  constructor(props: { children: ReactNode }) {
    super(props)
    this.state = { hasError: false }
  }

  static getDerivedStateFromError(): { hasError: boolean } {
    return { hasError: true }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('ErrorBoundary caught an error', error, info)
  }

  render() {
    if (this.state.hasError) {
      return (
        <main className="grid min-h-full place-items-center bg-white px-6 py-24 sm:py-32 lg:px-8">
          <div className="text-center">
            <h1 className="text-3xl font-semibold text-red-600">
              Une erreur est survenue
            </h1>
            <p className="mt-4 text-lg text-gray-500">
              Désolé, un problème est survenu. Veuillez réessayer plus tard ou
              retourner à l’accueil.
            </p>
            <div className="mt-6">
              <Link to="/">
                <Button size="lg">Retour à l’accueil</Button>
              </Link>
            </div>
          </div>
        </main>
      )
    }
    return this.props.children
  }
}
