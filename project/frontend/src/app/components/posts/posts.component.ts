import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AppService } from '../../services/app.service';
import { PostPayload } from 'src/app/models/payloads';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss']
})
export class PostsComponent implements OnChanges {
  @Input() usernameOfUserOnScreen: string | undefined;
  posts: PostPayload[] = [];

  constructor(private appService: AppService) {

  }

  ngOnChanges(changes: SimpleChanges): void {
    // if (this.usernameOfUserOnScreen) {
    //   this.appService.getAllPosts(this.usernameOfUserOnScreen).subscribe({
    //     next: (posts) => {
    //       this.posts = posts;
    //     },
    //     error: (error) => {
    //       console.error(error);
    //     }
    //   });
    // }
  }
}
