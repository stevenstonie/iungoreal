import { Component, Input } from '@angular/core';
import { ChatMessage } from 'src/app/models/app';
import { User } from 'src/app/models/user';
import { StompWebsocketService } from 'src/app/services/stomp-websocket.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent {
  @Input() loggedUser: User | null = null;
  addingNewChatroom: boolean = false;

  topic = '/topic/chatroom';
  topicToBack = '/app/chat.sendToChatroom'
  receivedMessages: ChatMessage[] = [];

  constructor(private stompWebsocketService: StompWebsocketService) {
  }

  ngOnInit(): void {
      
  }

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

  getAllFriendsWithNoChats(): string[] {
    // TODO:
    return [];
  }

  toggleAddNewChatroom(): void {
    this.addingNewChatroom = !this.addingNewChatroom;
    // fetch users that dont have a chatroom with the loggedUser
    
  }
}

// TODO: make sure its ok for all instances of 'loggedUser' to be null 