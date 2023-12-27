import { Component } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {
  // TODO: implement checking if the user exists before displaying the profile

  sendFriendRequest() {
    console.log('friend request sent to ???');
  }
}
