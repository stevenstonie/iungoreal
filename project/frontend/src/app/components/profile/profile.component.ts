import { Component, ViewChild } from '@angular/core';
import { User } from 'src/app/models/user';
import { UserService } from '../../services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  currentUser: User = {} as User;

  constructor(private userService: UserService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    const usernameOfLoggedUser = localStorage.getItem('username');
    const username = this.route.snapshot.paramMap.get('username');

    if (username) {
      console.log('username: ', username);
      console.log('usernameOfLoggedUser: ', usernameOfLoggedUser);
      let isPrivate = false;
      if (username === usernameOfLoggedUser) {
        isPrivate = true;
      }
      
      this.userService.getUserByUsername(username, isPrivate).subscribe({
        next: (user: User) => {
          this.currentUser = user;
        },
        error: (error) => {
          console.error('Error getting user.', error);
          this.router.navigate(['/404']);
        }
      });
    }
  }

  sendFriendRequest() {
    console.log("sent friend request to: ", this.currentUser.username);
  }
}
