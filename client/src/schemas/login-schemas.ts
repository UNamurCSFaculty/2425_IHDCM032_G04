import z from 'zod/v4'

export const LoginSchema = z.object({
  username: z.email(),
  password: z.string().min(8),
})
