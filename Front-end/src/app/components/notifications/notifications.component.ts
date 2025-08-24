import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { NotificationsService, Notification } from '../../services/notifications.service';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './notifications.component.html',
  animations: [
    trigger('listAnimation', [
      transition('* <=> *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(-10px)' }),
          stagger('100ms', animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })))
        ], { optional: true })
      ])
    ])
  ]
})
export class NotificationsComponent {
  notifications$: Observable<Notification[]>;

  constructor(private notificationsService: NotificationsService) {
    this.notifications$ = this.notificationsService.getNotifications();
  }

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
    this.notificationsService.markAllAsRead();
  }

  dismiss(id: string): void {
    this.notificationsService.dismiss(id);
  }
}
