import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { AppService } from '../../services/app.service';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss']
})
export class CreatePostComponent implements OnInit, OnDestroy {
  previewUrl: SafeUrl | null = null;
  file: File | null = null;
  title: string = '';
  description: string = '';
  authorUsername: string = localStorage.getItem('username') ?? '';

  constructor(private sanitizer: DomSanitizer, private http: HttpClient, private appService: AppService) {

  }

  ngOnInit(): void {
    this.authorUsername = localStorage.getItem('username') ?? '';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.file = input.files[0];
      const objectUrl = URL.createObjectURL(this.file);
      this.previewUrl = this.sanitizer.bypassSecurityTrustUrl(objectUrl);
    }
  }

  createPost(): void {
    const formData = new FormData();
    formData.append('title', this.title);
    formData.append('description', this.description);
    formData.append('authorUsername', this.authorUsername);
    if (this.file) {
      formData.append('file', this.file);
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
    return this.file?.type.startsWith('image/') ?? false;
  }

  isVideo(file: File): boolean {
    return file?.type.startsWith('video/') ?? false;
  }

  ngOnDestroy(): void {
    if (this.previewUrl) {
      const originalUrl = (this.previewUrl as any).changingThisBreaksApplicationSecurity;
      URL.revokeObjectURL(originalUrl);
    }
  }
}
