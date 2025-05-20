import {
  zCooperativeDto,
  zProductUpdateDto,
  zUserUpdateDto,
} from '@/api/generated/zod.gen'
import z from 'zod'

const zProducer = zUserUpdateDto.extend({
  type: z.literal('producer'),
  agriculturalIdentifier: z.string().min(1),
  cooperative: zCooperativeDto.optional(),
})

const zAdmin = zUserUpdateDto.extend({
  type: z.literal('admin'),
})

const zTransformer = zUserUpdateDto.extend({
  type: z.literal('transformer'),
})

const zCarrier = zUserUpdateDto.extend({
  type: z.literal('carrier'),
  pricePerKm: z.number().positive(),
  regionIds: z.array(z.number().int()),
})

const zExporter = zUserUpdateDto.extend({
  type: z.literal('exporter'),
})

const zQualityInspector = zUserUpdateDto.extend({
  type: z.literal('quality_inspector'),
})

export const zUser = z.discriminatedUnion('type', [
  zProducer,
  zAdmin,
  zTransformer,
  zCarrier,
  zExporter,
  zQualityInspector,
])

const zHarvestProduct = zProductUpdateDto.extend({
  type: z.literal('harvest'),
  producerId: z.number().int(),
  fieldId: z.number().int(),
})

const zTransformedProduct = zProductUpdateDto.extend({
  type: z.literal('transformed'),
  identifier: z.string().min(1),
  transformerId: z.number().int(),
})

export const zProduct = z.discriminatedUnion('type', [
  zHarvestProduct,
  zTransformedProduct,
])
