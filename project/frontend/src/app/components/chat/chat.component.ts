import { Component, ElementRef, HostListener, Input, ViewChild, ViewContainerRef } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { ChatroomPayload } from 'src/app/models/Payloads';
import { ChatMessage } from 'src/app/models/app';
import { ChatService } from 'src/app/services/chat.service';
import { StompWebsocketService } from 'src/app/services/stomp-websocket.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent {
  topic = '/topic/chatroom';
  topicToBack = '/app/chat.sendToChatroom'
  loggedUserUsername: string = localStorage.getItem('username') ?? '';
  @ViewChild('chatContainer') chatContainer!: ElementRef;

  isAddingNewChatroomOpen: boolean = false;
  areDmChatroomsOpen: boolean = false;
  areGroupChatroomsOpen: boolean = false;
  areRegionalChatroomsOpen: boolean = false;
  isChatroomOpened: boolean = false;
  loadingMessages: boolean = false;

  currentChatroom: ChatroomPayload | null = null;
  friendsUsernamesWithNoChats: string[] = [];
  dmChatrooms: ChatroomPayload[] = [];
  groupChatrooms: ChatroomPayload[] = [];
  regionalChatrooms: ChatroomPayload[] = [];
  receivedMessages: ChatMessage[] = [];

  messageToSend: string = '';

  constructor(private stompWebsocketService: StompWebsocketService, private chatService: ChatService, private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {

  }

  ngOnDestroy(): void {
    this.disconnectFromWebsocket();
  }

  // chatroom --------------------------------------------------------------------

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
    this.loadMessagesOfChatroomId(chatroom.id, null);
  }

  closeChatroom(): void {
    this.isChatroomOpened = false;
    this.currentChatroom = null;
    this.disconnectFromWebsocket();
    this.receivedMessages = [];
  }

  removeChatroom(chatroomId: number | undefined): void {
    this.chatService.removeChatroom(this.loggedUserUsername, chatroomId).subscribe({
      next: () => {
        this.closeChatroom();
      },
      error: (error) => {
        console.error(error);
      }
    });
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
    if (this.messageToSend === '') {
      return;
    }

    const chatMessage: ChatMessage = {
      id: 0,
      chatroomId: chatroomId,
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

  onScroll(): void {
    const chatContainerElement = this.chatContainer.nativeElement;
    const scrolledToBottom = chatContainerElement.scrollHeight - chatContainerElement.clientHeight <= -chatContainerElement.scrollTop + 100;

    if (scrolledToBottom && this.currentChatroom?.id) {
      this.loadMessagesOfChatroomId(this.currentChatroom?.id, this.receivedMessages[0]?.id);
    }
  }

  loadMessagesOfChatroomId(chatroomId: number, lastMessageId: number | null): void {
    if (this.loadingMessages) {
      return;
    }

    this.loadingMessages = true;
    this.chatService.getNextMessagesByChatroomId(chatroomId, lastMessageId).subscribe({
      next: (messages) => {
        this.receivedMessages.unshift(...messages.reverse());
      },
      error: (error) => {
        console.error(error);
      },
      complete: () => {
        this.loadingMessages = false;
      }
    });
  }

  sanitizeAndParseUrl(url: string): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(url);
  }

  messageWithParsedLinks(message: string): string {
    const customUrlRegex = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+(:[0-9]+)?|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/;
    return message.replace(customUrlRegex, (url) => `<a href="${url}" target="_blank">${url}</a>`);
  }

  // TODO: shift+enter should add a new line
}

// TODO: make sure its ok for all instances of 'loggedUser' to be null 