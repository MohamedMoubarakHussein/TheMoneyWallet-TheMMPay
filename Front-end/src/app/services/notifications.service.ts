import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  id: string;
  type: 'info' | 'success' | 'warning' | 'error';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
  private notifications = new BehaviorSubject<Notification[]>([]);

  constructor() {
    // Mock initial data
    this.notifications.next([
      { id: '1', type: 'success', title: 'Payment Received', message: 'You received $250 from John Doe', timestamp: new Date('2024-03-10T14:30:00'), read: false },
      { id: '2', type: 'warning', title: 'Bill Due Soon', message: 'Your electricity bill is due in 3 days', timestamp: new Date('2024-03-09T09:15:00'), read: false },
      { id: '3', type: 'error', title: 'Transfer Failed', message: 'Transfer to Jane Smith failed - Insufficient funds', timestamp: new Date('2024-03-08T16:45:00'), read: true }
    ]);
  }

  getNotifications(): Observable<Notification[]> {
    return this.notifications.asObservable();
  }

  markAllAsRead() {
    const currentNotifications = this.notifications.value;
    const updatedNotifications = currentNotifications.map(n => ({ ...n, read: true }));
    this.notifications.next(updatedNotifications);
  }

  dismiss(id: string) {
    const currentNotifications = this.notifications.value;
    const updatedNotifications = currentNotifications.filter(n => n.id !== id);
    this.notifications.next(updatedNotifications);
  }
}