import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { delay } from 'rxjs';
import { CommentPayload, PostPayload } from 'src/app/models/Payloads';
import { PostService } from 'src/app/services/post.service';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss']
})
export class PostsComponent implements OnChanges {
  @Input() usernameOfUserOnScreen: string | undefined;
  @Input() isFeed: boolean = false;
  usernameOfLoggedUser = localStorage.getItem('username');
  posts: PostPayload[] = [];
  currentPostIndex: number[] = [];
  comments: CommentPayload[] = [];
  showComments: boolean = false;
  lastCommentSectionId: number | undefined;

  constructor(private postService: PostService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.fetchPosts();
  }

  fetchPosts() {
    if (this.usernameOfUserOnScreen && this.usernameOfLoggedUser) {
      this.postService.getNextPosts(this.usernameOfUserOnScreen, this.usernameOfLoggedUser, this.posts[this.posts.length - 1]?.id, this.isFeed).subscribe({
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

  upvotePost(postIndex: number) {
    if (this.usernameOfLoggedUser) {
      this.postService.upvotePost(this.usernameOfLoggedUser, this.posts[postIndex].id).subscribe({
        next: (response) => {
          console.log(response);

          if (this.posts[postIndex].downvoted) {
            this.posts[postIndex].upvoteScore++;
            this.posts[postIndex].downvoted = false;
          }
          this.posts[postIndex].upvoted = !this.posts[postIndex].upvoted;
          this.posts[postIndex].upvoteScore += this.posts[postIndex].upvoted ? 1 : -1;
          this.posts[postIndex].seen = true;
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  downvotePost(postIndex: number) {
    if (this.usernameOfLoggedUser) {
      this.postService.downvotePost(this.usernameOfLoggedUser, this.posts[postIndex].id).subscribe({
        next: (response) => {
          console.log(response);

          if (this.posts[postIndex].upvoted) {
            this.posts[postIndex].upvoteScore--;
            this.posts[postIndex].upvoted = false;
          }
          this.posts[postIndex].downvoted = !this.posts[postIndex].downvoted;
          this.posts[postIndex].upvoteScore -= this.posts[postIndex].downvoted ? 1 : -1;
          this.posts[postIndex].seen = true;
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  savePost(postIndex: number) {
    if (this.usernameOfLoggedUser) {
      this.postService.savePost(this.usernameOfLoggedUser, this.posts[postIndex].id).subscribe({
        next: (response) => {
          console.log(response);

          this.posts[postIndex].saved = !this.posts[postIndex].saved;
          this.posts[postIndex].seen = true;
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  toggleComments(postIndex: number) {
    this.showComments = !this.showComments;

    if (this.showComments) {
      if (this.lastCommentSectionId != postIndex) {
        this.comments = [];
      }
      this.lastCommentSectionId = postIndex;

      this.getNextCommentsOfPost(postIndex);
    }
  }

  getNextCommentsOfPost(postIndex: number) {
    this.postService.getNextComments(this.posts[postIndex].id, this.comments[this.comments.length - 1]?.id).subscribe({
      next: (comments) => {
        this.comments = this.comments.concat(comments);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  // implement making posts seen when scrolling past them

  onScroll(): void {
    console.log("scrolled");
  }
}
