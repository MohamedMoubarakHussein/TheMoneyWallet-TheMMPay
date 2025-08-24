import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnimatedNotificationService, NotificationConfig } from '../../services/notification/animated-notification.service';
import { NotificationAnimationService } from '../../services/notification-animation.service';
import { Subscription } from 'rxjs';
import { AnimationEvent } from '@angular/animations';

@Component({
  selector: 'app-animated-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './animated-notification.component.html',
  animations: [NotificationAnimationService.prototype.notificationAnimation]
})
export class AnimatedNotificationComponent implements OnInit, OnDestroy {
  notifications: NotificationConfig[] = [];
  position = 'top-right';
  
  private notificationStates = new Map<string, 'enter' | 'leave'>();
  private subscriptions: Subscription[] = [];

  constructor(private notificationService: AnimatedNotificationService) {}

  ngOnInit(): void {
    this.subscriptions.push(
      this.notificationService.notifications$.subscribe(notification => {
        if (!this.notifications.find(n => n.id === notification.id)) {
          this.notifications.push(notification);
          this.notificationStates.set(notification.id, 'enter');
        }
        if (notification.position) this.position = notification.position;
      }),
      this.notificationService.closeNotification$.subscribe(id => {
        if (this.notifications.find(n => n.id === id)) {
          this.notificationStates.set(id, 'leave');
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getAnimationState(notification: NotificationConfig): string {
    return `${notification.animationType || 'slide'}-${this.notificationStates.get(notification.id) || 'enter'}`;
  }

  animationDone(event: AnimationEvent, id: string): void {
    if (event.toState.includes('leave')) {
      this.notifications = this.notifications.filter(n => n.id !== id);
      this.notificationStates.delete(id);
    }
  }

  onAction(notification: NotificationConfig): void {
    this.closeNotification(notification.id);
  }

  closeNotification(id: string): void {
    this.notificationService.close(id);
  }
}