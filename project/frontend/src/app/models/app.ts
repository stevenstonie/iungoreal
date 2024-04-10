export interface StringInJson {
	string: string
}

export interface ChatMessage {
	id: number
	chatroomId: number
	senderUsername: string
	message: string;
	createdAt: Date;
}