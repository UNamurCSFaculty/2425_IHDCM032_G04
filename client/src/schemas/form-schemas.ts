import z from 'zod/v4'

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

export const ContratSchema = z.object({
  quality: z.string(),
  price: z.number(),
  quantity: z.number(),
  lastingMonth: z.number(),
  lastingYear: z.number(),
})
