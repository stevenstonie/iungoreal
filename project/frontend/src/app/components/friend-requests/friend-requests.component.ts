import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { NotificationFPayload } from 'src/app/models/Payloads';
import { NotificationService } from 'src/app/services/notification.service';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.scss']
})
export class FriendRequestsComponent implements OnInit {
  last50NotificationsF: NotificationFPayload[] = [];
  loggedUserUsername = localStorage.getItem('username') ?? '';
  @Output() notificationRemoved = new EventEmitter<number>();

  constructor(private notificationService: NotificationService, private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.notificationService.getLast50NotificationsF(this.loggedUserUsername).subscribe({
      next: (notifications: NotificationFPayload[]) => {
        this.last50NotificationsF = notifications;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  removeNotificationFAndRedirectToProfile(notificationFId: number, emitterUsername: string) {
    if (emitterUsername == '' || emitterUsername == null) {
      this.notificationService.removeNotificationF(notificationFId).subscribe({
        next: (response) => {
          this.last50NotificationsF = this.last50NotificationsF.filter(notificationF => notificationF.id != notificationFId);
          this.notificationRemoved.emit();

          console.log(response);
        },
        error: (error) => {
          console.error(error);
        }
      });
    } else {
      window.location.href = '/user/' + emitterUsername;
    }
  }
}
