import tailwindcss from '@tailwindcss/vite'
import { TanStackRouterVite } from '@tanstack/router-plugin/vite'
import viteReact from '@vitejs/plugin-react'
import { resolve } from 'node:path'
import { defineConfig } from 'vite'
import checker from 'vite-plugin-checker'
import eslint from 'vite-plugin-eslint'
import fs from 'node:fs'

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
    {
      // default settings on build (i.e. fail on error)
      ...eslint(),
      apply: 'build',
    },
    {
      // do not fail on serve (i.e. local development)
      ...eslint({
        failOnWarning: false,
        failOnError: false,
      }),
      apply: 'serve',
      enforce: 'post',
    },
    checker({
      typescript: true,
      overlay: {
        initialIsOpen: false,
        position: 'br',
      },
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
  server: {
    https: {
      key: fs.readFileSync(resolve(__dirname, '../.certs/localhost-key.pem')),
      cert: fs.readFileSync(resolve(__dirname, '../.certs/localhost.pem')),
    },
    port: 3000,
    strictPort: true,
  },
}))
