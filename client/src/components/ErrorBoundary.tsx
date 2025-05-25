import { Button } from './ui/button'
import { Link } from '@tanstack/react-router'
import { Component, type ErrorInfo, type ReactNode } from 'react'
import { withTranslation, type WithTranslation } from 'react-i18next'

interface ErrorBoundaryProps extends WithTranslation {
  children: ReactNode
}

interface ErrorBoundaryState {
  hasError: boolean
}

class ErrorBoundaryComponent extends Component<
  ErrorBoundaryProps,
  ErrorBoundaryState
> {
  constructor(props: ErrorBoundaryProps) {
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
    const { t } = this.props
    if (this.state.hasError) {
      return (
        <main className="grid min-h-full place-items-center bg-white px-6 py-24 sm:py-32 lg:px-8">
          <div className="text-center">
            <h1 className="text-3xl font-semibold text-red-600">
              {t('error_boundary.title')}
            </h1>
            <p className="mt-4 text-lg text-gray-500">
              {t('error_boundary.message')}
            </p>
            <div className="mt-6">
              <Link to="/">
                <Button size="lg">{t('error_boundary.button_home')}</Button>
              </Link>
            </div>
          </div>
        </main>
      )
    }
    return this.props.children
  }
}

export const ErrorBoundary = withTranslation()(ErrorBoundaryComponent)
