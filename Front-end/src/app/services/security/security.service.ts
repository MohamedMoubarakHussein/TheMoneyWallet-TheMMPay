import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { SecuritySettings, AuditLog, ApiResponse, TwoFactorSetupResponse, TwoFactorVerificationResponse, SecurityAlert, DeviceSession, SecurityScore } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';

export interface TwoFactorSetupRequest {
  method: 'sms' | 'email' | 'authenticator';
  phoneNumber?: string;
  email?: string;
}

export interface TwoFactorVerificationRequest {
  code: string;
  method: 'sms' | 'email' | 'authenticator';
}

export interface SecuritySettingsUpdateRequest {
  twoFactorEnabled?: boolean;
  twoFactorMethod?: 'sms' | 'email' | 'authenticator';
  loginNotifications?: boolean;
  transactionNotifications?: boolean;
  sessionTimeout?: number;
  maxLoginAttempts?: number;
  requirePasswordChange?: boolean;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface LoginAttempt {
  timestamp: Date;
  ipAddress: string;
  userAgent: string;
  success: boolean;
  failureReason?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SecurityService {
  private readonly apiUrl = `${environment.apiUrl}/security`;
  private securitySettingsSubject = new BehaviorSubject<SecuritySettings | null>(null);
  private auditLogsSubject = new BehaviorSubject<AuditLog[]>([]);
  
  public securitySettings$ = this.securitySettingsSubject.asObservable();
  public auditLogs$ = this.auditLogsSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Get security settings
  getSecuritySettings(): Observable<SecuritySettings> {
    return this.http.get<ApiResponse<SecuritySettings>>(`${this.apiUrl}/settings`)
      .pipe(
        map(response => response.data),
        tap(settings => this.securitySettingsSubject.next(settings)),
        catchError(this.handleError)
      );
  }

  // Update security settings
  updateSecuritySettings(updateData: SecuritySettingsUpdateRequest): Observable<SecuritySettings> {
    return this.http.put<ApiResponse<SecuritySettings>>(`${this.apiUrl}/settings`, updateData)
      .pipe(
        map(response => response.data),
        tap(updatedSettings => this.securitySettingsSubject.next(updatedSettings)),
        catchError(this.handleError)
      );
  }

  // Setup two-factor authentication
  setupTwoFactor(setupData: TwoFactorSetupRequest): Observable<{
    success: boolean;
    message: string;
    qrCode?: string;
    secretKey?: string;
  }> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/2fa/setup`, setupData)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Verify two-factor authentication setup
  verifyTwoFactorSetup(verificationData: TwoFactorVerificationRequest): Observable<{
    success: boolean;
    message: string;
  }> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/2fa/verify`, verificationData)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Enable two-factor authentication
  enableTwoFactor(): Observable<{ success: boolean; message: string }> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/2fa/enable`, {})
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Disable two-factor authentication
  disableTwoFactor(password: string): Observable<{ success: boolean; message: string }> {
    return this.http.post<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/2fa/disable`, { password })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Change password
  changePassword(passwordData: PasswordChangeRequest): Observable<{ success: boolean; message: string }> {
    return this.http.post<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/password/change`, passwordData)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get audit logs
  getAuditLogs(
    limit: number = 50,
    offset: number = 0,
    action?: string,
    resource?: string,
    startDate?: Date,
    endDate?: Date
  ): Observable<AuditLog[]> {
    const params = new URLSearchParams();
    params.set('limit', limit.toString());
    params.set('offset', offset.toString());
    if (action) params.set('action', action);
    if (resource) params.set('resource', resource);
    if (startDate) params.set('startDate', startDate.toISOString());
    if (endDate) params.set('endDate', endDate.toISOString());

    return this.http.get<ApiResponse<AuditLog[]>>(`${this.apiUrl}/audit-logs?${params.toString()}`)
      .pipe(
        map(response => response.data || []),
        tap(logs => this.auditLogsSubject.next(logs)),
        catchError(this.handleError)
      );
  }

  // Get login attempts
  getLoginAttempts(limit: number = 20): Observable<LoginAttempt[]> {
    return this.http.get<ApiResponse<LoginAttempt[]>>(`${this.apiUrl}/login-attempts?limit=${limit}`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Get security alerts
  getSecurityAlerts(): Observable<SecurityAlert[]> {
    return this.http.get<ApiResponse<SecurityAlert[]>>(`${this.apiUrl}/alerts`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Mark security alert as read
  markAlertAsRead(alertId: string): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/alerts/${alertId}/read`, {})
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get device sessions
  getDeviceSessions(): Observable<DeviceSession[]> {
    return this.http.get<ApiResponse<DeviceSession[]>>(`${this.apiUrl}/sessions`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Revoke device session
  revokeSession(sessionId: string): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/sessions/${sessionId}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Revoke all other sessions
  revokeAllOtherSessions(): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/sessions/revoke-others`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get security score
  getSecurityScore(): Observable<{
    score: number;
    maxScore: number;
    percentage: number;
    recommendations: string[];
  }> {
    return this.http.get<ApiResponse<SecurityScore>>(`${this.apiUrl}/score`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Export security report
  exportSecurityReport(format: 'pdf' | 'csv' = 'pdf'): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/report?format=${format}`, {
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Get current security settings value
  getCurrentSecuritySettings(): SecuritySettings | null {
    return this.securitySettingsSubject.value;
  }

  // Get current audit logs value
  getCurrentAuditLogs(): AuditLog[] {
    return this.auditLogsSubject.value;
  }

  // Clear security data (for logout)
  clearSecurityData(): void {
    this.securitySettingsSubject.next(null);
    this.auditLogsSubject.next([]);
  }

  // Refresh security settings
  refreshSecuritySettings(): void {
    this.getSecuritySettings().subscribe();
  }

  // Utility method to check if 2FA is enabled
  isTwoFactorEnabled(): boolean {
    const settings = this.securitySettingsSubject.value;
    return settings?.twoFactorEnabled || false;
  }

  // Utility method to get 2FA method
  getTwoFactorMethod(): 'sms' | 'email' | 'authenticator' | null {
    const settings = this.securitySettingsSubject.value;
    return settings?.twoFactorMethod || null;
  }

  // Utility method to check if login notifications are enabled
  areLoginNotificationsEnabled(): boolean {
    const settings = this.securitySettingsSubject.value;
    return settings?.loginNotifications || false;
  }

  // Utility method to check if transaction notifications are enabled
  areTransactionNotificationsEnabled(): boolean {
    const settings = this.securitySettingsSubject.value;
    return settings?.transactionNotifications || false;
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      errorMessage = error.error?.message || `Server Error: ${error.status} ${error.statusText}`;
    }
    
    console.error('SecurityService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
} 