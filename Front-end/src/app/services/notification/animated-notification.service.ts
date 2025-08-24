import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';

export type NotificationType = 'success' | 'error' | 'warning' | 'info';

export interface NotificationOptions {
  message: string;
  title?: string;
  type?: NotificationType;
  duration?: number;
  closable?: boolean;
  hasAction?: boolean;
  actionText?: string;
  animationType?: 'slide' | 'fade' | 'zoom' | 'bounce';
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center';
  icon?: string;
  iconClass?: string;
}

export interface NotificationConfig extends NotificationOptions {
  id: string;
  createdAt: Date;
}

@Injectable({
  providedIn: 'root'
})
export class AnimatedNotificationService {
  private notificationsSubject = new Subject<NotificationConfig>();
  private closeNotificationSubject = new Subject<string>();
  private notificationsRegistry = new Map<string, NotificationConfig>();

  public notifications$ = this.notificationsSubject.asObservable();
  public closeNotification$ = this.closeNotificationSubject.asObservable();

  private defaultOptions: NotificationOptions = {
    message: '',
    type: 'info',
    duration: 5000, // ms
    closable: true,
    hasAction: false,
    animationType: 'slide',
    position: 'top-right'
  };

  constructor(private zone: NgZone) {}

  /**
   * Show a notification
   * @param options Notification options
   * @returns The notification ID
   */
  show(options: NotificationOptions): string {
    const config = this.createNotification(options);
    
    this.zone.run(() => {
      this.notificationsSubject.next(config);
    });
    
    this.notificationsRegistry.set(config.id, config);
    
    // Auto-dismiss if duration is set
    if (options.duration !== 0) {
      setTimeout(() => {
        this.close(config.id);
      }, options.duration || this.defaultOptions.duration);
    }
    
    return config.id;
  }

  /**
   * Show a success notification
   * @param message Notification message
   * @param title Optional title
   * @param options Additional options
   */
  success(message: string, title?: string, options?: Partial<NotificationOptions>): string {
    return this.show({
      ...this.defaultOptions,
      ...options,
      message,
      title,
      type: 'success',
      icon: '✅',
      iconClass: 'success-icon'
    });
  }

  /**
   * Show an error notification
   * @param message Notification message
   * @param title Optional title
   * @param options Additional options
   */
  error(message: string, title?: string, options?: Partial<NotificationOptions>): string {
    return this.show({
      ...this.defaultOptions,
      ...options,
      message,
      title,
      type: 'error',
      icon: '❌',
      iconClass: 'error-icon',
      // Longer duration for errors by default
      duration: options?.duration ?? 8000
    });
  }

  /**
   * Show a warning notification
   * @param message Notification message
   * @param title Optional title
   * @param options Additional options
   */
  warning(message: string, title?: string, options?: Partial<NotificationOptions>): string {
    return this.show({
      ...this.defaultOptions,
      ...options,
      message,
      title,
      type: 'warning',
      icon: '⚠️',
      iconClass: 'warning-icon'
    });
  }

  /**
   * Show an info notification
   * @param message Notification message
   * @param title Optional title
   * @param options Additional options
   */
  info(message: string, title?: string, options?: Partial<NotificationOptions>): string {
    return this.show({
      ...this.defaultOptions,
      ...options,
      message,
      title,
      type: 'info',
      icon: 'ℹ️',
      iconClass: 'info-icon'
    });
  }

  /**
   * Close a notification by ID
   * @param id Notification ID
   */
  close(id: string): void {
    if (this.notificationsRegistry.has(id)) {
      this.zone.run(() => {
        this.closeNotificationSubject.next(id);
      });
      
      // Remove from registry after animation completes
      setTimeout(() => {
        this.notificationsRegistry.delete(id);
      }, 500);
    }
  }

  /**
   * Close all notifications
   */
  closeAll(): void {
    this.notificationsRegistry.forEach((_, id) => {
      this.close(id);
    });
  }

  /**
   * Update an existing notification
   * @param id Notification ID
   * @param options New options
   */
  update(id: string, options: Partial<NotificationOptions>): void {
    if (this.notificationsRegistry.has(id)) {
      const current = this.notificationsRegistry.get(id);
      if (current) {
        const updated: NotificationConfig = {
          ...current,
          ...options
        };
        
        this.notificationsRegistry.set(id, updated);
        
        this.zone.run(() => {
          this.notificationsSubject.next(updated);
        });
      }
    }
  }

  /**
   * Create a notification config object
   * @param options Notification options
   */
  private createNotification(options: NotificationOptions): NotificationConfig {
    const id = this.generateId();
    
    return {
      ...this.defaultOptions,
      ...options,
      id,
      createdAt: new Date()
    };
  }

  /**
   * Generate a unique notification ID
   */
  private generateId(): string {
    return 'notification_' + Math.random().toString(36).substring(2, 9);
  }
}
