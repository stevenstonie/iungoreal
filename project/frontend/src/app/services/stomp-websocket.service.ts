import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { ChatMessage } from '../models/app';

@Injectable({
  providedIn: 'root'
})
export class StompWebsocketService {
  private stompClient: Client;

  constructor() {
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8083/chat-endpoint',
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });
    this.stompClient.activate();
  }

  subscribeToTopic(topic: string, callback: (chatMessage: ChatMessage) => void) {
    this.stompClient.onConnect = () => {
      this.stompClient.subscribe(topic, (message: IMessage) => {
        const chatMessage: ChatMessage = JSON.parse(message.body);
        callback(chatMessage);
      });
    };
  }

  sendMessage(destination: string, chatMessage: ChatMessage) {
    const message = JSON.stringify(chatMessage);

    this.stompClient.publish({
      destination: destination,
      body: message
    })
  }

  disconnect() {
    this.stompClient.deactivate();
  }

  isConnected(): boolean {
    return this.stompClient?.connected || false;
  }
}
