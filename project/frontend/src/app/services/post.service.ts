import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { PostInteractionPayload, PostPayload, ResponsePayload } from '../models/Payloads';

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

  getNextPosts(username: string, lastPostId: number | null, isFeed: boolean): Observable<PostPayload[]> {
    if (isFeed) {
      return this.getNextPostsFromFriends(username, lastPostId);
    } else {
      return this.getNextPostsOfUser(username, lastPostId);
    }
  }

  getPostInteractionsForPosts(username: string, postIds: number[]): Observable<PostInteractionPayload[]> {
    const params = new HttpParams().set('username', username).set('postIds', postIds.toString());

    return this.http.get<PostInteractionPayload[]>(`${this.postApiUrl}/getPostInteractionsForPostIds`, { params })
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

  removePostById(username: string, postId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('postId', postId.toString());

    return this.http.delete<ResponsePayload>(`${this.postApiUrl}/remove`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // ----------------------------------------------------------

  private getNextPostsOfUser(username: string, lastPostId: number | null): Observable<PostPayload[]> {
    let params = new HttpParams().set('authorUsername', username);
    if (lastPostId) {
      params = params.set('cursor', lastPostId.toString());
    }

    return this.http.get<PostPayload[]>(`${this.postApiUrl}/getNextPostsOfUser`, { params })
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
