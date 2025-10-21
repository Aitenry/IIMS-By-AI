/**
 * @param {string} path
 * @returns {Boolean}
 */
export function isExternal(path: string): boolean {
  if (!path) return false
  return /^(https?:|mailto:|tel:)/.test(path)
}
