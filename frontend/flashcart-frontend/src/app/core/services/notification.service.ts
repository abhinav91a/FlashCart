import { Injectable, signal } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import SockJS from 'sockjs-client';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  notifications = signal<string[]>([]);
  connected = signal(false);
  private stockUpdateSubject = new Subject<void>();
  stockUpdate$ = this.stockUpdateSubject.asObservable();

  private client: Client;

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        this.connected.set(true);
     this.client.subscribe('/topic/notifications', (msg) => {
          this.notifications.update(n => [msg.body, ...n].slice(0, 10));
          this.stockUpdateSubject.next();
        });
      },
      onDisconnect: () => this.connected.set(false),
    });
  }

  connect() {
    this.client.activate();
  }

  disconnect() {
    this.client.deactivate();
  }
}
