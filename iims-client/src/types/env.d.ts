interface ImportMeta {
    glob: (pattern: string | string[], options?: {
        eager?: boolean;
        import?: string;
        query?: string | Record<string, string | number | boolean>;
        as?: 'url' | 'raw' | 'worker';
    }) => Record<string, () => Promise<any>>

    globEager: (pattern: string | string[]) => Record<string, any>
}

interface ImportMetaEnv {
    readonly VITE_APP_BASE_API: string
    readonly VITE_APP_TITLE: string
    readonly VITE_APP_API_URL: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}