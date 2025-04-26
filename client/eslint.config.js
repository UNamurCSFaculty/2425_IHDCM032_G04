import js from '@eslint/js'
import globals from 'globals'
import tseslint from 'typescript-eslint'
import pluginReact from 'eslint-plugin-react'
import reactCompiler from 'eslint-plugin-react-compiler'
import eslintConfigPrettier from 'eslint-config-prettier/flat'
import jsxA11y from 'eslint-plugin-jsx-a11y'
import reactHooks from 'eslint-plugin-react-hooks'
import { defineConfig, globalIgnores } from 'eslint/config'

export default defineConfig([
  globalIgnores(['src/api/generated/']),
  {
    files: ['vite.config.js', '.prettierrc.cjs', 'scripts/*.js'],
    languageOptions: {
      globals: globals.node, // __dirname, process, module, require…
      parserOptions: {
        ecmaVersion: 2021,
        sourceType: 'module',
      },
    },
  },
  {
    files: ['**/*.{js,mjs,cjs,ts,jsx,tsx}'],
    plugins: { js },
    extends: ['js/recommended'],
  },
  {
    files: ['**/*.{js,mjs,cjs,ts,jsx,tsx}'],
    languageOptions: { globals: globals.browser },
    settings: {
      react: { version: 'detect' },
    },
  },
  tseslint.configs.recommended,
  pluginReact.configs.flat.recommended,
  reactHooks.configs['recommended-latest'],
  jsxA11y.flatConfigs.recommended,
  reactCompiler.configs.recommended,
  eslintConfigPrettier,
  {
    files: ['**/*.{js,mjs,cjs,ts,jsx,tsx}'],
    rules: {
      // React 17+ n’a plus besoin d’import React en scope
      'react/react-in-jsx-scope': 'off',
      'react/prop-types': 'off',
      'react-hooks/exhaustive-deps': 'warn',
      'jsx-react/no-children-prop': 'off',
      'react/no-children-prop': 'off',
      'react/no-unescaped-entities': 'off',
    },
  },
])
