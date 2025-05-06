export function getCookie(name: string): string | undefined {
  const match = document.cookie.match(
    new RegExp('(^|;)\\s*' + name + '=([^;]*)')
  )
  return match ? decodeURIComponent(match[2]) : undefined
}
