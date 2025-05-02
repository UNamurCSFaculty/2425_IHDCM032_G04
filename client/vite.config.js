import { defineConfig } from 'vite'
import viteReact from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import eslint from 'vite-plugin-eslint'

import { TanStackRouterVite } from '@tanstack/router-plugin/vite'
import { resolve } from 'node:path'

const babelPlugins = [['babel-plugin-react-compiler', {}]]

// https://vitejs.dev/config/
export default defineConfig(({ command }) => ({
  plugins: [
    TanStackRouterVite({ autoCodeSplitting: true }),
    viteReact({
      babel: {
        plugins: [
          ...babelPlugins,
          ...(command === 'serve'
            ? [['@babel/plugin-transform-react-jsx-development']]
            : []),
        ],
      },
    }),
    tailwindcss(),
    eslint({
      lintOnStart: true,
      failOnError: false, // 🛑 ne bloque pas le build sur les erreurs :contentReference[oaicite:0]{index=0}
      failOnWarning: false, // ne bloque pas non plus sur les warnings
      emitWarning: true, // affiche les warnings
      emitError: true, // affiche les erreurs (mais ne bloque plus)
      fix: true, // correction automatique quand possible
      include: ['src/**/*.{js,ts,jsx,tsx}'],
      exclude: ['node_modules'],
    }),
  ],
  test: {
    globals: true,
    environment: 'jsdom',
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, './src'),
    },
  },
}))
