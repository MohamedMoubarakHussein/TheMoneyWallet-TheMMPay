import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { RecurringPayment, ApiResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';

export interface CreateRecurringPaymentRequest {
  walletId: string;
  name: string;
  amount: number;
  currency: string;
  frequency: 'daily' | 'weekly' | 'monthly' | 'yearly';
  nextPaymentDate: Date;
  endDate?: Date;
  recipient: {
    name: string;
    email?: string;
    phone?: string;
    accountNumber?: string;
  };
  description?: string;
}

export interface UpdateRecurringPaymentRequest {
  name?: string;
  amount?: number;
  frequency?: 'daily' | 'weekly' | 'monthly' | 'yearly';
  nextPaymentDate?: Date;
  endDate?: Date;
  recipient?: {
    name?: string;
    email?: string;
    phone?: string;
    accountNumber?: string;
  };
  description?: string;
  isActive?: boolean;
}

export interface RecurringPaymentStats {
  totalRecurringPayments: number;
  activeRecurringPayments: number;
  totalAmount: number;
  currency: string;
  nextPaymentDate: Date;
  frequencyBreakdown: {
    daily: number;
    weekly: number;
    monthly: number;
    yearly: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class RecurringPaymentService {
  private readonly apiUrl = `${environment.apiUrl}/recurring-payments`;
  private recurringPaymentsSubject = new BehaviorSubject<RecurringPayment[]>([]);
  
  public recurringPayments$ = this.recurringPaymentsSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Get all recurring payments for a user
  getRecurringPayments(walletId?: string, isActive?: boolean): Observable<RecurringPayment[]> {
    let params = new URLSearchParams();
    if (walletId) params.set('walletId', walletId);
    if (isActive !== undefined) params.set('isActive', isActive.toString());

    return this.http.get<ApiResponse<RecurringPayment[]>>(`${this.apiUrl}?${params.toString()}`)
      .pipe(
        map(response => response.data || []),
        tap(payments => this.recurringPaymentsSubject.next(payments)),
        catchError(this.handleError)
      );
  }

  // Get recurring payment by ID
  getRecurringPaymentById(paymentId: string): Observable<RecurringPayment> {
    return this.http.get<ApiResponse<RecurringPayment>>(`${this.apiUrl}/${paymentId}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Create new recurring payment
  createRecurringPayment(paymentData: CreateRecurringPaymentRequest): Observable<RecurringPayment> {
    return this.http.post<ApiResponse<RecurringPayment>>(`${this.apiUrl}`, paymentData)
      .pipe(
        map(response => response.data),
        tap(newPayment => {
          const currentPayments = this.recurringPaymentsSubject.value;
          this.recurringPaymentsSubject.next([newPayment, ...currentPayments]);
        }),
        catchError(this.handleError)
      );
  }

  // Update recurring payment
  updateRecurringPayment(paymentId: string, updateData: UpdateRecurringPaymentRequest): Observable<RecurringPayment> {
    return this.http.put<ApiResponse<RecurringPayment>>(`${this.apiUrl}/${paymentId}`, updateData)
      .pipe(
        map(response => response.data),
        tap(updatedPayment => {
          const currentPayments = this.recurringPaymentsSubject.value;
          const index = currentPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            currentPayments[index] = updatedPayment;
            this.recurringPaymentsSubject.next([...currentPayments]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Delete recurring payment
  deleteRecurringPayment(paymentId: string): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/${paymentId}`)
      .pipe(
        map(response => response.data),
        tap(() => {
          const currentPayments = this.recurringPaymentsSubject.value;
          this.recurringPaymentsSubject.next(currentPayments.filter(p => p.id !== paymentId));
        }),
        catchError(this.handleError)
      );
  }

  // Toggle recurring payment active status
  toggleRecurringPaymentStatus(paymentId: string): Observable<RecurringPayment> {
    return this.http.patch<ApiResponse<RecurringPayment>>(`${this.apiUrl}/${paymentId}/toggle`, {})
      .pipe(
        map(response => response.data),
        tap(updatedPayment => {
          const currentPayments = this.recurringPaymentsSubject.value;
          const index = currentPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            currentPayments[index] = updatedPayment;
            this.recurringPaymentsSubject.next([...currentPayments]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Pause recurring payment
  pauseRecurringPayment(paymentId: string): Observable<RecurringPayment> {
    return this.http.patch<ApiResponse<RecurringPayment>>(`${this.apiUrl}/${paymentId}/pause`, {})
      .pipe(
        map(response => response.data),
        tap(updatedPayment => {
          const currentPayments = this.recurringPaymentsSubject.value;
          const index = currentPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            currentPayments[index] = updatedPayment;
            this.recurringPaymentsSubject.next([...currentPayments]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Resume recurring payment
  resumeRecurringPayment(paymentId: string): Observable<RecurringPayment> {
    return this.http.patch<ApiResponse<RecurringPayment>>(`${this.apiUrl}/${paymentId}/resume`, {})
      .pipe(
        map(response => response.data),
        tap(updatedPayment => {
          const currentPayments = this.recurringPaymentsSubject.value;
          const index = currentPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            currentPayments[index] = updatedPayment;
            this.recurringPaymentsSubject.next([...currentPayments]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Skip next payment
  skipNextPayment(paymentId: string): Observable<RecurringPayment> {
    return this.http.patch<ApiResponse<RecurringPayment>>(`${this.apiUrl}/${paymentId}/skip`, {})
      .pipe(
        map(response => response.data),
        tap(updatedPayment => {
          const currentPayments = this.recurringPaymentsSubject.value;
          const index = currentPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            currentPayments[index] = updatedPayment;
            this.recurringPaymentsSubject.next([...currentPayments]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Get recurring payment statistics
  getRecurringPaymentStats(walletId?: string): Observable<RecurringPaymentStats> {
    let params = '';
    if (walletId) params = `?walletId=${walletId}`;

    return this.http.get<ApiResponse<RecurringPaymentStats>>(`${this.apiUrl}/stats${params}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get upcoming payments
  getUpcomingPayments(days: number = 30): Observable<RecurringPayment[]> {
    return this.http.get<ApiResponse<RecurringPayment[]>>(`${this.apiUrl}/upcoming?days=${days}`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Get payment history for a recurring payment
  getPaymentHistory(paymentId: string, limit: number = 20): Observable<{
    id: string;
    amount: number;
    currency: string;
    status: string;
    scheduledDate: Date;
    processedDate?: Date;
    failureReason?: string;
  }[]> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/${paymentId}/history?limit=${limit}`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Clone recurring payment
  cloneRecurringPayment(paymentId: string, modifications?: Partial<CreateRecurringPaymentRequest>): Observable<RecurringPayment> {
    return this.http.post<ApiResponse<RecurringPayment>>(`${this.apiUrl}/${paymentId}/clone`, modifications || {})
      .pipe(
        map(response => response.data),
        tap(newPayment => {
          const currentPayments = this.recurringPaymentsSubject.value;
          this.recurringPaymentsSubject.next([newPayment, ...currentPayments]);
        }),
        catchError(this.handleError)
      );
  }

  // Get current recurring payments value
  getCurrentRecurringPayments(): RecurringPayment[] {
    return this.recurringPaymentsSubject.value;
  }

  // Clear recurring payments (for logout)
  clearRecurringPayments(): void {
    this.recurringPaymentsSubject.next([]);
  }

  // Refresh recurring payments
  refreshRecurringPayments(walletId?: string, isActive?: boolean): void {
    this.getRecurringPayments(walletId, isActive).subscribe();
  }

  // Utility method to calculate next payment date
  calculateNextPaymentDate(
    frequency: 'daily' | 'weekly' | 'monthly' | 'yearly',
    lastPaymentDate: Date,
    endDate?: Date
  ): Date | null {
    const nextDate = new Date(lastPaymentDate);
    
    switch (frequency) {
      case 'daily':
        nextDate.setDate(nextDate.getDate() + 1);
        break;
      case 'weekly':
        nextDate.setDate(nextDate.getDate() + 7);
        break;
      case 'monthly':
        nextDate.setMonth(nextDate.getMonth() + 1);
        break;
      case 'yearly':
        nextDate.setFullYear(nextDate.getFullYear() + 1);
        break;
    }
    
    if (endDate && nextDate > endDate) {
      return null;
    }
    
    return nextDate;
  }

  // Utility method to get frequency display name
  getFrequencyDisplayName(frequency: string): string {
    const displayNames: { [key: string]: string } = {
      'daily': 'Daily',
      'weekly': 'Weekly',
      'monthly': 'Monthly',
      'yearly': 'Yearly'
    };
    return displayNames[frequency] || frequency;
  }

  // Utility method to get frequency icon
  getFrequencyIcon(frequency: string): string {
    const iconMap: { [key: string]: string } = {
      'daily': '📅',
      'weekly': '📆',
      'monthly': '🗓️',
      'yearly': '📅'
    };
    return iconMap[frequency] || '📅';
  }

  // Utility method to check if payment is overdue
  isPaymentOverdue(payment: RecurringPayment): boolean {
    return new Date() > payment.nextPaymentDate;
  }

  // Utility method to get days until next payment
  getDaysUntilNextPayment(payment: RecurringPayment): number {
    const today = new Date();
    const nextPayment = new Date(payment.nextPaymentDate);
    const diffTime = nextPayment.getTime() - today.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      errorMessage = error.error?.message || `Server Error: ${error.status} ${error.statusText}`;
    }
    
    console.error('RecurringPaymentService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
} 