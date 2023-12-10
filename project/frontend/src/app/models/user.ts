export interface User {
	id: number;
	email: string;
	password: string;
	firstname?: string;
	lastname?: string;
	role: Role;
}

export enum Role {
	ADMIN = 'ADMIN',
	USER = 'USER'
}