import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from '../../entity/UnifiedResponse';

@Injectable({ providedIn: 'root' })
export class UserService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Gets the current user from the server.
   * @returns An observable of the user.
   */
  getCurrentUser(): Observable<User> {
    return this.http.get<User>('/api/users/me').pipe(
      catchError(() => {
        // Handle error
        return throwError(() => new Error('Failed to get user'));
      })
    );
  }

  /**
   * Sets the current user.
   * @param user The user to set.
   */
  setCurrentUser(user: User): void {
    this.currentUserSubject.next(user);
  }

  /**
   * Clears the current user.
   */
  clearUser(): void {
    this.currentUserSubject.next(null);
  }
}