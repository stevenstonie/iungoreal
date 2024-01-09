import { Component, OnDestroy } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss']
})
export class CreatePostComponent implements OnDestroy {
  previewUrl: SafeUrl | null = null;
  file: File | null = null;

  constructor(private sanitizer: DomSanitizer) {

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
    // TODO: Implement
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
