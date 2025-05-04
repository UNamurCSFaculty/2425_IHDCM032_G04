// prettier.config.cjs
module.exports = {
  semi: false,
  singleQuote: true,
  bracketSpacing: true,
  arrowParens: 'avoid',
  trailingComma: 'es5',
  tabWidth: 2,
  importOrderSeparation: true,
  importOrderSortSpecifiers: true,
  // on liste ici le plugin par son nom, Prettier le trouve en node_modules
  plugins: [
    'prettier-plugin-tailwindcss',
    '@trivago/prettier-plugin-sort-imports',
  ],

  // soit on supprime cette ligne pour laisser Prettier chercher normalement
  // soit, si vraiment besoin, on pointe sur le dossier racine de votre projet :
  // pluginSearchDirs: ['.'],
}
