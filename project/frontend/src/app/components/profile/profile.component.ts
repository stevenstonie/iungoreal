import { Component } from '@angular/core';
import { User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FriendsService } from 'src/app/services/friends.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';
import { JsonString } from 'src/app/models/app';
import { ResponsePayload } from 'src/app/models/payloads';

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
  file: File | null = null;
  previewUrl: SafeUrl | null = null;
  profilePictureUrl: string = 'assets/default-images/default-profile-picture.jpg';
  profileCoverUrl: string = 'assets/default-images/default-cover-photo.jpg';

  constructor(private userService: UserService, private friendsService: FriendsService, private route: ActivatedRoute, private router: Router, private sanitizer: DomSanitizer, private http: HttpClient) { }

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

      this.getUserObjectFromService();

      this.getPfpFromService();
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.file = input.files[0];
    }

    this.saveThePfpFromService();
  }

  getPfpFromService(): void {
    // this.userService.getProfilePicture(this.usernameOfUserOnScreen).subscribe({
    //   next: (pfp: JsonString) => {
    //     if (pfp.string === '' || pfp.string === null) {
    //       return;
    //     }
    //     this.profilePictureUrl = pfp.string;
    //   },
    //   error: (error) => {
    //     console.error(error);
    //   }
    // });
  }

  getUserObjectFromService() {
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

  saveThePfpFromService() {
    // if (this.file) {
    //   this.userService.saveProfilePicture(this.file).subscribe({
    //     next: (response: ResponsePayload) => {
    //       this.getUserPfpFromService();
    //       console.log(response);
    //     },
    //     error: (error) => {
    //       console.error(error);
    //     }
    //   });
    // }
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
        if (response.status === 200) {
          this.loggedUserSentFriendRequest = true;
        }
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.friendsService.checkRequest(this.usernameOfUserOnScreen, this.usernameOfLoggedUser).subscribe({
      next: (response) => {
        console.log(response.message);
        if (response.status === 200) {
          this.userSentFriendRequest = true;
        }
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.friendsService.checkFriendship(this.usernameOfLoggedUser, this.usernameOfUserOnScreen).subscribe({
      next: (response) => {
        console.log(response.message);
        if (response.status === 200) {
          this.isFriends = true;
        }
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
