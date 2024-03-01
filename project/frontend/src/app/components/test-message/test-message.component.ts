import { Component } from '@angular/core';

@Component({
  selector: 'app-test-message',
  standalone: true,
  imports: [],
  templateUrl: './test-message.component.html',
  styleUrl: './test-message.component.scss'
})
export class TestMessageComponent {
  private url: string = "ws://localhost:8083/notification-endpoint";
  private topic: string = "/topic/greetings";

  constructor() { }

}
