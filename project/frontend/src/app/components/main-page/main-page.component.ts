import { Component, OnInit } from '@angular/core';
import { Role, User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

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

  ngOnInit() {
    this.userService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
    });
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
      },
      error: (err) => {
        if (err.status === 401) {
          window.location.href = '/auth';
        }
      }
      // TODO: try to get the currentUser before anything is loaded
    });
  }

  logout() {
    this.AuthService.removeCredentialsFromStorage();
    window.location.reload();
  }
}
