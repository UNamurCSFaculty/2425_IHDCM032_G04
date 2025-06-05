import { defineConfig } from '@hey-api/openapi-ts'

export default defineConfig({
  // URL de votre spec OpenAPI (Swagger UI)
  input: 'https://localhost:8080/v3/api-docs',
  // dossier de sortie
  output: 'src/api/generated',
  // plugins pour générer :
  // - un client fetch
  // - des schémas Zod
  // - des hooks React Query
  plugins: [
    '@hey-api/client-fetch',
    'zod',
    '@tanstack/react-query',
    {
      name: '@hey-api/typescript',
      readOnlyWriteOnlyBehavior: 'off',
      enums: 'typescript',
      exportInlineEnums: true,
    },
  ],
})
