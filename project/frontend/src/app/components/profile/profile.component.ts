import { Component} from '@angular/core';
import { User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  userOnScreen: User = {} as User;
  isUserOnScreenTheLoggedOne: boolean = false;

  constructor(private userService: UserService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    const usernameOfLoggedUser = localStorage.getItem('username');
    const username = this.route.snapshot.paramMap.get('username');

    if (username) {
      if (username === usernameOfLoggedUser) {
        this.isUserOnScreenTheLoggedOne = true;
      }

      this.userService.getUserByUsername(username, this.isUserOnScreenTheLoggedOne).subscribe({
        next: (user: User) => {
          this.userOnScreen = user;
          console.log('user: ', user);
        },
        error: (error) => {
          console.error('Error getting user.', error);
          this.router.navigate(['/404']);
        }
      });
    }
  }

  sendFriendRequest() {
    console.log("sent friend request to: ", this.userOnScreen.username);
  }
}
