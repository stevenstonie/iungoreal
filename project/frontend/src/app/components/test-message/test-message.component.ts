import { NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Message } from '@stomp/stompjs';
import { StompWebsocketService } from 'src/app/services/stomp-websocket.service';

@Component({
  selector: 'app-test-message',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './test-message.component.html',
  styleUrl: './test-message.component.scss'
})
export class TestMessageComponent implements OnInit, OnDestroy {
  topic = '/topic/greetings';
  receivedMessages: string[] = [];

  constructor(private stompWebsocketService: StompWebsocketService) {
  }

  ngOnInit(): void {
    console.log('TestMessageComponent ngOnInit() called');
    this.stompWebsocketService.subscribeToTopic(this.topic, (message: Message) => {
      this.handleReceivedMessage(message);
    })
  }

  connectToWebsocket(): void {
    this.stompWebsocketService = new StompWebsocketService();
    this.ngOnInit();
  }

  disconnectFromWebsocket(): void {
    this.stompWebsocketService.disconnect();
  }

  handleReceivedMessage(message: Message): void {
    console.log("Received message: " + message.body);
    this.receivedMessages.push(message.body);
  }

  sendMessage(message: string): void {
    this.stompWebsocketService.sendMessage(this.topic, message);
  }

  ngOnDestroy(): void {
    this.stompWebsocketService.disconnect();
  }

  isWebsocketConnected(): boolean {
    return this.stompWebsocketService.isConnected();
  }
}
