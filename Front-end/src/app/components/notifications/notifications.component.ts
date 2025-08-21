import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface Notification {
  id: string;
  type: 'info' | 'success' | 'warning' | 'error';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
}

@Component({
  selector: 'app-notifications',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, RouterModule],
  templateUrl: './notifications.component.html',

})
export class NotificationsComponent {
  notifications: Notification[] = [
    {
      id: '1',
      type: 'success',
      title: 'Payment Received',
      message: 'You received $250 from John Doe',
      timestamp: new Date('2024-03-10T14:30:00'),
      read: false
    },
    {
      id: '2',
      type: 'warning',
      title: 'Bill Due Soon',
      message: 'Your electricity bill is due in 3 days',
      timestamp: new Date('2024-03-09T09:15:00'),
      read: false
    },
    {
      id: '3',
      type: 'error',
      title: 'Transfer Failed',
      message: 'Transfer to Jane Smith failed - Insufficient funds',
      timestamp: new Date('2024-03-08T16:45:00'),
      read: true
    }
  ];

  getIcon(type: string): string {
    const icons: { [key: string]: string } = {
      info: 'fa-info-circle',
      success: 'fa-check-circle',
      warning: 'fa-exclamation-triangle',
      error: 'fa-times-circle'
    };
    return icons[type] || 'fa-bell';
  }

  markAllAsRead(): void {
    this.notifications = this.notifications.map(n => ({ ...n, read: true }));
  }

  dismiss(id: string): void {
    this.notifications = this.notifications.filter(n => n.id !== id);
  }
}