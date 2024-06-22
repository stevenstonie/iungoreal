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

export interface CountryAndRegionsMenuOptions {
	showCountryOptions: boolean;
	showPrimaryRegionOptions: boolean;
	showSecondaryRegionOptions: boolean;
	showSecondaryRegionOptionsToRemove: boolean;
}