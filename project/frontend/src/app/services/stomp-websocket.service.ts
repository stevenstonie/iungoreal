import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class StompWebsocketService {
  private stompClient: Client;

  constructor() { 
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8083/notification-endpoint',
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });
    this.stompClient.activate();
  }

  subscribeToTopic(topic: string, callback: (message: Message) => void) {
    this.stompClient.onConnect = () => {
      this.stompClient.subscribe(topic, callback);
    }
  }

  sendMessage(destination: string, message: string) {
    this.stompClient.publish({
      destination: destination,
      body: message
    })
  }

  disconnect() {
    this.stompClient.deactivate();
  }
}
