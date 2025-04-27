import { z, ZodIssueCode, type ZodErrorMap } from 'zod'
import i18n from '../i18n'

export const customErrorMap: ZodErrorMap = issue => {
  console.log(issue, issue.message, issue.code)
  switch (issue.code) {
    case 'too_small': {
      const min = issue.minimum as number
      return { message: i18n.t('validation.minLength', { count: min }) }
    }
    case 'too_big': {
      const max = issue.maximum as number
      return { message: i18n.t('validation.maxLength', { count: max }) }
    }
    case 'invalid_format': {
      // issue.validation est "email" | "url" | …
      return { message: i18n.t(`validation.${issue.validation}`) }
    }
    default:
      return { message: 'Error Occurred' }
  }
}
