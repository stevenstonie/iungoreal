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
  currentPostIndex: number = 0;
  currentPostIndexes: number[] = [];

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
          console.log(posts);

          this.posts = this.posts.concat(posts);

          for (let i = 0; i < posts.length; i++) {
            this.currentPostIndexes.push(0);
          }
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  nextImage(postIndex: number) {
    const currentPost = this.posts[postIndex];
    if (currentPost.mediaLinks.length > 0 && this.currentPostIndexes[postIndex] < currentPost.mediaLinks.length - 1) {
      this.currentPostIndexes[postIndex]++;
    }
  }

  previousImage(postIndex: number) {
    const currentPost = this.posts[postIndex];
    if (currentPost.mediaLinks.length > 0 && this.currentPostIndexes[postIndex] > 0) {
      this.currentPostIndexes[postIndex]--;
    }
  }

  isImage(file: string): boolean {
    return true;
  }

  // TODO: current problems:
  //            -> images from friends posts are not visible.only images from one's own posts
  //            -> videos are not displayed

  onScroll(): void {
    console.log("scrolled");
  }
}
// TODO: when going back to the previous page when last time the logged user accepted a friend request, instead of "unfriend" the options are still "accept" and "decline" even though that shouldnt happen
