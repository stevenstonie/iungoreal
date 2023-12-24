import { Component} from '@angular/core';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent {
  showMap: boolean = false;
  showUserMenu: boolean = false;
  mapParams: any;

  constructor(private userService: UserService, private AuthService: AuthService) {
  }
}
