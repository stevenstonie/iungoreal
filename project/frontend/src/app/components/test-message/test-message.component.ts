import { NgFor, NgIf } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Message } from '@stomp/stompjs';
import { StompWebsocketService } from 'src/app/services/stomp-websocket.service';
import { ChatMessage } from '../../models/app';

@Component({
  selector: 'app-test-message',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './test-message.component.html',
  styleUrl: './test-message.component.scss'
})
export class TestMessageComponent implements OnInit, OnDestroy {
  topic = '/topic/greetings';
  topicToBack = '/app/hello'
  receivedMessages: ChatMessage[] = [];

  constructor(private stompWebsocketService: StompWebsocketService) {
  }

  ngOnInit(): void {
    this.stompWebsocketService.subscribeToTopic(this.topic, (chatMessage: ChatMessage) => {
      this.handleReceivedMessage(chatMessage);
    })
  }

  connectToWebsocket(): void {
    this.stompWebsocketService = new StompWebsocketService();
    this.ngOnInit();
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
}