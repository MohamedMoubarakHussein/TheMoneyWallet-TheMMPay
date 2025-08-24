import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { Transaction, ApiResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';

export interface CreateTransactionRequest {
  walletId: string;
  type: 'sent' | 'received' | 'deposit' | 'withdrawal' | 'transfer' | 'payment' | 'refund';
  amount: number;
  currency: string;
  description?: string;
  recipientId?: string;
  recipientEmail?: string;
  recipientPhone?: string;
  category?: string;
  tags?: string[];
  metadata?: Record<string, unknown>;
}

export interface TransactionFilters {
  walletId?: string;
  type?: string;
  status?: string;
  fromDate?: Date;
  toDate?: Date;
  minAmount?: number;
  maxAmount?: number;
  currency?: string;
  category?: string;
  search?: string;
  limit?: number;
  offset?: number;
  sortBy?: 'createdAt' | 'amount' | 'type';
  sortOrder?: 'asc' | 'desc';
}

export interface TransactionStats {
  totalTransactions: number;
  totalSent: number;
  totalReceived: number;
  totalDeposits: number;
  totalWithdrawals: number;
  currentBalance: number;
  currency: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly apiUrl = `${environment.apiUrl}/transactions`;
  private transactionsSubject = new BehaviorSubject<Transaction[]>([]);
  
  public transactions$ = this.transactionsSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Get all transactions with filters
  getTransactions(filters?: TransactionFilters): Observable<Transaction[]> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.walletId) params = params.set('walletId', filters.walletId);
      if (filters.type) params = params.set('type', filters.type);
      if (filters.status) params = params.set('status', filters.status);
      if (filters.fromDate) params = params.set('fromDate', filters.fromDate.toISOString());
      if (filters.toDate) params = params.set('toDate', filters.toDate.toISOString());
      if (filters.minAmount) params = params.set('minAmount', filters.minAmount.toString());
      if (filters.maxAmount) params = params.set('maxAmount', filters.maxAmount.toString());
      if (filters.currency) params = params.set('currency', filters.currency);
      if (filters.category) params = params.set('category', filters.category);
      if (filters.search) params = params.set('search', filters.search);
      if (filters.limit) params = params.set('limit', filters.limit.toString());
      if (filters.offset) params = params.set('offset', filters.offset.toString());
      if (filters.sortBy) params = params.set('sortBy', filters.sortBy);
      if (filters.sortOrder) params = params.set('sortOrder', filters.sortOrder);
    }

    return this.http.get<ApiResponse<Transaction[]>>(`${this.apiUrl}`, { params })
      .pipe(
        map(response => response.data || []),
        tap(transactions => this.transactionsSubject.next(transactions)),
        catchError(this.handleError)
      );
  }

  // Get recent transactions for a wallet
  getRecentTransactions(walletId: string, limit: number = 10): Observable<Transaction[]> {
    return this.getTransactions({
      walletId,
      limit,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    });
  }

  // Get transaction by ID
  getTransactionById(transactionId: string): Observable<Transaction> {
    return this.http.get<ApiResponse<Transaction>>(`${this.apiUrl}/${transactionId}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Create a new transaction
  createTransaction(transactionData: CreateTransactionRequest): Observable<Transaction> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}`, transactionData)
      .pipe(
        map(response => response.data),
        tap(newTransaction => {
          const currentTransactions = this.transactionsSubject.value;
          this.transactionsSubject.next([newTransaction, ...currentTransactions]);
        }),
        catchError(this.handleError)
      );
  }

  // Send money
  sendMoney(data: {
    fromWalletId: string;
    toEmail?: string;
    toPhone?: string;
    toUserId?: string;
    amount: number;
    currency: string;
    description?: string;
  }): Observable<Transaction> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/send`, data)
      .pipe(
        map(response => response.data),
        tap(transaction => {
          const currentTransactions = this.transactionsSubject.value;
          this.transactionsSubject.next([transaction, ...currentTransactions]);
        }),
        catchError(this.handleError)
      );
  }

  // Request money
  requestMoney(data: {
    fromWalletId: string;
    fromEmail?: string;
    fromPhone?: string;
    fromUserId?: string;
    amount: number;
    currency: string;
    description?: string;
  }): Observable<{ success: boolean; requestId: string; message: string }> {
    return this.http.post<ApiResponse<{ success: boolean; requestId: string; message: string }>>(
      `${this.apiUrl}/request`, 
      data
    ).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Add funds to wallet
  addFunds(data: {
    walletId: string;
    amount: number;
    currency: string;
    paymentMethodId: string;
    description?: string;
  }): Observable<Transaction> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/add-funds`, data)
      .pipe(
        map(response => response.data),
        tap(transaction => {
          const currentTransactions = this.transactionsSubject.value;
          this.transactionsSubject.next([transaction, ...currentTransactions]);
        }),
        catchError(this.handleError)
      );
  }

  // Withdraw funds from wallet
  withdrawFunds(data: {
    walletId: string;
    amount: number;
    currency: string;
    bankAccountId: string;
    description?: string;
  }): Observable<Transaction> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/withdraw`, data)
      .pipe(
        map(response => response.data),
        tap(transaction => {
          const currentTransactions = this.transactionsSubject.value;
          this.transactionsSubject.next([transaction, ...currentTransactions]);
        }),
        catchError(this.handleError)
      );
  }

  // Get transaction statistics
  getTransactionStats(walletId: string, period?: 'week' | 'month' | 'year'): Observable<TransactionStats> {
    let params = new HttpParams().set('walletId', walletId);
    if (period) {
      params = params.set('period', period);
    }

    return this.http.get<ApiResponse<TransactionStats>>(`${this.apiUrl}/stats`, { params })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get transaction categories
  getTransactionCategories(): Observable<string[]> {
    return this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/categories`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Update transaction
  updateTransaction(transactionId: string, data: {
    description?: string;
    category?: string;
    tags?: string[];
  }): Observable<Transaction> {
    return this.http.put<ApiResponse<Transaction>>(`${this.apiUrl}/${transactionId}`, data)
      .pipe(
        map(response => response.data),
        tap(updatedTransaction => {
          const currentTransactions = this.transactionsSubject.value;
          const index = currentTransactions.findIndex(t => t.id === transactionId);
          if (index !== -1) {
            currentTransactions[index] = updatedTransaction;
            this.transactionsSubject.next([...currentTransactions]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Cancel transaction (if possible)
  cancelTransaction(transactionId: string): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${transactionId}/cancel`,
      {}
    ).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Get transaction receipt
  getTransactionReceipt(transactionId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${transactionId}/receipt`, {
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Export transactions
  exportTransactions(filters?: TransactionFilters, format: 'csv' | 'pdf' | 'excel' = 'csv'): Observable<Blob> {
    let params = new HttpParams().set('format', format);
    
    if (filters) {
      if (filters.walletId) params = params.set('walletId', filters.walletId);
      if (filters.fromDate) params = params.set('fromDate', filters.fromDate.toISOString());
      if (filters.toDate) params = params.set('toDate', filters.toDate.toISOString());
      // Add other filter parameters as needed
    }

    return this.http.get(`${this.apiUrl}/export`, {
      params,
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Search transactions
  searchTransactions(query: string, walletId?: string, limit: number = 20): Observable<Transaction[]> {
    return this.getTransactions({
      search: query,
      walletId,
      limit,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    });
  }

  // Get pending transactions
  getPendingTransactions(walletId?: string): Observable<Transaction[]> {
    return this.getTransactions({
      walletId,
      status: 'pending',
      sortBy: 'createdAt',
      sortOrder: 'desc'
    });
  }

  // Get failed transactions
  getFailedTransactions(walletId?: string): Observable<Transaction[]> {
    return this.getTransactions({
      walletId,
      status: 'failed',
      sortBy: 'createdAt',
      sortOrder: 'desc'
    });
  }

  // Retry failed transaction
  retryTransaction(transactionId: string): Observable<Transaction> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/${transactionId}/retry`, {})
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get current transactions value
  getCurrentTransactions(): Transaction[] {
    return this.transactionsSubject.value;
  }

  // Clear transactions (for logout)
  clearTransactions(): void {
    this.transactionsSubject.next([]);
  }

  // Refresh transactions
  refreshTransactions(filters?: TransactionFilters): void {
    this.getTransactions(filters).subscribe();
  }

  // Utility methods
  formatTransactionAmount(transaction: Transaction): string {
    const sign = transaction.type === 'received' || transaction.type === 'deposit' ? '+' : '-';
    return `${sign}${Math.abs(transaction.amount).toFixed(2)}`;
  }

  getTransactionStatusColor(status: string): string {
    const colorMap: { [key: string]: string } = {
      'completed': '#4CAF50',
      'pending': '#FF9800',
      'failed': '#F44336',
      'cancelled': '#757575',
      'refunded': '#2196F3'
    };
    return colorMap[status?.toLowerCase()] || '#757575';
  }

  getTransactionTypeIcon(type: string): string {
    const iconMap: { [key: string]: string } = {
      'sent': '📤',
      'received': '📥',
      'deposit': '💰',
      'withdrawal': '💸',
      'transfer': '🔄',
      'payment': '💳',
      'refund': '↩️'
    };
    return iconMap[type?.toLowerCase()] || '💰';
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
   
   console.error('TransactionService Error:', errorMessage);
   return throwError(() => new Error(errorMessage));
 }
}