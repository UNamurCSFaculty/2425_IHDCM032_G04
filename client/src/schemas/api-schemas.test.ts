// src/schemas/zod-schemas.test.ts
import { describe, it, expect } from 'vitest'
import { zRoleDto, zLanguageDto, zUserDetailDto } from './zod-schemas'
import '../utils/zod-config.ts'

// ---------- zRoleDto ----------
describe('zRoleDto', () => {
  it('valide un rôle avec un id et un name', () => {
    const valid = { id: 1, name: 'Manager' }
    expect(() => zRoleDto.parse(valid)).not.toThrow()
  })

  it('rejette un rôle sans name ou name vide', () => {
    const noName = { id: 2 }
    const result = zRoleDto.safeParse(noName)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.issues[0].path).toEqual(['name'])
      console.log(result.error.issues[0].message)
    }

    const emptyName = { name: '' }
    const res2 = zRoleDto.safeParse(emptyName)
    expect(res2.success).toBe(false)
    if (!res2.success) {
      expect(res2.error.issues[0].path).toEqual(['name'])
    }
  })
})

// ---------- zLanguageDto ----------
describe('zLanguageDto', () => {
  it('valide une langue avec name non vide', () => {
    const valid = { id: 10, name: 'Français' }
    expect(() => zLanguageDto.parse(valid)).not.toThrow()
  })

  it('rejette une langue sans name', () => {
    const invalid = { id: 5 }
    const result = zLanguageDto.safeParse(invalid)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.issues[0].path).toEqual(['name'])
    }
  })
})

// ---------- zUserDetailDto ----------
describe('zUserDetailDto', () => {
  const baseValid = {
    firstName: 'Jean',
    lastName: 'Dupont',
    email: 'jean.dupont@example.com',
    language: { id: 1, name: 'en' },
    type: 'admin' as const,
  }

  it('valide un objet minimal correct', () => {
    expect(() => zUserDetailDto.parse(baseValid)).not.toThrow()
  })

  it('valide toutes les propriétés optionnelles correctement', () => {
    const full = {
      ...baseValid,
      id: 42,
      registrationDate: '2025-04-01T10:00:00Z',
      validationDate: '2025-04-02T11:00:00Z',
      enabled: true,
      address: '123 Rue Principale',
      phone: '+2290197000000',
      password: 'secret123',
      roles: [{ id: 7, name: 'User' }],
    }

    expect(() => zUserDetailDto.parse(full)).not.toThrow()
  })

  it('rejette si firstName est manquant ou vide', () => {
    const missing = { ...baseValid, firstName: '' }
    const result = zUserDetailDto.safeParse(missing)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.issues.some(e => e.path[0] === 'firstName')).toBe(
        true
      )
    }
  })

  it('rejette un phone invalide', () => {
    const invalidPhone = { ...baseValid, phone: '123' }
    const result = zUserDetailDto.safeParse(invalidPhone)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.issues.some(e => e.path[0] === 'phone')).toBe(true)
    }
  })

  it('rejette un type hors enum', () => {
    const badType = { ...baseValid, type: 'superadmin' }
    const result = zUserDetailDto.safeParse(badType)
    expect(result.success).toBe(false)
    if (!result.success) {
      expect(result.error.issues.some(e => e.path[0] === 'type')).toBe(true)
    }
  })
})
