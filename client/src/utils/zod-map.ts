import i18n from '../i18n'
import { type ZodErrorMap } from 'zod'

export const customErrorMap: ZodErrorMap = issue => {
  // console.log(issue, issue.message, issue.code)
  switch (issue.code) {
    case 'too_small': {
      const min = issue.minimum as number
      if (min === 1) return { message: i18n.t('validation.required') }
      return { message: i18n.t('validation.minLength', { count: min }) }
    }
    case 'too_big': {
      const max = issue.maximum as number
      return { message: i18n.t('validation.maxLength', { count: max }) }
    }
    case 'invalid_format': {
      // issue.validation est "email" | "url" | …
      if (issue.format === 'regex')
        return { message: i18n.t(`validation.${issue.path}`) }
      return { message: i18n.t(`validation.${issue.format}`) }
    }
    case 'custom': {
      return { message: i18n.t(`validation.${issue.path}`) }
    }
    default:
      return { message: 'An error occurred' }
  }
}
