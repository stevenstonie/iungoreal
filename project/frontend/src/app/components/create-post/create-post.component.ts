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
  title: string = '';
  authorUsername: string = localStorage.getItem('username') ?? '';
  description: string | null = null;
  file: File | null = null;
  previewUrl: SafeUrl | null = null;

  constructor(private sanitizer: DomSanitizer, private http: HttpClient, private appService: AppService) {

  }

  ngOnInit(): void {
    
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
    formData.append('authorUsername', this.authorUsername);
    if (this.file) {
      formData.append('file', this.file);
    }
    if(this.description) {
      formData.append('description', this.description);
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
    if (this.previewUrl) {
      const originalUrl = (this.previewUrl as any).changingThisBreaksApplicationSecurity;
      URL.revokeObjectURL(originalUrl);
    }
  }
}
