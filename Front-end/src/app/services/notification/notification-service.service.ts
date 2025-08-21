import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError, timer } from 'rxjs';
import { catchError, tap, map, switchMap } from 'rxjs/operators';
import { Notification, ApiResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';

export interface CreateNotificationRequest {
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  userId?: string;
  actionUrl?: string;
  expiresAt?: Date;
}

export interface NotificationFilters {
  read?: boolean;
  type?: string;
  fromDate?: Date;
  toDate?: Date;
  limit?: number;
  offset?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly apiUrl = `${environment.apiUrl}/notifications`;
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  private unreadCountSubject = new BehaviorSubject<number>(0);
  
  public notifications$ = this.notificationsSubject.asObservable();
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {
    // Auto-refresh notifications every 30 seconds
    this.startAutoRefresh();
  }

  // Get all notifications
  getNotifications(filters?: NotificationFilters): Observable<Notification[]> {
    let params: any = {};
    
    if (filters) {
      if (filters.read !== undefined) params.read = filters.read;
      if (filters.type) params.type = filters.type;
      if (filters.fromDate) params.fromDate = filters.fromDate.toISOString();
      if (filters.toDate) params.toDate = filters.toDate.toISOString();
      if (filters.limit) params.limit = filters.limit;
      if (filters.offset) params.offset = filters.offset;
    }

    return this.http.get<ApiResponse<Notification[]>>(`${this.apiUrl}`, { params })
      .pipe(
        map(response => response.data || []),
        tap(notifications => {
          this.notificationsSubject.next(notifications);
          this.updateUnreadCount(notifications);
        }),
        catchError(this.handleError)
      );
  }

  // Get unread notifications
  getUnreadNotifications(): Observable<Notification[]> {
    return this.getNotifications({ read: false });
  }

  // Get notification by ID
  getNotificationById(notificationId: string): Observable<Notification> {
    return this.http.get<ApiResponse<Notification>>(`${this.apiUrl}/${notificationId}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Mark notification as read
  markAsRead(notificationId: string): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${notificationId}/read`,
      { read: true }
    ).pipe(
      map(response => response.data),
      tap(() => {
        this.updateNotificationReadStatus(notificationId, true);
      }),
      catchError(this.handleError)
    );
  }

  // Mark notification as unread
  markAsUnread(notificationId: string): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${notificationId}/read`,
      { read: false }
    ).pipe(
      map(response => response.data),
      tap(() => {
        this.updateNotificationReadStatus(notificationId, false);
      }),
      catchError(this.handleError)
    );
  }

  // Mark multiple notifications as read
  markMultipleAsRead(notificationIds: string[]): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/bulk-read`,
      { notificationIds, read: true }
    ).pipe(
      map(response => response.data),
      tap(() => {
        notificationIds.forEach(id => {
          this.updateNotificationReadStatus(id, true);
        });
      }),
      catchError(this.handleError)
    );
  }

  // Mark all notifications as read
  markAllAsRead(): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/mark-all-read`,
      {}
    ).pipe(
      map(response => response.data),
      tap(() => {
        const currentNotifications = this.notificationsSubject.value;
        const updatedNotifications = currentNotifications.map(n => ({ ...n, read: true }));
        this.notificationsSubject.next(updatedNotifications);
        this.unreadCountSubject.next(0);
      }),
      catchError(this.handleError)
    );
  }

  // Delete notification
  deleteNotification(notificationId: string): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${notificationId}`
    ).pipe(
      map(response => response.data),
      tap(() => {
        const currentNotifications = this.notificationsSubject.value;
        const filteredNotifications = currentNotifications.filter(n => n.id !== notificationId);
        this.notificationsSubject.next(filteredNotifications);
        this.updateUnreadCount(filteredNotifications);
      }),
      catchError(this.handleError)
    );
  }

  // Clear all notifications
  clearAll(): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/clear-all`
    ).pipe(
      map(response => response.data),
      tap(() => {
        this.notificationsSubject.next([]);
        this.unreadCountSubject.next(0);
      }),
      catchError(this.handleError)
    );
  }

  // Create notification (admin/system use)
  createNotification(notificationData: CreateNotificationRequest): Observable<Notification> {
    return this.http.post<ApiResponse<Notification>>(`${this.apiUrl}`, notificationData)
      .pipe(
        map(response => response.data),
        tap(newNotification => {
          const currentNotifications = this.notificationsSubject.value;
          this.notificationsSubject.next([newNotification, ...currentNotifications]);
          this.updateUnreadCount([newNotification, ...currentNotifications]);
        }),
        catchError(this.handleError)
      );
  }

  // Get notification count
  getNotificationCount(): Observable<{ total: number; unread: number }> {
    return this.http.get<ApiResponse<{ total: number; unread: number }>>(
      `${this.apiUrl}/count`
    ).pipe(
      map(response => response.data),
      tap(count => {
        this.unreadCountSubject.next(count.unread);
      }),
      catchError(this.handleError)
    );
  }

  // Subscribe to real-time notifications (if WebSocket is available)
  subscribeToRealTimeNotifications(): Observable<Notification> {
    // This would typically use WebSocket or Server-Sent Events
    // For now, return an empty observable
    return new Observable<Notification>(observer => {
      // WebSocket implementation would go here
      // const ws = new WebSocket(`${environment.wsUrl}/notifications`);
      // ws.onmessage = (event) => {
      //   const notification = JSON.parse(event.data);
      //   observer.next(notification);
      // };
      
      return () => {
        // Cleanup WebSocket connection
      };
    });
  }

  // Auto-refresh notifications
  private startAutoRefresh(): void {
    timer(0, 30000) // Refresh every 30 seconds
      .pipe(
        switchMap(() => this.getNotifications({ limit: 20 }))
      )
      .subscribe({
        next: () => {
          // Notifications updated via tap operator
        },
        error: (error) => {
          console.error('Auto-refresh error:', error);
        }
      });
  }

  // Update notification read status locally
  private updateNotificationReadStatus(notificationId: string, read: boolean): void {
    const currentNotifications = this.notificationsSubject.value;
    const updatedNotifications = currentNotifications.map(notification => 
      notification.id === notificationId 
        ? { ...notification, read } 
        : notification
    );
    this.notificationsSubject.next(updatedNotifications);
    this.updateUnreadCount(updatedNotifications);
  }

  // Update unread count
  private updateUnreadCount(notifications: Notification[]): void {
    const unreadCount = notifications.filter(n => !n.read).length;
    this.unreadCountSubject.next(unreadCount);
  }

  // Get current notifications value
  getCurrentNotifications(): Notification[] {
    return this.notificationsSubject.value;
  }

  // Get current unread count
  getCurrentUnreadCount(): number {
    return this.unreadCountSubject.value;
  }

  // Clear notifications (for logout)
  clearNotifications(): void {
    this.notificationsSubject.next([]);
    this.unreadCountSubject.next(0);
  }

  // Refresh notifications
  refreshNotifications(): void {
    this.getNotifications().subscribe();
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = error.error?.message || `Server Error: ${error.status} ${error.statusText}`;
    }
    
    console.error('NotificationService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}