import Cookies from 'js-cookie'

export function getStorage(key: string) {
    return Cookies.get(key)
}

export function setStorage(key: string, value: string) {
    return Cookies.set(key, value)
}

export function removeStorage(key: string) {
    return Cookies.remove(key)
}
