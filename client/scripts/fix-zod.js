import { readFile, writeFile } from 'fs/promises'
import { resolve } from 'path'

async function fixZod() {
  const file = resolve('src/api/generated/zod.gen.ts')
  let src = await readFile(file, 'utf8')

  // Patch pour documents : unknown → file
  src = src.replace(
    /documents:\s*z\.array\(\s*z\.unknown\(\)\s*\)(\.optional\(\))?/g,
    'documents: z.array(z.file())$1'
  )

  await writeFile(file, src, 'utf8')
  console.log('✅ src/api/generated/zod.gen.ts patché')
}

fixZod().catch(err => {
  console.error('❌ Erreur fix-zod:', err)
  process.exit(1)
})