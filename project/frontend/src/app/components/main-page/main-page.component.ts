import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { catchError, delayWhen, firstValueFrom, retry, throwError, timeout, timer } from 'rxjs';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  currentUser: User | null = null;
  showMap: boolean = false;
  showUserMenu: boolean = false;
  mapParams: any;

  constructor(private userService: UserService, private AuthService: AuthService) {
  }

  async ngOnInit() {
    try {
      this.currentUser = await firstValueFrom(
        this.userService.getCurrentUser().pipe(
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

      if (!this.currentUser) {
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

  toggleMap() {
    this.showMap = !this.showMap;
  }

  toggleUserMenu() {
    this.showUserMenu = !this.showUserMenu;
  }

  profile() {
    window.location.href = '/profile';
  }

  settings() {
    window.location.href = '/settings';
  }

  logout() {
    this.AuthService.logout();
  }
}
