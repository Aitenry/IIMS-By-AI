import type {ParseResult} from "@/utils/parse-reasoning.ts";

export interface ChatHeaderProps {
    isOpenChatList: boolean
    wikiStatusDecl: string
    isFlashing: boolean
    loadWikiTitles: string[]
    onModelSelectionChanged: (model: any) => void
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
    index: string
    name: string
}

export interface MsgParam {
    topicId: string | null
    lastId: string | null
    fileId: string | null
    question: string
    modelId: string | null
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
    aiContent?: AiContent[]
    userContent?: UserContent
    sender: string
    fileInfos?: any[]
    isLoadingAnswer?: boolean
    isStar: boolean
    feedbackStatus: number
    docMetadata?: any
    lastId: string | null
}

export interface AiContent {
    content: string
    thinking: string
    contentResult?: ParseResult[]
    tools?: Tool[]
}

export interface UserContent {
    question: string
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
    createTime: string;
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