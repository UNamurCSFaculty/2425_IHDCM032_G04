import { readFile, writeFile } from 'fs/promises'
import { resolve } from 'path'

async function fixZod() {
  const file = resolve('src/api/generated/zod.gen.ts')
  let src = await readFile(file, 'utf8')

  // 1) AnyZodObject → ZodObject
  src = src.replace(/AnyZodObject/g, 'ZodObject')

  // 2) .string().datetime() → .iso.datetime()
  src = src.replace(/\.string\(\)\.datetime\(\)/g, '.iso.datetime()')

  // 3) merge(...) → and(...)
  src = src.replace(/merge\(([^)]+)\)/g, 'and($1)')

  await writeFile(file, src, 'utf8')
  console.log('✅ src/api/generated/zod.gen.ts patché')
}

fixZod().catch(err => {
  console.error('❌ Erreur fix-zod:', err)
  process.exit(1)
})
