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
  currentPostIndex: number[] = [];
  usernameOfLoggedUser = localStorage.getItem('username');

  constructor(private postService: PostService) {

  }

  ngOnChanges(changes: SimpleChanges): void {
    this.fetchPosts();
  }

  fetchPosts() {
    if (this.usernameOfUserOnScreen) {
      this.postService.getNextPosts(this.usernameOfUserOnScreen, this.posts[this.posts.length - 1]?.id, this.isFeed).subscribe({
        next: (posts) => {
          if (posts.length <= 0 && this.posts.length > 0) {
            alert("no more posts");
          }
          console.log(posts);

          this.posts = this.posts.concat(posts);
          for (const element of posts) {
            this.currentPostIndex.push(0);
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
    if (currentPost.mediaLinks.length > 0 && this.currentPostIndex[postIndex] < currentPost.mediaLinks.length - 1) {
      this.currentPostIndex[postIndex]++;
    }
  }

  previousImage(postIndex: number) {
    const currentPost = this.posts[postIndex];
    if (currentPost.mediaLinks.length > 0 && this.currentPostIndex[postIndex] > 0) {
      this.currentPostIndex[postIndex]--;
    }
  }

  isImage(file: string): boolean {
    return file.includes('.png') || file.includes('.jpg') || file.includes('.jpeg') || file.includes('.gif') || file.includes('.webp');
  }

  removePost(postIndex: number) {
    const confirmation = window.confirm("Are you sure you want to remove this post?");

    if (confirmation && this.usernameOfLoggedUser) {
      this.postService.removePostById(this.usernameOfLoggedUser, this.posts[postIndex].id).subscribe({
        next: (response) => {
          this.posts.splice(postIndex, 1);
          alert("post removed successfully.");
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  onScroll(): void {
    console.log("scrolled");
  }
}
