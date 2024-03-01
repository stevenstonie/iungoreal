import { TestBed } from '@angular/core/testing';

import { StompWebsocketService } from './stomp-websocket.service';

describe('StompWebsocketService', () => {
  let service: StompWebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StompWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
