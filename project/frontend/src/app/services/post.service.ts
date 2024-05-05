import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { CommentPayload, PostPayload, ResponsePayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private postApiUrl = 'http://localhost:8083/api/post';

  constructor(private http: HttpClient) { }

  createPost(formData: FormData): Observable<ResponsePayload> {
    return this.http.post<ResponsePayload>(`${this.postApiUrl}/create`, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  addComment(username: string, content: string, postId: number): Observable<CommentPayload> {
    const params = new HttpParams().set('username', username).set('content', content).set('postId', postId.toString());

    return this.http.post<CommentPayload>(`${this.postApiUrl}/addComment`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  setSeen(username: string, postId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('postId', postId.toString());

    return this.http.post<ResponsePayload>(`${this.postApiUrl}/setSeen`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getNextPosts(authorUsername: string, username: string, lastPostId: number | null, isFeed: boolean): Observable<PostPayload[]> {
    if (isFeed) {
      return this.getNextPostsFromFriends(username, lastPostId);
    } else {
      return this.getNextPostsOfAuthor(authorUsername, username, lastPostId);
    }
  }

  getNextComments(postId: number, lastCommentId: number | null): Observable<CommentPayload[]> {
    let params = new HttpParams().set('postId', postId.toString());

    if (lastCommentId) {
      params = params.set('cursor', lastCommentId.toString());
    }

    return this.http.get<CommentPayload[]>(`${this.postApiUrl}/getNextCommentsOfPost`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  upvotePost(username: string, postId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('postId', postId.toString());

    return this.http.put<ResponsePayload>(`${this.postApiUrl}/upvote`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  downvotePost(username: string, postId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('postId', postId.toString());

    return this.http.put<ResponsePayload>(`${this.postApiUrl}/downvote`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  savePost(username: string, postId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('postId', postId.toString());

    return this.http.put<ResponsePayload>(`${this.postApiUrl}/save`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  removePostById(username: string, postId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('postId', postId.toString());

    return this.http.delete<ResponsePayload>(`${this.postApiUrl}/remove`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  removeComment(username: string, commentId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('commentId', commentId.toString());

    return this.http.delete<ResponsePayload>(`${this.postApiUrl}/removeComment`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // ----------------------------------------------------------

  private getNextPostsOfAuthor(authorUsername: string, username: string, lastPostId: number | null): Observable<PostPayload[]> {
    let params = new HttpParams().set('authorUsername', authorUsername).set('username', username);
    if (lastPostId) {
      params = params.set('cursor', lastPostId.toString());
    }

    return this.http.get<PostPayload[]>(`${this.postApiUrl}/getNextPostsOfAuthor`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  private getNextPostsFromFriends(username: string, lastPostId: number | null): Observable<PostPayload[]> {
    let params = new HttpParams().set('username', username);
    if (lastPostId) {
      params = params.set('cursor', lastPostId.toString());
    }

    return this.http.get<PostPayload[]>(`${this.postApiUrl}/getNextPostsOfFriends`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in post service.'));
  }
}
