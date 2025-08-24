import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, tap } from 'rxjs/operators';

export interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  currency: string;
  language: string;
  newsletter: boolean;
  twoFactorAuth: boolean;
  profileImage?: string;
}

@Injectable({
  providedIn: 'root'
})
export class EditProfileService {
  private userProfile = new BehaviorSubject<UserProfile | null>(null);
  private loading = new BehaviorSubject<boolean>(false);

  constructor() {
    // Mock initial data loading
    this.fetchUserProfile();
  }

  getUserProfile(): Observable<UserProfile | null> {
    return this.userProfile.asObservable();
  }

  isLoading(): Observable<boolean> {
    return this.loading.asObservable();
  }

  fetchUserProfile() {
    this.loading.next(true);
    // Simulate API call
    of({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      phone: '123-456-7890',
      currency: 'USD',
      language: 'en',
      newsletter: true,
      twoFactorAuth: false,
      profileImage: 'assets/default-avatar.png'
    }).pipe(delay(1000)).subscribe(profile => {
      this.userProfile.next(profile);
      this.loading.next(false);
    });
  }

  updateUserProfile(profile: UserProfile): Observable<{ success: boolean }> {
    this.loading.next(true);
    // Simulate API call
    return of({ success: true }).pipe(
      delay(2000),
      tap(() => {
        this.userProfile.next(profile);
        this.loading.next(false);
      })
    );
  }
}