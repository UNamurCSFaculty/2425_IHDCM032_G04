module.exports = {
    semi: false,
    singleQuote: true,
    bracketSpacing: true,
    arrowParens: 'avoid',
    trailingComma: 'es5',
    tabWidth: 2,
  
    // active le plugin Tailwind
    plugins: [require('prettier-plugin-tailwindcss')],
  
    // désactive la recherche automatique de plugins dans les dossiers parents
    pluginSearchDirs: false,

  }