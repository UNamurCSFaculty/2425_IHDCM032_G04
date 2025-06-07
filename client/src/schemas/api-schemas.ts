import {
  zCooperativeDto,
  zProductUpdateDto as zGeneratedProductUpdateDto,
  zUserCreateDto as zGeneratedUserCreateDto,
  zUserListDto as zGeneratedUserListDto,
  zUserUpdateDto as zGeneratedUserUpdateDto,
  zUserDetailDto as zGeneratedUserDetailDto,
} from '@/api/generated/zod.gen'
import z from 'zod/v4'

// --- User Create Schemas ---
const zUserCreateDtoBase = zGeneratedUserCreateDto.omit({ type: true })

const zCreateProducer = zUserCreateDtoBase.extend({
  type: z.literal('producer'),
  agriculturalIdentifier: z.string().min(1),
  cooperative: zCooperativeDto.optional(),
})

const zCreateAdmin = zUserCreateDtoBase.extend({
  type: z.literal('admin'),
})

const zCreateTransformer = zUserCreateDtoBase.extend({
  type: z.literal('transformer'),
})

const zCreateTrader = zUserCreateDtoBase.extend({
  type: z.literal('trader'),
})

const zCreateCarrier = zUserCreateDtoBase.extend({
  type: z.literal('carrier'),
  pricePerKm: z.number().min(1),
  radius: z.number().min(1),
})

const zCreateExporter = zUserCreateDtoBase.extend({
  type: z.literal('exporter'),
})

const zCreateQualityInspector = zUserCreateDtoBase.extend({
  type: z.literal('quality_inspector'),
})

export const zAppCreateUser = z.discriminatedUnion('type', [
  zCreateProducer,
  zCreateAdmin,
  zCreateTransformer,
  zCreateCarrier,
  zCreateExporter,
  zCreateQualityInspector,
  zCreateTrader,
])
export type AppCreateUserDto = z.infer<typeof zAppCreateUser>

// --- User Update Schemas ---
const zUserUpdateDtoBase = zGeneratedUserUpdateDto.omit({ type: true })

const zUpdateProducer = zUserUpdateDtoBase.extend({
  type: z.literal('producer'),
  agriculturalIdentifier: z.string().min(1),
  cooperative: zCooperativeDto.optional(),
})

const zUpdateAdmin = zUserUpdateDtoBase.extend({
  type: z.literal('admin'),
})

const zUpdateTransformer = zUserUpdateDtoBase.extend({
  type: z.literal('transformer'),
})

const zUpdateCarrier = zUserUpdateDtoBase.extend({
  type: z.literal('carrier'),
  pricePerKm: z.number().min(1),
  radius: z.number().min(1),
})

const zUpdateExporter = zUserUpdateDtoBase.extend({
  type: z.literal('exporter'),
})

const zUpdateQualityInspector = zUserUpdateDtoBase.extend({
  type: z.literal('quality_inspector'),
})

const zUpdateTrader = zUserUpdateDtoBase.extend({
  type: z.literal('trader'),
})

export const zAppUpdateUser = z.discriminatedUnion('type', [
  zUpdateProducer,
  zUpdateAdmin,
  zUpdateTransformer,
  zUpdateCarrier,
  zUpdateExporter,
  zUpdateQualityInspector,
  zUpdateTrader,
])
export type AppUpdateUserDto = z.infer<typeof zAppUpdateUser>

// --- Product Schemas (assuming for Update context based on zGeneratedProductUpdateDto) ---
const zProductUpdateDtoBase = zGeneratedProductUpdateDto.omit({ type: true })

const zHarvestProduct = zProductUpdateDtoBase.extend({
  type: z.literal('harvest'),
  producerId: z.number().int(),
  fieldId: z.number().int(),
})

const zTransformedProduct = zProductUpdateDtoBase.extend({
  type: z.literal('transformed'),
  identifier: z.string().min(1),
  transformerId: z.number().int(),
})

export const zAppProduct = z.discriminatedUnion('type', [
  zHarvestProduct,
  zTransformedProduct,
])
export type AppProductDto = z.infer<typeof zAppProduct>

