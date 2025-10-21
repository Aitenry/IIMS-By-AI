declare module 'vditor' {
    interface VditorOptions {
        height?: number | string
        mode?: 'ir' | 'wysiwyg' | 'sv'
        preview?: {
            actions: string[]
        }
        toolbar?: Array<string | {
            name: string
            tip: string
            tipPosition: string
            icon: string
            toolbar: string[]
        }>
        toolbarConfig?: {
            pin: boolean
        }
        counter?: {
            enable: boolean
        }
        outline?: {
            enable: boolean
        }
        cache?: {
            enable: boolean
        }
        after?(): void
        upload?: {
            accept: string
            url: string
            multiple: boolean
            fieldName: string
            max: number
            extraData: Record<string, any>
            linkToImgUrl: string
            filename?(name: string): string
            validate?(msg: string): void
            linkToImgFormat?(files: string): string
            format?(files: File[], responseText: string): string
            error?(msg: string): void
        }
    }

    class Vditor {
        constructor(element: HTMLElement, options: VditorOptions)
        setValue(value: string): void
        getValue(): string
        destroy(): void
    }

    export default Vditor
}