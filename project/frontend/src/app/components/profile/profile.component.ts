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

    if (this.usernameOfLoggedUser !== this.usernameOfUserOnScreen) {
      this.getFriendshipStatusFromService();
    }
  }

  getUserFromService() {
    if (this.usernameOfUserOnScreen) {
      if (this.usernameOfUserOnScreen === this.usernameOfLoggedUser) {
        this.isUserOnScreenTheLoggedOne = true;
      }

      this.userService.getUserByUsername(this.usernameOfUserOnScreen, this.isUserOnScreenTheLoggedOne).subscribe({
        next: (user: User) => {
          console.log('user: ', user);
          this.userOnScreen = user;
        },
        error: (error) => {
          console.error('Error getting user.', error);
          this.router.navigate(['/404']);
        }
      });
    }
  }

  /*
    the logic is like this:
    the one who sends the friend request is the sender(duuh)
    the one who cancels it is the sender
    the one who accepts or declines is the receiver
    the one who unfriends is the unfriender
    the one who is supposed to do all the actions is the logged user (so careful with the parameters)
  */

  getFriendshipStatusFromService() {
    this.friendsService.checkRequest(this.usernameOfLoggedUser, this.usernameOfUserOnScreen).subscribe({
      next: (response) => {
        console.log(response.message);
        this.loggedUserSentFriendRequest = response.success;
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.friendsService.checkRequest(this.usernameOfUserOnScreen, this.usernameOfLoggedUser).subscribe({
      next: (response) => {
        console.log(response.message);
        this.userSentFriendRequest = response.success;
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.friendsService.checkFriendship(this.usernameOfLoggedUser, this.usernameOfUserOnScreen).subscribe({
      next: (response) => {
        console.log(response.message);
        this.isFriends = response.success;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  sendFriendRequest() {
    this.friendsService.sendFriendRequest(this.usernameOfLoggedUser, this.usernameOfUserOnScreen).subscribe({
      next: (response) => {
        console.log(response);
        this.loggedUserSentFriendRequest = true;
        this.userSentFriendRequest = false;
        this.isFriends = false;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  cancelFriendRequest() {
    this.friendsService.cancelFriendRequest(this.usernameOfLoggedUser, this.usernameOfUserOnScreen).subscribe({
      next: (response) => {
        console.log(response);
        this.loggedUserSentFriendRequest = false;
        this.userSentFriendRequest = false;
        this.isFriends = false;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  acceptFriendRequest() {
    this.friendsService.acceptFriendRequest(this.usernameOfUserOnScreen, this.usernameOfLoggedUser).subscribe({
      next: (response) => {
        console.log(response);
        this.loggedUserSentFriendRequest = false;
        this.userSentFriendRequest = false;
        this.isFriends = true;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  declineFriendRequest() {
    this.friendsService.declineFriendRequest(this.usernameOfUserOnScreen, this.usernameOfLoggedUser).subscribe({
      next: (response) => {
        console.log(response);
        this.loggedUserSentFriendRequest = false;
        this.userSentFriendRequest = false;
        this.isFriends = false;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  unfriend() {
    this.friendsService.unfriend(this.usernameOfLoggedUser, this.usernameOfUserOnScreen).subscribe({
      next: (response) => {
        console.log(response);
        this.loggedUserSentFriendRequest = false;
        this.userSentFriendRequest = false;
        this.isFriends = false;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  editProfile() {
    console.log("to implement");
  }
}
