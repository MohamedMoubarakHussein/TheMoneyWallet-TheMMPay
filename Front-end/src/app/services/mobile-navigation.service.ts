import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class MobileNavigationService {
  private isOpen = new BehaviorSubject<boolean>(false);
  private showUserMenu = new BehaviorSubject<boolean>(false);
  private showNotifications = new BehaviorSubject<boolean>(false);

  constructor(private router: Router) { }

  isOpen$(): Observable<boolean> { return this.isOpen.asObservable(); }
  showUserMenu$(): Observable<boolean> { return this.showUserMenu.asObservable(); }
  showNotifications$(): Observable<boolean> { return this.showNotifications.asObservable(); }

  toggleNavigation() {
    this.isOpen.next(!this.isOpen.value);
  }

  closeNavigation() {
    this.isOpen.next(false);
  }

  toggleUserMenu() {
    this.showUserMenu.next(!this.showUserMenu.value);
    this.showNotifications.next(false);
  }

  toggleNotifications() {
    this.showNotifications.next(!this.showNotifications.value);
    this.showUserMenu.next(false);
  }

  navigateTo(path: string) {
    this.router.navigate([path]);
    this.closeNavigation();
  }
}