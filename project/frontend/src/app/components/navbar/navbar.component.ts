import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { User } from 'src/app/models/user';
import { catchError, delayWhen, firstValueFrom, retry, throwError, timeout, timer } from 'rxjs';
import { UserService } from '../../services/user.service';
import { NotificationService } from 'src/app/services/notification.service';
import { MiscService } from '../../services/misc.service';
import { PublicUserPayload } from 'src/app/models/Payloads';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  loggedUser: User | null = null;
  showMap: boolean = false;
  showChat: boolean = false;
  showUserMenu: boolean = false;
  showNotifications: boolean = false;
  showFriendRequests: boolean = false;
  nbOfNotificationsF: number = 0;
  searchInput: string = '';
  searchResults: PublicUserPayload[] = [];

  constructor(private userService: UserService, private authService: AuthService, private notificationService: NotificationService, private miscService: MiscService) { }

  // TODO: test this thoroughly (also make it so that this only happens once and not every time the page reloads or smth)
  async ngOnInit() {
    await this.getUserAuth();

    this.getNbOfNotificationsF();
  }

  async getUserAuth() {
    const email = localStorage.getItem('email') ?? '';

    try {
      this.loggedUser = await firstValueFrom(
        this.userService.getUserByEmail(email).pipe(
          retry(11),
          delayWhen((_, attempt) => timer(attempt * 1000)),
          timeout(10000),
          catchError(error => {
            if (error.name === 'TimeoutError') {
              throw new Error('Timeout');
            } else {
              return throwError(() => error);
            }
          })
        )
      );

      if (this.loggedUser) {
        localStorage.setItem('username', this.loggedUser.username);
      }
      else {
        this.logout();
      }
    } catch (err: any) {
      if (err.message === 'Timeout') {
        this.logout();
      }

      if (err.status === 401) {
        window.location.href = '/auth';
      }
    }
  }

  getNbOfNotificationsF() {
    this.notificationService.getNbOfLast51NotificationsF(this.loggedUser!.username).subscribe({
      next: (response) => {
        this.nbOfNotificationsF = response;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  search(): void {
    if (this.searchInput.length >= 3) {
      this.miscService.searchForUsersByInput(this.searchInput).subscribe({
        next: (response) => {
          this.searchResults = response;
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
    else {
      this.searchResults = [];
    }
  }

  createPost(): void {
    window.location.href = '/createPost';
  }

  toggleMap() {
    this.showMap = !this.showMap;
  }

  toggleChat() {
    this.showChat = !this.showChat;
  }

  toggleFriendRequests() {
    this.showFriendRequests = !this.showFriendRequests;
  }

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
  }

  toggleUserMenu() {
    this.showUserMenu = !this.showUserMenu;
  }

  profile() {
    window.location.href = '/user/' + this.loggedUser?.username;
  }

  settings() {
    window.location.href = '/settings';
  }

  mainPage() {
    window.location.href = '/';
  }

  logout() {
    this.authService.logout();
  }

  updateNbOfNotificationsF() {
    --this.nbOfNotificationsF;
  }

  goToUserProfile(usernameOfUser: string) {
    window.location.href = '/user/' + usernameOfUser;
  }
}
