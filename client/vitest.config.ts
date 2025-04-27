import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true, // pour ne pas avoir à importer describe/test/expect
    setupFiles: './vitest.setup.ts',
    coverage: { reporter: ['text', 'html'] },
  },
})
