import { defineConfig } from '@hey-api/openapi-ts'
import * as dotenv from 'dotenv'
dotenv.config() 

export default defineConfig({
  // URL de la spec OpenAPI (Swagger UI)
  input: process.env.VITE_API_BASE_URL + "/v3/api-docs", 
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
