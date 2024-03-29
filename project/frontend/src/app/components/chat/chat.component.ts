import { Component, Input, ViewContainerRef } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ChatroomPayload } from 'src/app/models/Payloads';
import { ChatMessage } from 'src/app/models/app';
import { User } from 'src/app/models/user';
import { ChatService } from 'src/app/services/chat.service';
import { StompWebsocketService } from 'src/app/services/stomp-websocket.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent {
  loggedUserUsername: string = localStorage.getItem('username') ?? '';
  isAddingNewChatroomOpen: boolean = false;
  areDmChatroomsOpen: boolean = false;
  areGroupChatroomsOpen: boolean = false;
  areRegionalChatroomsOpen: boolean = false;
  isChatroomOpened: boolean = false;

  currentChatroom: ChatroomPayload | null = null;

  friendsUsernamesWithNoChats: string[] = [];
  dmChatrooms: ChatroomPayload[] = [];
  groupChatrooms: ChatroomPayload[] = [];
  regionalChatrooms: ChatroomPayload[] = [];

  messageToSend: string = '';

  topic = '/topic/chatroom';
  topicToBack = '/app/chat.sendToChatroom'
  receivedMessages: ChatMessage[] = [];

  constructor(private stompWebsocketService: StompWebsocketService, private chatService: ChatService, private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {

  }

  ngOnDestroy(): void {
    this.disconnectFromWebsocket();
  }

  toggleAddNewChatroom(): void {
    this.isAddingNewChatroomOpen = !this.isAddingNewChatroomOpen;

    // fetch users that dont have a chatroom with the loggedUser
    this.chatService.getAllFriendsWithNoDmChats(this.loggedUserUsername).subscribe({
      next: (usernames) => {
        this.friendsUsernamesWithNoChats = usernames;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  createNewChatroom(friendUsername: string): void {
    this.chatService.createChatroom(friendUsername, this.loggedUserUsername).subscribe({
      next: (chatroom) => {
        console.log(chatroom);
        this.isAddingNewChatroomOpen = false;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  toggleDmChatrooms() {
    this.areDmChatroomsOpen = !this.areDmChatroomsOpen;

    if (this.areDmChatroomsOpen) {
      this.chatService.getAllDmChatroomsOfUser(this.loggedUserUsername).subscribe({
        next: (dmChatrooms) => {
          this.dmChatrooms = dmChatrooms;
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  toggleGroupChatrooms() {

  }

  toggleRegionalChatrooms() {

  }

  openChatroom(chatroom: ChatroomPayload): void {
    if (this.currentChatroom?.id === chatroom.id) {
      return;
    }

    this.closeChatroom();

    this.connectToWebsocket(chatroom.id);
    this.isChatroomOpened = true;
    this.currentChatroom = chatroom;
    this.receivedMessages = [];
  }

  closeChatroom(): void {
    this.isChatroomOpened = false;
    this.currentChatroom = null;
    this.disconnectFromWebsocket();
  }

  // websocket ---------------------------

  connectToWebsocket(chatroomId: number): void {
    this.stompWebsocketService = new StompWebsocketService();

    this.stompWebsocketService.subscribeToTopic(this.topic + '/' + chatroomId, (chatMessage: ChatMessage) => {
      this.handleReceivedMessage(chatMessage);
    })
  }

  disconnectFromWebsocket(): void {
    this.stompWebsocketService.disconnect();
  }

  handleReceivedMessage(chatMessage: ChatMessage): void {
    this.receivedMessages.push(chatMessage);
  }

  sendMessage(chatroomId: number | undefined): void {
    if (!chatroomId) {
      console.error("chatroom is undefined!!!!!!!!!!!!");
      return;
    }
    if(this.messageToSend === '') {
      return;
    }

    const chatMessage: ChatMessage = {
      senderUsername: this.loggedUserUsername !== '' ? this.loggedUserUsername : 'nousername',
      createdAt: new Date(),
      message: this.messageToSend
    }
    this.stompWebsocketService.sendMessage(this.topicToBack + '/' + chatroomId, chatMessage);

    this.messageToSend = '';
  }

  isWebsocketConnected(): boolean {
    return this.stompWebsocketService.isConnected();
  }

  // ^^^ --------------------------------

  sanitizeAndParseUrl(url: string): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }

  messageWithParsedLinks(message: string): string {
    const customUrlRegex = /(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?\/[a-zA-Z0-9]{2,}|((https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?)|(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}(\.[a-zA-Z0-9]{2,})?/g;
    return message.replace(customUrlRegex, (url) => `<a href="${url}" target="_blank">${url}</a>`);
  }
}

// TODO: make sure its ok for all instances of 'loggedUser' to be null 