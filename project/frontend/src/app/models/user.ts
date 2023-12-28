export interface User {
	id: number;
	email: string;
	password: string;
	username: string;
	role: Role;
}

export enum Role {
	ADMIN = 'ADMIN',
	USER = 'USER'
}