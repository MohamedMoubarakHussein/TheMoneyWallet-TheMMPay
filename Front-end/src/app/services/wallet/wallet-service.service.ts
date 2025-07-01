import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { UserWallet, ApiResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';

export interface CreateWalletRequest {
  name: string;
  type: 'checking' | 'savings' | 'credit' | 'crypto' | 'business' | 'investment';
  currency: string;
  initialBalance?: number;
  description?: string;
}

export interface UpdateWalletRequest {
  name?: string;
  description?: string;
  currency?: string;
}

export interface TransferRequest {
  fromWalletId: string;
  toWalletId: string;
  amount: number;
  description?: string;
}

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  private readonly apiUrl = `${environment.apiUrl}/wallets`;
  private walletsSubject = new BehaviorSubject<UserWallet[]>([]);
  
  public wallets$ = this.walletsSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Get all user wallets
  getWallets(): Observable<UserWallet[]> {
    return this.http.get<ApiResponse<UserWallet[]>>(`${this.apiUrl}`)
      .pipe(
        map(response => response.data || []),
        tap(wallets => this.walletsSubject.next(wallets)),
        catchError(this.handleError)
      );
  }

  // Get wallet by ID
  getWalletById(walletId: string): Observable<UserWallet> {
    return this.http.get<ApiResponse<UserWallet>>(`${this.apiUrl}/${walletId}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Create new wallet
  createWallet(walletData: CreateWalletRequest): Observable<UserWallet> {
    return this.http.post<ApiResponse<UserWallet>>(`${this.apiUrl}`, walletData)
      .pipe(
        map(response => response.data),
        tap(newWallet => {
          const currentWallets = this.walletsSubject.value;
          this.walletsSubject.next([...currentWallets, newWallet]);
        }),
        catchError(this.handleError)
      );
  }

  // Update wallet
  updateWallet(walletId: string, walletData: UpdateWalletRequest): Observable<UserWallet> {
    return this.http.put<ApiResponse<UserWallet>>(`${this.apiUrl}/${walletId}`, walletData)
      .pipe(
        map(response => response.data),
        tap(updatedWallet => {
          const currentWallets = this.walletsSubject.value;
          const index = currentWallets.findIndex(w => w.id === walletId);
          if (index !== -1) {
            currentWallets[index] = updatedWallet;
            this.walletsSubject.next([...currentWallets]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Update primary wallet
  updatePrimaryWallet(walletId: string): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${walletId}/set-primary`, 
      {}
    ).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Activate/Deactivate wallet
  toggleWalletStatus(walletId: string, status: 'active' | 'inactive'): Observable<UserWallet> {
    return this.http.patch<ApiResponse<UserWallet>>(
      `${this.apiUrl}/${walletId}/status`,
      { status }
    ).pipe(
      map(response => response.data),
      tap(updatedWallet => {
        const currentWallets = this.walletsSubject.value;
        const index = currentWallets.findIndex(w => w.id === walletId);
        if (index !== -1) {
          currentWallets[index] = updatedWallet;
          this.walletsSubject.next([...currentWallets]);
        }
      }),
      catchError(this.handleError)
    );
  }

  // Delete wallet
  deleteWallet(walletId: string): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${walletId}`
    ).pipe(
      map(response => response.data),
      tap(() => {
        const currentWallets = this.walletsSubject.value;
        const filteredWallets = currentWallets.filter(w => w.id !== walletId);
        this.walletsSubject.next(filteredWallets);
      }),
      catchError(this.handleError)
    );
  }

  // Transfer between wallets
  transferBetweenWallets(transferData: TransferRequest): Observable<{ success: boolean; message: string }> {
    return this.http.post<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/transfer`,
      transferData
    ).pipe(
      map(response => response.data),
      tap(() => {
        // Refresh wallets after transfer
        this.getWallets().subscribe();
      }),
      catchError(this.handleError)
    );
  }

  // Get wallet balance
  getWalletBalance(walletId: string): Observable<{ balance: number; currency: string }> {
    return this.http.get<ApiResponse<{ balance: number; currency: string }>>(
      `${this.apiUrl}/${walletId}/balance`
    ).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Get wallet types
  getWalletTypes(): Observable<string[]> {
    return this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/types`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Get supported currencies
  getSupportedCurrencies(): Observable<string[]> {
    return this.http.get<ApiResponse<string[]>>(`${this.apiUrl}/currencies`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Refresh wallets data
  refreshWallets(): void {
    this.getWallets().subscribe();
  }

  // Get current wallets value
  getCurrentWallets(): UserWallet[] {
    return this.walletsSubject.value;
  }

  // Clear wallets (for logout)
  clearWallets(): void {
    this.walletsSubject.next([]);
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = error.error?.message || `Server Error: ${error.status} ${error.statusText}`;
    }
    
    console.error('WalletService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}