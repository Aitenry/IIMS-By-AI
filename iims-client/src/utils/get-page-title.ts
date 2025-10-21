import defaultSettings from '@/settings'

const title = defaultSettings.title || 'IIMS By AI'

export default function getPageTitle(pageTitle: string) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
