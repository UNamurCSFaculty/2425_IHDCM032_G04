import { z } from 'zod'
import i18n from '../i18n'

/**
 * Propriétés des utilisateurs : Role et Langage
 */
export const zRole = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1, i18n.t("validation.required")),
})

export const zLanguage = z.object({
  id: z.number().int().readonly(),
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
    'carrier'
  ]),
  firstName: z.string().min(1, i18n.t("validation.required")),
  lastName: z.string().min(1, i18n.t("validation.required")),
  email: z.string().min(1, i18n.t("validation.required")),
  registrationDate: z.iso.datetime().readonly().optional(),
  validationDate: z.iso.datetime().readonly().optional(),
  enabled: z.boolean().optional(),
  address: z.string().min(1, i18n.t("validation.required")),
  phone: z.string().regex(/^(?:\+229)?(?:01[2-9]\d{7}|[2-9]\d{7})$/),
  password: z.string().min(8, i18n.t("validation.minLength")),
  language: zLanguage,
  agriculturalIdentifier: z.string().min(1, i18n.t("validation.required")),
})

const zProducer = zUser.extend({
  type: z.literal('producer'),
  agriculturalIdentifier: z.string().min(1, i18n.t("validation.required")),
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
    message: i18n.t('Les mots de passe ne correspondent pas')
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
  name: z.string().min(1, i18n.t("validation.required")),
  address: z.string().min(1, i18n.t("validation.required")),
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
  name: z.string().min(1, i18n.t("validation.required")),
})

export const zQuality = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1, i18n.t("validation.required")),
})

export const zDocument = z.object({
  id: z.number().int().readonly().optional(),
  documentType: z.string().min(1, i18n.t("validation.required")),
  format: z.string().min(1, i18n.t("validation.required")),
  storagePath: z.string().min(1, i18n.t("validation.required")),
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
  location: z.string().min(1, i18n.t("validation.required")),
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
  identifier: z.string().min(1, i18n.t("validation.required")),
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
    status: z.string().min(1, i18n.t("validation.required")),
    pricePerKg: z.number(),
    creationDate: z.iso.datetime().readonly(),
    endDate: z.iso.datetime(),
    seller: zTrader,
    buyer: zTrader,
    quality: zQuality,
  })
  .refine(data => data.seller.id !== data.buyer.id, {
    path: ['buyer'],
    message: i18n.t("Le vendeur et l'acheteur doivent être différents")
  })

/**
 * Les offres avec leurs enchères, options et stratégies
 */
export const zAuctionStrategy = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1, i18n.t("validation.required")),
})

export const zAuctionOption = z.object({
  id: z.number().int().readonly().optional(),
  name: z.string().min(1, i18n.t("validation.required")),
})

export const zAuctionOptionValue = z.object({
  id: z.number().int().readonly().optional(),
  auctionOption: zAuctionOption,
  optionValue: z.string().min(1, i18n.t("validation.required")),
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
  name: z.string().min(1, i18n.t("validation.required")),
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