// --- User List Schemas ---
const zAppUserListDtoBase = zGeneratedUserListDto.omit({ type: true })

const zAdminAppListDtoSchema = zAppUserListDtoBase.extend({
  type: z.literal('admin'),
})

const zProducerAppListDtoSchema = zAppUserListDtoBase.extend({
  type: z.literal('producer'),
  agriculturalIdentifier: z.string(),
  cooperative: zCooperativeDto,
})

const zTransformerAppListDtoSchema = zAppUserListDtoBase.extend({
  type: z.literal('transformer'),
})

const zQualityInspectorAppListDtoSchema = zAppUserListDtoBase.extend({
  type: z.literal('quality_inspector'),
})

const zExporterAppListDtoSchema = zAppUserListDtoBase.extend({
  type: z.literal('exporter'),
})

const zCarrierAppListDtoSchema = zAppUserListDtoBase.extend({
  type: z.literal('carrier'),
  pricePerKm: z.number(),
  radius: z.number(),
})

const zTraderAppListDtoSchema = zAppUserListDtoBase.extend({
  type: z.literal('trader'),
})

export const zAppUserList = z.discriminatedUnion('type', [
  zAdminAppListDtoSchema,
  zProducerAppListDtoSchema,
  zTransformerAppListDtoSchema,
  zQualityInspectorAppListDtoSchema,
  zExporterAppListDtoSchema,
  zCarrierAppListDtoSchema,
  zTraderAppListDtoSchema,
])
export type AppUserListDto = z.infer<typeof zAppUserList>

// --- User Detail Schemas ---
const zUserDetailDtoBase = zGeneratedUserDetailDto.omit({
  type: true,
})

const zAdminDetailDtoSchema = zUserDetailDtoBase.extend({
  type: z.literal('admin'),
})

const zProducerDetailDtoSchema = zUserDetailDtoBase.extend({
  type: z.literal('producer'),
  agriculturalIdentifier: z.string().nullable().optional(),
  cooperative: zCooperativeDto.nullable().optional(),
})

const zTransformerDetailDtoSchema = zUserDetailDtoBase.extend({
  type: z.literal('transformer'),
})

const zQualityInspectorDetailDtoSchema = zUserDetailDtoBase.extend({
  type: z.literal('quality_inspector'),
})

const zExporterDetailDtoSchema = zUserDetailDtoBase.extend({
  type: z.literal('exporter'),
})

const zCarrierDetailDtoSchema = zUserDetailDtoBase.extend({
  type: z.literal('carrier'),
  pricePerKm: z.number().nullable().optional(),
  radius: z.number().nullable().optional(),
})

const zTraderDetailDtoSchema = zUserDetailDtoBase.extend({
  type: z.literal('trader'),
})

export const zAppUserDetail = z.discriminatedUnion('type', [
  zAdminDetailDtoSchema,
  zProducerDetailDtoSchema,
  zTransformerDetailDtoSchema,
  zQualityInspectorDetailDtoSchema,
  zExporterDetailDtoSchema,
  zCarrierDetailDtoSchema,
  zTraderDetailDtoSchema,
])
export type AppUserDetailDto = z.infer<typeof zAppUserDetail>

// Fonction de validation pour la force du mot de passe
export const passwordStrengthValidationFn = (data: {
  password?: string | null
}): boolean => {
  if (!data.password) {
    return true
  }

  if (!/[A-Z]/.test(data.password)) return false
  if (!/[a-z]/.test(data.password)) return false
  if (!/[0-9]/.test(data.password)) return false
  if (!/[^A-Za-z0-9]/.test(data.password)) return false

  return true
}

// Configuration pour le refine de la force du mot de passe
export const passwordStrengthRefineConfig = {
  message: 'validation.password',
  path: ['password'],
}

// Fonction de validation conditionnelle pour la longueur minimale du mot de passe
export const conditionalMinLengthValidationFn = (data: {
  password?: string | null
}): boolean => {
  if (!data.password || data.password.length === 0) {
    return true
  }
  return data.password.length >= 8
}

// Configuration pour le refine de la longueur minimale du mot de passe
export const conditionalMinLengthRefineConfig = {
  message: 'validation.minLength',
  path: ['password'],
}
