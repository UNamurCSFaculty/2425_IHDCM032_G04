import dayjs from 'dayjs'
import 'dayjs/locale/en'
import 'dayjs/locale/fr'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.extend(relativeTime)

export function setDayjsLocale(lang: string) {
  dayjs.locale(lang)
}

export default dayjs
