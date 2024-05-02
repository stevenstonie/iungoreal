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
  currentPostIndex: number = 0;
  currentPostIndexes: number[] = [];

  constructor(private postService: PostService) {

  }

  ngOnChanges(changes: SimpleChanges): void {
    this.fetchPosts();
  }

  fetchPosts() {
    if (this.isFeed) {
      this.fetchPostsForFeed();
    } else {
      this.fetchPostsOfUser();
    }
  }

  fetchPostsForFeed() {
    if (this.usernameOfUserOnScreen) {
      this.postService.getNextPostsFromFriends(this.usernameOfUserOnScreen, this.posts[this.posts.length - 1]?.id).subscribe({
        next: (posts) => {
          if (posts.length <= 0 && this.posts.length > 0) {
            alert("no more posts");
          }
          console.log(posts);

          this.posts = this.posts.concat(posts);

          for (const element of posts) {
            this.currentPostIndexes.push(0);
          }
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  fetchPostsOfUser() {
    if (this.usernameOfUserOnScreen) {
      this.postService.getNextPostsOfUser(this.usernameOfUserOnScreen, this.posts[this.posts.length - 1]?.id).subscribe({
        next: (posts) => {
          if (posts.length <= 0 && this.posts.length > 0) {
            alert("no more posts");
          }
          console.log(posts);

          this.posts = this.posts.concat(posts);

          for (const element of posts) {
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
    return file.includes('.png') || file.includes('.jpg') || file.includes('.jpeg') || file.includes('.gif') || file.includes('.webp');
  }

  removePost(postIndex: number) {
    const confirmation = window.confirm("Are you sure you want to remove this post?");

    if (confirmation) {
      console.log("to implement");
    }
  }

  onScroll(): void {
    console.log("scrolled");
  }
}
