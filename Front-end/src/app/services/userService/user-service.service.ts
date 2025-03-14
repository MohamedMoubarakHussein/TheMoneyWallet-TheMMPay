// user.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

interface User {
  id: string;
  email: string;
  // Add other user properties
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<User> {
    return this.http.get<User>('/api/users/me', {
      withCredentials: true // For cookie-based auth
    });
  }

  setCurrentUser(user: User): void {
    this.currentUserSubject.next(user);
  }

  clearUser(): void {
    this.currentUserSubject.next(null);
  }
}