import { Component } from '@angular/core';
import { User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FriendsService } from 'src/app/services/friends.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  userOnScreen: User = {} as User;
  isUserOnScreenTheLoggedOne: boolean = false;
  userSentFriendRequest: boolean = false;
  loggedUserSentFriendRequest: boolean = false;
  isFriends: boolean = false;
  usernameOfLoggedUser = localStorage.getItem('username') ?? '';
  usernameOfUserOnScreen = this.route.snapshot.paramMap.get('username') ?? '';

  constructor(private userService: UserService, private friendsService: FriendsService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.getUserFromService();

    this.getFriendshipStatusFromService();
  }

  getUserFromService() {
    if (this.usernameOfUserOnScreen) {
      if (this.usernameOfUserOnScreen === this.usernameOfLoggedUser) {
        this.isUserOnScreenTheLoggedOne = true;
      }

      this.userService.getUserByUsername(this.usernameOfUserOnScreen, this.isUserOnScreenTheLoggedOne).subscribe({
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

  getFriendshipStatusFromService() {
    console.log("to implement")
  }

  sendFriendRequest() {
    this.friendsService.sendFriendRequest(this.usernameOfLoggedUser, this.usernameOfUserOnScreen).subscribe({
      next: (response) => {
        console.log(response);
        this.userSentFriendRequest = true;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  cancelFriendRequest() {
    console.log("to implement");
  }

  acceptFriendRequest() {
    console.log("to implement");
  }

  declineFriendRequest() {
    console.log("to implement");
  }

  unfriend() {
    console.log("to implement");
  }

  editProfile() {
    console.log("to implement");
  }
}
