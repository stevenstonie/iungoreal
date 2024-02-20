export interface JsonifiedString {
	string: string
}

export interface PostPayload {
	authorUsername: string;
	title: string;
	description: string;
	createdAt: Date;
	mediaLinks: string[];
	likes: number;
	dislikes: number;
}