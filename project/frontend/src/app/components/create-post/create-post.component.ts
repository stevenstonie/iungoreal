import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { AppService } from '../../services/app.service';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss']
})
export class CreatePostComponent implements OnDestroy {
  title: string = '';
  authorUsername: string = localStorage.getItem('username') ?? '';
  description: string | null = null;
  files: File[] = [];
  previewUrls: any[] = [];

  constructor(private sanitizer: DomSanitizer, private http: HttpClient, private appService: AppService) {

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
    // const input = event.target as HTMLInputElement;
    // if (input.files?.length) {
    //   this.file = input.files[0];
    //   const objectUrl = URL.createObjectURL(this.file);
    //   this.previewUrl = this.sanitizer.bypassSecurityTrustUrl(objectUrl);
    // }
  }

  createPost(): void {
    const formData = new FormData();
    formData.append('title', this.title);
    formData.append('authorUsername', this.authorUsername);
    if (this.description) {
      formData.append('description', this.description);
    }
    if (this.files?.length) {
      this.files.forEach((file, index) => {
        formData.append(`files[${index}]`, file, file.name);
      });
    }

    this.appService.createPost(formData).subscribe({
      next: (response) => {
        console.log('Post created successfully', response);
      },
      error: (error) => {
        console.error('Error creating post', error);
      }
    });
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
