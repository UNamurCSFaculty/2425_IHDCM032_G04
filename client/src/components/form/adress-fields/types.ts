import { zAddressDto } from '@/api/generated/zod.gen'
import type { ReactFormExtendedApi } from '@tanstack/react-form'
import z from 'zod/v4'

/* Schéma Zod du wrapper { address: AddressDto } */
export const zAddressFieldWrapper = z.object({
  address: zAddressDto,
})
export type AddressFieldWrapper = z.infer<typeof zAddressFieldWrapper>

/* API React de TanStack Form pour ce composite field */

export type AddressReactForm = ReactFormExtendedApi<
  AddressFieldWrapper, // 1) TFormData      → { address: AddressDto }
  undefined, // 2) TOnMount       (non utilisé)
  typeof zAddressFieldWrapper, // 3) TOnChange      → validation Zod
  undefined,
  undefined,
  undefined,
  undefined,
  undefined,
  undefined,
  undefined
>
