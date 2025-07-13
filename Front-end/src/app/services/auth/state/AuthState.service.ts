import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../../../entity/UnifiedResponse';


@Injectable({
  providedIn: 'root'
})
export class AuthStateService {

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor() {}


  setAuthenticatedUser(user: User): void {
    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
  }


  updateCurrentUser(user: User): void {
    if (this.isAuthenticatedSubject.value) {
      this.currentUserSubject.next(user);
    }
  }


  setAuthenticationStatus(isAuthenticated: boolean): void {
    this.isAuthenticatedSubject.next(isAuthenticated);
    if (!isAuthenticated) {
      this.currentUserSubject.next(null);
    }
  }

 
  resetAuthenticationState(): void {
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }


  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }


  hasAuthenticatedUser(): boolean {
    return this.currentUserSubject.value !== null;
  }


  getAuthenticationStatus(): boolean {
    return this.isAuthenticatedSubject.value;
  }


  getUserProperty<K extends keyof User>(property: K): User[K] | undefined {
    const user = this.getCurrentUser();
    return user ? user[property] : undefined;
  }
//TODO adding role settings to this 
/*
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    if (!user || !user.roles) {
      return false;
    }
    
    // Handle both array and string role formats
    if (Array.isArray(user.roles)) {
      return user.roles.includes(role);
    } else {
      return user.roles === role;
    }
  }*/

/*
  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }*/
/*
  hasAllRoles(roles: string[]): boolean {
    return roles.every(role => this.hasRole(role));
  }*/
}