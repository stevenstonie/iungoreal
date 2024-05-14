import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommentPayload, PostPayload } from 'src/app/models/Payloads';
import { TimeAgoPipe } from 'src/app/pipes/time-ago.pipe';
import { PostService } from 'src/app/services/post.service';


@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss'],
  providers: [TimeAgoPipe]
})
export class PostsComponent implements OnChanges {
  @Input() usernameOfUserOnScreen: string | undefined;
  @Input() isFeed: boolean = false;
  @Input() isThoseUpvoted: boolean = false;
  @Input() isThoseDownvoted: boolean = false;
  @Input() isThoseSaved: boolean = false;
  usernameOfLoggedUser = localStorage.getItem('username');
  posts: PostPayload[] = [];
  currentPostIndex: number[] = [];
  comments: CommentPayload[] = [];
  showComments: boolean = false;
  lastCommentSectionId: number | undefined;
  commentToAdd: string = "";

  constructor(private postService: PostService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.fetchPosts();
  }

  fetchPosts() {
    if (this.usernameOfUserOnScreen && this.usernameOfLoggedUser) {
      if (this.isThoseUpvoted) {
        this.getUpvotedPostsByUserOnScreen();
      } else if (this.isThoseDownvoted) {
        this.getDownvotedPostsByUserOnScreen();
      } else {
        this.getPostsOfUserOnScreen();
      }
    }
  }

  getPostsOfUserOnScreen() {
    this.postService.getNextPosts(this.usernameOfUserOnScreen!, this.usernameOfLoggedUser!, this.posts[this.posts.length - 1]?.id, this.isFeed).subscribe({
      next: (posts) => {
        this.concatPostsAndLog(posts);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getUpvotedPostsByUserOnScreen() {
    this.postService.getNextUpvotedPostsByUser(this.usernameOfLoggedUser!, this.posts[this.posts.length - 1]?.id).subscribe({
      next: (posts) => {
        this.concatPostsAndLog(posts);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getDownvotedPostsByUserOnScreen() {
    this.postService.getNextDownvotedPostsByUser(this.usernameOfLoggedUser!, this.posts[this.posts.length - 1]?.id).subscribe({
      next: (posts) => {
        this.concatPostsAndLog(posts);
      },
      error: (error) => {
        console.error(error);
      }
    });
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
          if (this.posts[postIndex].downvoted) {
            this.posts[postIndex].upvoteScore++;
            this.posts[postIndex].downvoted = false;
          }
          this.posts[postIndex].upvoted = !this.posts[postIndex].upvoted;
          this.posts[postIndex].upvoteScore += this.posts[postIndex].upvoted ? 1 : -1;
          this.posts[postIndex].seen = true;

          console.log(response);
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
          if (this.posts[postIndex].upvoted) {
            this.posts[postIndex].upvoteScore--;
            this.posts[postIndex].upvoted = false;
          }
          this.posts[postIndex].downvoted = !this.posts[postIndex].downvoted;
          this.posts[postIndex].upvoteScore -= this.posts[postIndex].downvoted ? 1 : -1;
          this.posts[postIndex].seen = true;

          console.log(response);
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
          this.posts[postIndex].saved = !this.posts[postIndex].saved;
          this.posts[postIndex].seen = true;

          console.log(response);
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  getNextCommentsOfPost(postIndex: number) {
    this.postService.getNextCommentsOfPost(this.posts[postIndex].id, this.comments[this.comments.length - 1]?.id).subscribe({
      next: (comments) => {
        this.comments = this.comments.concat(comments);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  addComment(postIndex: number) {
    if (this.usernameOfLoggedUser) {
      this.postService.addComment(this.usernameOfLoggedUser, this.commentToAdd, this.posts[postIndex].id).subscribe({
        next: (response) => {
          this.comments = [response, ...this.comments];
          this.posts[postIndex].nbOfComments++;
          this.posts[postIndex].seen = true;
          this.commentToAdd = "";

          console.log(response);
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  removeComment(commentIndex: number) {
    if (this.usernameOfLoggedUser) {
      this.postService.removeComment(this.usernameOfLoggedUser, this.comments[commentIndex].id).subscribe({
        next: (response) => {
          this.comments.splice(commentIndex, 1);
          this.posts[this.lastCommentSectionId!].nbOfComments--;

          console.log("comment removed");

        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  setSeen(postIndex: number) {
    if (!this.posts[postIndex].seen && this.usernameOfLoggedUser) {
      this.postService.setSeen(this.usernameOfLoggedUser, this.posts[postIndex].id).subscribe({
        next: (response) => {
          this.posts[postIndex].seen = true;

          console.log(response);
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  toggleComments(postIndex: number) {
    // toggle the comment section if no comment section was opened before or if the user clicks on the same section again
    if (this.lastCommentSectionId === undefined || postIndex === this.lastCommentSectionId) {
      this.showComments = !this.showComments;
    }

    // open if the section is closed and the user clicks on a different section
    if (!this.showComments && this.lastCommentSectionId != postIndex) {
      this.showComments = true;
    }

    if (this.showComments) {
      // when opening the section if its a new one then reset the comments
      if (this.lastCommentSectionId != postIndex) {
        this.comments = [];
      }
      this.lastCommentSectionId = postIndex;

      this.getNextCommentsOfPost(postIndex);
    }
  }

  concatPostsAndLog(posts: PostPayload[]) {
    if (posts.length <= 0 && this.posts.length > 0) {
      alert("no more posts");
    }
    console.log(posts);

    this.posts = this.posts.concat(posts);
    for (const element of posts) {
      this.currentPostIndex.push(0);
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

  // implement making posts seen when scrolling past them

  onScroll(): void {
    console.log("scrolled");
  }
}
