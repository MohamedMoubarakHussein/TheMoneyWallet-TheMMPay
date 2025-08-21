import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  AnimatedNotificationService, 
  NotificationType, 
  NotificationConfig 
} from '../../services/notification/animated-notification.service';
import { 
  trigger, 
  state, 
  style, 
  transition, 
  animate, 
  query, 
  stagger, 
  keyframes 
} from '@angular/animations';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-animated-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notifications-container" [ngClass]="position">
      <div 
        *ngFor="let notification of notifications" 
        class="notification-item" 
        [ngClass]="[notification.type || 'info', notification.animationType || 'slide']"
        [@notificationAnimation]="getAnimationState(notification)"
        (@notificationAnimation.done)="animationDone($event, notification.id)"
      >
        <div class="notification-icon" *ngIf="notification.icon">
          <span [ngClass]="notification.iconClass">{{ notification.icon }}</span>
        </div>
        <div class="notification-content">
          <div class="notification-header" *ngIf="notification.title">
            <h4 class="notification-title">{{ notification.title }}</h4>
          </div>
          <div class="notification-body">
            <p class="notification-message">{{ notification.message }}</p>
          </div>
          <div class="notification-actions" *ngIf="notification.hasAction">
            <button class="action-button" (click)="onAction(notification)">
              {{ notification.actionText || 'Action' }}
            </button>
          </div>
        </div>
        <button 
          *ngIf="notification.closable !== false"
          class="notification-close"
          (click)="closeNotification(notification.id)"
        >
          ×
        </button>

        <!-- Auto-dismiss progress bar -->
        <div *ngIf="notification.duration && notification.duration > 0" class="auto-dismiss-progress">
          <div 
            class="progress-bar" 
            [style.animation-duration]="notification.duration + 'ms'"
          ></div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host {
      position: fixed;
      z-index: 9999;
      pointer-events: none;
    }
    
    .notifications-container {
      position: fixed;
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      max-width: 400px;
      width: 100%;
      padding: 1rem;
      pointer-events: none;
    }
    
    .notifications-container.top-right {
      top: 0;
      right: 0;
    }
    
    .notifications-container.top-left {
      top: 0;
      left: 0;
    }
    
    .notifications-container.bottom-right {
      bottom: 0;
      right: 0;
    }
    
    .notifications-container.bottom-left {
      bottom: 0;
      left: 0;
    }
    
    .notifications-container.top-center {
      top: 0;
      left: 50%;
      transform: translateX(-50%);
    }
    
    .notifications-container.bottom-center {
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
    }
    
    .notification-item {
      display: flex;
      position: relative;
      padding: 1rem 1rem 1.25rem;
      border-radius: 0.5rem;
      background-color: white;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      overflow: hidden;
      pointer-events: auto;
      width: 100%;
      max-width: 100%;
    }
    
    .notification-item.success {
      border-left: 4px solid #10b981;
    }
    
    .notification-item.error {
      border-left: 4px solid #ef4444;
    }
    
    .notification-item.warning {
      border-left: 4px solid #f59e0b;
    }
    
    .notification-item.info {
      border-left: 4px solid #3b82f6;
    }
    
    .notification-icon {
      display: flex;
      align-items: flex-start;
      justify-content: center;
      margin-right: 0.75rem;
      font-size: 1.25rem;
    }
    
    .success-icon {
      color: #10b981;
    }
    
    .error-icon {
      color: #ef4444;
    }
    
    .warning-icon {
      color: #f59e0b;
    }
    
    .info-icon {
      color: #3b82f6;
    }
    
    .notification-content {
      flex: 1;
      min-width: 0;
    }
    
    .notification-header {
      margin-bottom: 0.25rem;
    }
    
    .notification-title {
      margin: 0;
      font-size: 1rem;
      font-weight: 600;
      color: #1e293b;
    }
    
    .notification-message {
      margin: 0;
      font-size: 0.875rem;
      color: #64748b;
      line-height: 1.5;
    }
    
    .notification-actions {
      margin-top: 0.75rem;
    }
    
    .action-button {
      padding: 0.375rem 0.75rem;
      border-radius: 0.375rem;
      border: none;
      background-color: #f1f5f9;
      color: #1e293b;
      font-size: 0.75rem;
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.2s ease;
    }
    
    .action-button:hover {
      background-color: #e2e8f0;
    }
    
    .notification-close {
      background: none;
      border: none;
      color: #94a3b8;
      font-size: 1.25rem;
      cursor: pointer;
      padding: 0;
      margin-left: 0.5rem;
      line-height: 1;
      opacity: 0.7;
      transition: opacity 0.2s ease;
    }
    
    .notification-close:hover {
      opacity: 1;
    }
    
    .auto-dismiss-progress {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 3px;
      background-color: rgba(0, 0, 0, 0.05);
      overflow: hidden;
    }
    
    .progress-bar {
      height: 100%;
      width: 100%;
      transform-origin: left center;
      animation-name: shrink;
      animation-timing-function: linear;
      animation-fill-mode: forwards;
    }
    
    .notification-item.success .progress-bar {
      background-color: #10b981;
    }
    
    .notification-item.error .progress-bar {
      background-color: #ef4444;
    }
    
    .notification-item.warning .progress-bar {
      background-color: #f59e0b;
    }
    
    .notification-item.info .progress-bar {
      background-color: #3b82f6;
    }
    
    @keyframes shrink {
      from { transform: scaleX(1); }
      to { transform: scaleX(0); }
    }
  `],
  animations: [
    trigger('notificationAnimation', [
      // Slide Animation (Default)
      state('slide-enter', style({ transform: 'translateX(0)', opacity: 1 })),
      state('slide-leave', style({ transform: 'translateX(100%)', opacity: 0 })),
      transition('void => slide-enter', [
        style({ transform: 'translateX(110%)', opacity: 0 }),
        animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition('slide-enter => slide-leave', [
        animate('200ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(110%)', opacity: 0 }))
      ]),
      
      // Fade Animation
      state('fade-enter', style({ opacity: 1 })),
      state('fade-leave', style({ opacity: 0 })),
      transition('void => fade-enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ]),
      transition('fade-enter => fade-leave', [
        animate('200ms ease-in', style({ opacity: 0 }))
      ]),
      
      // Zoom Animation
      state('zoom-enter', style({ transform: 'scale(1)', opacity: 1 })),
      state('zoom-leave', style({ transform: 'scale(0.8)', opacity: 0 })),
      transition('void => zoom-enter', [
        style({ transform: 'scale(0.8)', opacity: 0 }),
        animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'scale(1)', opacity: 1 }))
      ]),
      transition('zoom-enter => zoom-leave', [
        animate('200ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'scale(0.8)', opacity: 0 }))
      ]),
      
      // Bounce Animation
      state('bounce-enter', style({ transform: 'translateY(0)', opacity: 1 })),
      state('bounce-leave', style({ transform: 'translateY(-100%)', opacity: 0 })),
      transition('void => bounce-enter', [
        animate('500ms cubic-bezier(.51,-0.65,.53,1.52)', keyframes([
          style({ transform: 'translateY(-120%)', opacity: 0, offset: 0 }),
          style({ transform: 'translateY(15px)', opacity: 1, offset: 0.7 }),
          style({ transform: 'translateY(0)', opacity: 1, offset: 1.0 })
        ]))
      ]),
      transition('bounce-enter => bounce-leave', [
        animate('200ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateY(-100%)', opacity: 0 }))
      ])
    ])
  ]
})
export class AnimatedNotificationComponent implements OnInit, OnDestroy {
  notifications: NotificationConfig[] = [];
  position: string = 'top-right';
  
  private notificationStates = new Map<string, 'enter' | 'leave'>();
  private subscriptions: Subscription[] = [];
  private removalQueue: string[] = [];

  constructor(private notificationService: AnimatedNotificationService) {}

  ngOnInit(): void {
    // Subscribe to new notifications
    this.subscriptions.push(
      this.notificationService.notifications$.subscribe(notification => {
        // Handle either new or updated notification
        const existingIndex = this.notifications.findIndex(n => n.id === notification.id);
        
        if (existingIndex >= 0) {
          // Update existing notification
          this.notifications[existingIndex] = notification;
        } else {
          // Add new notification
          this.notifications = [...this.notifications, notification];
          this.notificationStates.set(notification.id, 'enter');
        }
        
        // Update position if needed
        if (notification.position) {
          this.position = notification.position;
        }
      })
    );
    
    // Subscribe to notification close events
    this.subscriptions.push(
      this.notificationService.closeNotification$.subscribe(id => {
        const index = this.notifications.findIndex(n => n.id === id);
        
        if (index >= 0) {
          this.notificationStates.set(id, 'leave');
          this.removalQueue.push(id);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  /**
   * Get the current animation state for a notification
   */
  getAnimationState(notification: NotificationConfig): string {
    const animationType = notification.animationType || 'slide';
    const state = this.notificationStates.get(notification.id) || 'enter';
    return `${animationType}-${state}`;
  }

  /**
   * Handle animation completion
   */
  animationDone(event: any, id: string): void {
    if (event.toState.includes('leave')) {
      const index = this.notifications.findIndex(n => n.id === id);
      
      if (index >= 0) {
        this.notifications = this.notifications.filter(n => n.id !== id);
        this.notificationStates.delete(id);
      }
    }
  }

  /**
   * Handle notification action click
   */
  onAction(notification: NotificationConfig): void {
    // You can add custom action handling here
    this.closeNotification(notification.id);
  }

  /**
   * Close a notification
   */
  closeNotification(id: string): void {
    this.notificationService.close(id);
  }
}
