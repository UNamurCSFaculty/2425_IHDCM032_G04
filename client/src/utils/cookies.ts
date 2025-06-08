/**
 * Récupère la valeur d'un cookie par son nom
 * @param name - Le nom du cookie à récupérer
 * @returns La valeur du cookie décodée ou undefined si le cookie n'existe pas
 */
export function getCookie(name: string): string | undefined {
  const match = document.cookie.match(
    new RegExp('(^|;)\\s*' + name + '=([^;]*)')
  )
  return match ? decodeURIComponent(match[2]) : undefined
}
