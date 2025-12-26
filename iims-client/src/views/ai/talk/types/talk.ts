export interface ChatHeaderProps {
    isOpenChatList: boolean
    wikiStatusDecl: string
    isFlashing: boolean
    loadWikiTitles: string[]
}

// 定义类型
export interface FileInfo {
    filename: string
    [key: string]: any // 其他可能的属性
}

export interface UseFileParam {
    isUseFile: boolean
    fileInfos: FileInfo[]
}

export interface WikiItem {
    id: string
    title: string
    summary: string
    imgUrl: string
    createTime: string
    type: boolean
    taskStatus: number
    isTop: boolean
}

export interface WikiMeta {
    wikiId: string
    docId: string
    name: string
}

export interface MsgParam {
    topicId: string | null
    lastId: string | null
    fileId: string | null
    question: string
    isUseAgent: boolean | null
    wikiIds: string[] | null
}

export interface DialoguePages {
    topicId: string | null
    page: number
    pageSize: number
    total: number
}

export interface TopicPages {
    title: string | null
    page: number
    pageSize: number
    total: number
}

export interface WikiPages {
    title: string | null
    page: number
    pageSize: number
    total: number
}

export interface RenderedMarkdownItem {
    renderedText: string
    [key: string]: any
}


export interface MetadataItem {
    metadata: {
        name: string;
    };
}

export interface Message {
    id: string | null
    content: string
    sender: string
    fileInfos?: any[]
    view?: string
    tools?: Tool[]
    isLoadingAnswer?: boolean
    isStar: boolean
    feedbackStatus: number
    docMetadata?: any
    lastId: string | null
    think?: string
    isComplete?: boolean
    isShowThink?: boolean
    isExpanded?: boolean
}

export interface Tool {
    id: string | null
    type: string | null
    name: string | null
    arguments: string | null
    responseData: string | null
}

export interface StatusData {
    task: string;
    progress: number;
    total: number;
}

export interface ChatItem {
    id: string;
    title: string;
}

export interface HotTopic {
    rank: number;
    title: string;
}

export interface ToolTopic {
    name: string;
    content: string;
    avatarSrc: string;
}

export interface AgentTopic {
    name: string;
    content: string;
    avatarSrc: string;
}