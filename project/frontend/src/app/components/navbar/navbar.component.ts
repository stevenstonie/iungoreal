import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { User } from 'src/app/models/user';
import { catchError, delayWhen, firstValueFrom, retry, throwError, timeout, timer } from 'rxjs';
import { UserService } from '../../services/user.service';

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

  constructor(private userService: UserService, private authService: AuthService) { }

  async ngOnInit() {
    try {
      this.loggedUser = await firstValueFrom(
        this.userService.getLoggedUser().pipe(
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

      if (!this.loggedUser) {
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

  search(): void {
    // TODO: Implement search
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
}
