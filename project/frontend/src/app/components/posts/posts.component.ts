import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { PostPayload } from 'src/app/models/Payloads';
import { PostService } from 'src/app/services/post.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss']
})
export class PostsComponent implements OnChanges {
  @Input() usernameOfUserOnScreen: string | undefined;
  @Input() isFeed: boolean = false;
  posts: PostPayload[] = [];

  constructor(private postService: PostService) {

  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.usernameOfUserOnScreen) {
      if (this.isFeed) {
        this.postService.getNextPostsFromFriends(this.usernameOfUserOnScreen, null).subscribe({
          next: (posts) => {
            this.posts = posts;
          },
          error: (error) => {
            console.error(error);
          }
        });
      }
      else {
        this.postService.getAllPostsOfUser(this.usernameOfUserOnScreen).subscribe({
          next: (posts) => {
            this.posts = posts;
          },
          error: (error) => {
            console.error(error);
          }
        });
      }
    }
  }

  onScroll(event: Event): void {
    console.log("scrolled");
  }
}
