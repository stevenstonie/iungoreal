import { Component } from '@angular/core';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { FriendService } from '../../services/friend.service';
import { PostPayload } from 'src/app/models/Payloads';
import { PostService } from 'src/app/services/post.service';

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
  posts: PostPayload[] = [];

  constructor(private friendService: FriendService, private postService: PostService) {
  }

  ngOnInit(): void {
    this.getAllFriendsFromService(this.usernameOfLoggedUser);

    this.fetchPosts();
  }

  getAllFriendsFromService(username: string) {
    this.friendService.getAllUsernamesOfFriends(username).subscribe({
      next: (usernames: string[]) => {
        this.friendsUsernames = usernames;
      },
      error: (error) => {
        console.error('Error getting all friends.', error);
      }
    });
  }

  fetchPosts() {
    if (this.usernameOfLoggedUser) {
      this.postService.getNextPostsFromFriends(this.usernameOfLoggedUser, this.posts[this.posts.length - 1]?.id).subscribe({
        next: (posts) => {
          if (posts.length <= 0) {
            alert("no more posts");
          }
          this.posts = this.posts.concat(posts);
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  onScroll(event: Event): void {
    console.log("scrolled");
  }
}
// TODO: when going back to the previous page when last time the logged user accepted a friend request, instead of "unfriend" the options are still "accept" and "decline" even though that shouldnt happen
