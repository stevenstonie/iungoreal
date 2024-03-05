export interface ResponsePayload {
	status: number;
	message: string;
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

export interface NotificationFPayload {
	id: number,
	receiverUsername: string,
	emitterUsername: string,
	emitterPfpLink: string,
	type: string,
	description: string,
	createdAt: Date
}