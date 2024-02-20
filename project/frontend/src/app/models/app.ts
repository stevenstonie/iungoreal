export interface Post{
	id: number,
	authorId: number,
	title: string,
	description: string | null,
	createdAt: Date
}

export interface JsonString {
	string: string
}