import { Component, OnInit } from '@angular/core';
import { Role, User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { catchError, delayWhen, firstValueFrom, retry, throwError, timeout, timer } from 'rxjs';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  currentUser: User;
  showMap: boolean = false;
  mapParams: any;

  toggleMap() {
    this.showMap = !this.showMap;
  }

  constructor(private userService: UserService, private AuthService: AuthService) {
    this.currentUser = { id: 0, email: '', password: '', firstname: '', lastname: '', role: Role.USER };
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

  logout() {
    this.AuthService.logout();
  }
}
