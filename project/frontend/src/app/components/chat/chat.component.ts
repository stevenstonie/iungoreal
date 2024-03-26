import { Component, Input } from '@angular/core';
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
  @Input() loggedUser: User | null = null;
  addingNewChatroom: boolean = false;

  friendsUsernamesWithNoChats: string[] = [];
  dmChatrooms: string[] = [];

  topic = '/topic/chatroom';
  topicToBack = '/app/chat.sendToChatroom'
  receivedMessages: ChatMessage[] = [];

  constructor(private stompWebsocketService: StompWebsocketService, private chatService: ChatService) {
  }

  ngOnInit(): void {
    this.connectToWebsocket();
    this.getAllFriendsUsernamesWithChatrooms();
  }

  // websocket ---------------------------

  connectToWebsocket(): void {
    this.stompWebsocketService = new StompWebsocketService();

    this.stompWebsocketService.subscribeToTopic(this.topic, (chatMessage: ChatMessage) => {
      this.handleReceivedMessage(chatMessage);
    })
  }

  disconnectFromWebsocket(): void {
    this.stompWebsocketService.disconnect();
  }

  handleReceivedMessage(chatMessage: ChatMessage): void {
    this.receivedMessages.push(chatMessage);
  }

  sendMessage(message: string): void {
    const chatMessage: ChatMessage = {
      username: localStorage.getItem('username') ?? 'nousername',
      createdAt: new Date(),
      message: message
    }
    this.stompWebsocketService.sendMessage(this.topicToBack, chatMessage);
  }

  ngOnDestroy(): void {
    this.stompWebsocketService.disconnect();
  }

  isWebsocketConnected(): boolean {
    return this.stompWebsocketService.isConnected();
  }

  // ^^^ --------------------------------

  toggleAddNewChatroom(): void {
    this.addingNewChatroom = !this.addingNewChatroom;

    // fetch users that dont have a chatroom with the loggedUser
    this.chatService.getAllFriendsWithNoChats(localStorage.getItem('username') ?? '').subscribe({
      next: (usernames) => {
        this.friendsUsernamesWithNoChats = usernames;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  createNewChatroom(friendUsername: string): void {
    this.chatService.createChatroom(friendUsername, localStorage.getItem('username') ?? '').subscribe({
      next: (chatroom) => {
        console.log(chatroom);
        this.addingNewChatroom = false;
        this.getAllFriendsUsernamesWithChatrooms();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getAllFriendsUsernamesWithChatrooms(): void {
    this.chatService.getAllFriendsWithChatrooms(localStorage.getItem('username') ?? '').subscribe({
      next: (chatrooms) => {
        this.dmChatrooms = chatrooms;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}

// TODO: make sure its ok for all instances of 'loggedUser' to be null 