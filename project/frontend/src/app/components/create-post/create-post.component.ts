import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss']
})
export class CreatePostComponent implements OnDestroy {
  authorUsername: string = localStorage.getItem('username') ?? '';
  title: string = '';
  description: string | null = null;
  files: File[] = [];
  previewUrls: any[] = [];

  constructor(private sanitizer: DomSanitizer, private http: HttpClient, private postService: PostService) {

  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.files = [];
      this.previewUrls = [];

      this.files = Array.from(input.files);

      for (let file of this.files) {
        const objectUrl: string = URL.createObjectURL(file);
        this.previewUrls.push(this.sanitizer.bypassSecurityTrustUrl(objectUrl));
      }
    }
  }

  createPost(): void {
    // const formData = new FormData();

    // formData.append('title', this.title);
    // formData.append('authorUsername', this.authorUsername);
    // if (this.description) {
    //   formData.append('description', this.description);
    // }
    // if (this.files?.length) {
    //   this.files.forEach((file) => {
    //     formData.append('files', file, file.name);
    //   });
    // }

    // this.postService.createPost(formData).subscribe({
    //   next: (response) => {
    //     alert('Post created successfully');
    //     this.title = this.description = '';
    //     this.previewUrls = this.files = [];
    //   },
    //   error: (error) => {
    //     console.error('Error creating post', error);
    //   }
    // });
  }

  isImage(file: File): boolean {
    return file?.type.startsWith('image/') ?? false;
  }

  isVideo(file: File): boolean {
    return file?.type.startsWith('video/') ?? false;
  }

  ngOnDestroy(): void {
    this.previewUrls.forEach((previewUrl) => {
      URL.revokeObjectURL(previewUrl);
    });
  }
}
