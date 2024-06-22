import { SafeUrl } from "@angular/platform-browser";

export interface ResponsePayload {
	status: number;
	message: string;
}

export interface PostPayload {
	id: number;
	authorUsername: string;
	title: string;
	description: string;
	createdAt: Date;
	mediaLinks: string[];
	upvoteScore: number;
	nbOfComments: number;
	upvoted: boolean;
	downvoted: boolean;
	saved: boolean;
	seen: boolean;
}

export interface CommentPayload {
	id: number;
	authorUsername: string;
	postId: number;
	content: string;
	createdAt: Date;
}

export interface CommentDetachedPayload {
	id: number;
	postTitle: string;
	content: string;
	createdAt: Date;
}

export interface NotificationFPayload {
	id: number,
	receiverUsername: string,
	emitterUsername: string,
	emitterPfpLink: SafeUrl,
	type: string,
	description: string,
	createdAt: Date
}

export interface CountryOrRegionPayload {
	id: number,
	name: string
}

export interface RegionDetailsPayload {
	id: number,
	name: string,
	latitude: number,
	longitude: number
}

export interface ChatroomPayload {
	id: number,
	name: string,
	type: ChatroomType,
	adminUsername: string,
	lastMessageTime: Date,
	participantsUsernames: string[]
}

export enum ChatroomType {
	DM = 'DM',
	GROUP = 'GROUP',
	REGIONAL = 'REGIONAL'
}

export interface PublicUserPayload {
	id: number,
	username: string,
	pfpLink: string
}