import { Component} from '@angular/core';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { FriendsService } from '../../services/friends.service';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent {
  showMap: boolean = false;
  showUserMenu: boolean = false;
  friendsUsernames: string[] = [];
  usernameOfLoggedUser = localStorage.getItem('username') ?? '';

  constructor(private userService: UserService, private authService: AuthService, private friendsService: FriendsService) {
  }

  ngOnInit(): void {
    this.getAllFriendsFromService(this.usernameOfLoggedUser);
  }

  getAllFriendsFromService(username: string) {
    this.friendsService.getAllUsernamesOfFriends(username).subscribe({
      next: (usernames: string[]) => {
        this.friendsUsernames = usernames;
      },
      error: (error) => {
        console.error('Error getting all friends.', error);
      }
    });

  }
}
// TODO: when going back to the previous page when last time the logged user accepted a friend request, instead of "unfriend" the options are still "accept" and "decline" even though that shouldnt happen
