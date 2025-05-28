import { client } from '@/api/generated/client.gen'

client.setConfig({ credentials: 'include' })

export { client }
