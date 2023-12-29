import { Component } from '@angular/core';
import { Role, User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  currentUser: User = {} as User;

  constructor(private userService: UserService, private route: ActivatedRoute) { }

  ngOnInit() {
    const username = this.route.snapshot.paramMap.get('username');
    if (username) {
      this.userService.getUserByUsername(username).subscribe({
        next: (user: User) => {
          this.currentUser = user;
        },
        error: (error) => {
          console.error('Error fetching user by username', error);
        }
      });
    }
  }

    sendFriendRequest() {
      console.log("sent friend request to: ", this.currentUser.username);
    }
  }
