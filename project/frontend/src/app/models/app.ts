export interface Post{
	id: number,
	authorId: number,
	title: string,
	description: string,
	mediaName: string,
	createdAt: Date
}

export interface JsonString {
	string: string
}