import { readFile, writeFile } from 'fs/promises'
import { resolve } from 'path'

async function fixZod() {
  const file = resolve('src/api/generated/zod.gen.ts')
  let src = await readFile(file, 'utf8')

  // 1) AnyZodObject → ZodObject
  src = src.replace(/AnyZodObject/g, 'ZodObject')

  // 2) .string().datetime() → .iso.datetime()
  src = src.replace(/\.string\(\)\.datetime\(\)/g, '.iso.datetime()')

  // 3) Cast du lazy pour restaurer .merge() côté TS
  src = src.replace(
    /z\.lazy\(\(\) =>\s*\{\s*return\s+([\w\d_]+Dto);\s*\}\)/g,
    '(z.lazy(() => { return $1; }) as unknown as z.ZodObject<any>)'
  )

  await writeFile(file, src, 'utf8')
  console.log('✅ src/api/generated/zod.gen.ts patché')
}

fixZod().catch(err => {
  console.error('❌ Erreur fix-zod:', err)
  process.exit(1)
})
