import { readFile, writeFile } from 'fs/promises'
import { resolve } from 'path'

async function fixZod() {
  const file = resolve('src/api/generated/zod.gen.ts')
  let src = await readFile(file, 'utf8')

  // 1) Patch des documents : unknown → file
  src = src.replace(
    /documents:\s*z\.array\(\s*z\.unknown\(\)\s*\)(\.optional\(\))?/g,
    'documents: z.array(z.file())$1'
  )
  src = src.replace(
    /documents:\s*z\.array\(\s*z\.string\(\)\s*\)(\.optional\(\))?/g,
    'documents: z.array(z.file())$1'
  )

  // 2) Remplacement de l'import au début du fichier
  //    - ^\s*         : accepte d’éventuels blancs en début de ligne
  //    - import {...} : capture l’import nommé « z »
  //    - ['"]zod['"]  : guillemets simples ou doubles
  //    - ;?           : point-virgule optionnel
  src = src.replace(
    /^\s*import\s+\{\s*z\s*\}\s+from\s+['"]zod['"]\s*;?/m,
    "import z from 'zod/v4'"
  )

  // 3) Remplacement de .string().email() par .email()
  //    pour passer de z.string().email() à z.email() (Zod v4)

  src = src.replace(/\.string\(\)\s*\.email\s*(\([^)]*\))/g, '.email$1')

  // 4) Remplacement de .string().datetime(...) par .datetime()
  src = src.replace(
    /\.string\(\)\s*\.datetime\s*(\([^)]*\))/g,
    '.iso.datetime$1'
  )

  await writeFile(file, src, 'utf8')
  console.log('✅ src/api/generated/zod.gen.ts patché')
}

fixZod().catch(err => {
  console.error('❌ Erreur fix-zod :', err)
  process.exit(1)
})
