import { z } from 'zod'
export const ContactSchema = z.object({
  name: z.string().min(3),
  email: z.email(),
  message: z.string().min(10).max(500),
})

export const ContactSchemaDefaultValues = {
  name: '',
  email: '',
  message: '',
}
