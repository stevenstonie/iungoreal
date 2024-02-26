export interface User {
	id: number;
	email: string;
	password: string;
	username: string;
	role: Role;
	createdAt: Date;
	profilePictureUrl: string;
}

export enum Role {
	ADMIN = 'ADMIN',
	USER = 'USER'
}