import dayjs from 'dayjs'
import 'dayjs/locale/en'
import 'dayjs/locale/fr'
import relativeTime from 'dayjs/plugin/relativeTime'
import utc from 'dayjs/plugin/utc'

// Extension to format date to relative time strings (e.g. 3 hours ago).
dayjs.extend(relativeTime)

// Extension to parse date in UTC. This requires the specific constructor daysjs.utc(),
// and not the default dayjs() constructor which remains in local time.
dayjs.extend(utc)

export function setDayjsLocale(lang: string) {
  dayjs.locale(lang)
}

export default dayjs
