import { Component } from '@angular/core';
import { NotificationFPayload } from 'src/app/models/payloads';
import { NotificationsService } from 'src/app/services/notifications.service';
import { map } from 'rxjs';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.scss']
})
export class FriendRequestsComponent {
  last50NotificationsF: NotificationFPayload[] = [];
  loggedUserUsername = localStorage.getItem('username') ?? '';
  profilePictureUrl: string = 'assets/default-images/default-profile-picture.jpg';

  constructor(private notificationsService: NotificationsService, private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.notificationsService.getLast50NotificationsF(this.loggedUserUsername).pipe(
      map((notifications: NotificationFPayload[]) => {
        return notifications.map(notification => {
          if (notification.emitterPfpLink === '' || notification.emitterPfpLink === null) {
            notification.emitterPfpLink = this.profilePictureUrl;
          } else {
            const sanitizedUrl: SafeUrl = this.sanitizer.bypassSecurityTrustUrl(notification.emitterPfpLink);
          notification.emitterPfpLink = sanitizedUrl.toString();
          }
          return notification;
        });
      })
    ).subscribe({
      next: (notifications: NotificationFPayload[]) => {
        this.last50NotificationsF = notifications;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  // TODO!!!!!!!!: upodate the code so that the users who have no pfp will use the default one from the cloud
}
