import { z } from 'zod'

/**
 * Propriétés des utilisateurs : Role et Langage
 */
export const zRole = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1),
})

export const zLanguage = z.object({
  id: z.number().int().readonly().optional(),
  code: z.string().min(1),
  name: z.string().min(1),
})

/**
 * Les différents types d'utilisateurs (hiérarchie)
 */
export const zUser = z.object({
  id: z.number().int().readonly().optional(),
  type: z.enum([
    'admin',
    'producer',
    'transformer',
    'quality_inspector',
    'exporter',
    'carrier',
  ]),
  firstName: z.string().min(1),
  lastName: z.string().min(1),
  email: z.email().min(1),
  registrationDate: z.iso.datetime().readonly().optional(),
  validationDate: z.iso.datetime().readonly().optional(),
  enabled: z.boolean().optional(),
  address: z.string().min(1),
  phone: z.string().regex(/^(?:\+229)?(?:01[2-9]\d{7}|[2-9]\d{7})$/),
  password: z
    .string()
    .min(8)
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/),
  language: zLanguage,
})

const zProducer = zUser.extend({
  type: z.literal('producer'),
  agriculturalIdentifier: z.string().min(1),
})

const zTransformer = zUser.extend({
  type: z.literal('transformer'),
})

const zCarrier = zUser.extend({
  type: z.literal('carrier'),
  pricePerKm: z.number().positive(),
  regionIds: z.array(z.number().int()).nonempty(),
})

const zExporter = zUser.extend({
  type: z.literal('exporter'),
})

const zQualityInspector = zUser.extend({
  type: z.literal('quality_inspector'),
})

export const zUserRegistration = z
  .discriminatedUnion('type', [
    zProducer,
    zTransformer,
    zCarrier,
    zExporter,
    zQualityInspector,
  ])
  .and(
    z.object({
      passwordValidation: z.string(),
    })
  )
  .refine(data => data.password === data.passwordValidation, {
    path: ['passwordValidation'],
  })

export const zTrader = z.discriminatedUnion('type', [
  zExporter,
  zProducer,
  zTransformer,
])

/**
 * Table Champ, Coopérative, Magasin, Région, Qualité et Document
 */
export const zField = z.object({
  id: z.number().int().readonly().optional(),
  identifier: z.string().optional(),
  location: z.string().optional(),
  producer: zProducer.optional(),
})

export const zCooperative: z.ZodObject = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1),
  address: z.string().min(1),
  creationDate: z.iso.datetime(),
  president: zProducer,
})

export const zStore = z.object({
  id: z.number().int().readonly().optional(),
  location: z.string(),
  userId: z.number().int(),
})

export const zRegion = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1),
})

export const zQuality = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1),
})

export const zDocument = z.object({
  id: z.number().int().readonly().optional(),
  documentType: z.string().min(1),
  format: z.string().min(1),
  storagePath: z.string().min(1),
  uploadDate: z.iso.datetime().readonly().optional(),
  userId: z.number().int(),
})

/**
 * Les différents types de produits (hiérarchie)
 */
export const zProduct: z.ZodObject = z.object({
  id: z.number().int().readonly().optional(),
  weightKg: z.number().optional(),
  qualityControlId: z.number().int(),
})

export const zTransformedProduct = zProduct.extend({
  type: z.literal('transformed'),
  location: z.string().min(1),
  transformer: zTransformer,
})

export const zHarvestProduct = zProduct.extend({
  type: z.literal('harvest'),
  store: zStore,
  producer: zProducer,
  field: zField,
  transformedProduct: zTransformedProduct.optional(),
  deliveryDate: z.iso.datetime().optional(),
})

export const zProductDeposit = z.discriminatedUnion('type', [
  zTransformedProduct,
  zHarvestProduct,
])

/**
 * Le contrôle de qualité et les offres de contrats
 */
export const zQualityControl: z.ZodObject = z.object({
  id: z.number().int().readonly().optional(),
  identifier: z.string().min(1),
  controlDate: z.iso.datetime(),
  granularity: z.number(),
  korTest: z.number(),
  humidity: z.number(),
  qualityInspector: zQualityInspector,
  product: zProductDeposit,
  quality: zQuality,
  document: zDocument,
})

export const zContractOffer = z
  .object({
    id: z.number().int().readonly().optional(),
    status: z.string().min(1),
    pricePerKg: z.number(),
    creationDate: z.iso.datetime().readonly(),
    endDate: z.iso.datetime(),
    seller: zTrader,
    buyer: zTrader,
    quality: zQuality,
  })
  .refine(data => data.seller.id !== data.buyer.id, {
    path: ['buyer'],
  })

/**
 * Les offres avec leurs enchères, options et stratégies
 */
export const zAuctionStrategy = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1),
})

export const zAuctionOption = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1),
})

export const zAuctionOptionValue = z.object({
  id: z.number().int().readonly().optional(),
  auctionOption: zAuctionOption,
  optionValue: z.string().min(1),
})

export const zAuction = z.object({
  id: z.number().int().readonly().optional(),
  price: z.number(),
  productQuantity: z.number().int(),
  expirationDate: z.iso.datetime(),
  creationDate: z.iso.datetime().readonly(),
  active: z.boolean(),
  strategy: zAuctionStrategy,
  product: zProductDeposit,
  auctionOptionValues: z.array(zAuctionOptionValue).optional(),
})

export const zBidStatus = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1),
})

export const zBid = z.object({
  id: z.number().int().readonly().optional(),
  amount: z.number(),
  auctionDate: z.iso.datetime(),
  creationDate: z.iso.datetime().readonly(),
  auction: zAuction,
  trader: zTrader,
  status: zBidStatus,
})

/**
 * Requête de connexion
 */
export const zLoginRequest = z.object({
  username: z.string(),
  password: z.string(),
})

/**
 * Les types d'érreurs
 */
export const zApiErrorErrors = z.object({
  path: z.string().optional(),
  message: z.string().optional(),
  errorCode: z.string().optional(),
})

export const zApiError = z.object({
  message: z.string().optional(),
  errors: z.array(zApiErrorErrors).optional(),
})
