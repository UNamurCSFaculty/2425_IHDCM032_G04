import z from 'zod/v4'

const HAS_LETTER_OR_AT_REGEX = /[a-zA-Z@]/
const BENIN_PHONE_REGEX = /^(\+229)?01\d{8}$/

export const LoginSchema = z.object({
  username: z.string().superRefine((username, ctx) => {
    if (username.length === 0) {
      ctx.addIssue({
        code: z.ZodIssueCode.too_small,
        minimum: 1,
        type: 'string',
        inclusive: true,
        message: 'validation.required',
        input: username,
        origin: 'string',
      })
      return
    }

    const containsLetterOrAt = HAS_LETTER_OR_AT_REGEX.test(username)

    if (containsLetterOrAt) {
      const emailValidation = z
        .string()
        .email({ message: 'validation.email' })
        .safeParse(username)
      if (!emailValidation.success) {
        emailValidation.error.issues.forEach(issue => ctx.addIssue(issue))
      }
    } else {
      if (!BENIN_PHONE_REGEX.test(username)) {
        ctx.addIssue({
          code: z.ZodIssueCode.invalid_format,
          message: 'validation.phone',
          input: username,
          format: 'phone',
        })
      }
    }
  }),
  password: z.string().min(8),
})
