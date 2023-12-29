import { Component } from '@angular/core';
import { Role, User } from 'src/app/models/user';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  currentUser: User = {
    email: 'dumb@eemaile.coc',
    username: 'dumb',
    role: Role.USER,
    id: 0,
    password: '',
    createdAt: 
    new Date()
  };
  // TODO: implement checking if the user exists before displaying the profile

  sendFriendRequest() {
    console.log('friend request sent to ???');
  }
}
