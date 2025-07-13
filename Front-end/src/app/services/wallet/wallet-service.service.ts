import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap, map, distinctUntilChanged } from 'rxjs/operators';
import { UserWallet, CreateWalletRequest, UpdateWalletRequest, TransferRequest, User, UnifiedResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';
import { ResponseExtractor } from '../../utilities/responseExtractor';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  private readonly apiUrl = `${environment.apiUrl}/wallet`;
  
  // State management
  private walletsSubject = new BehaviorSubject<UserWallet[]>([]);
  private primaryWalletSubject = new BehaviorSubject<UserWallet | null>(null);
  private totalBalanceSubject = new BehaviorSubject<number>(0);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  
  // Public observables
  public wallets$ = this.walletsSubject.asObservable();
  public primaryWallet$ = this.primaryWalletSubject.asObservable();
  public totalBalance$ = this.totalBalanceSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();
  public walletsCount$ = this.wallets$.pipe(map(wallets => wallets.length));
  public activeWallets$ = this.wallets$.pipe(
    map(wallets => wallets.filter(wallet => wallet.status === 'active'))
  );

  constructor(private http: HttpClient) {
    this.initializeCalculations();
  }

  private initializeCalculations(): void {
    
    this.wallets$.pipe(
      map(wallets => wallets.reduce((total, wallet) => total + (wallet.balance || 0), 0)),
      distinctUntilChanged()
    ).subscribe(total => this.totalBalanceSubject.next(total));

    
    this.wallets$.pipe(
      map(wallets => wallets.find(wallet => wallet.isPrimary) || null),
      distinctUntilChanged((prev, curr) => prev?.id === curr?.id)
    ).subscribe(primaryWallet => this.primaryWalletSubject.next(primaryWallet));
  }

  initializeWithUser(user: User): void {
    if (user.wallets?.length) {
      this.walletsSubject.next(user.wallets);
    }
    this.totalBalanceSubject.next(user.totalBalance || 0);
    
    const primaryWallet = user.wallets?.find(w => w.id === user.primaryWalletId);
    if (primaryWallet) {
      this.primaryWalletSubject.next(primaryWallet);
    }
  }

  getWallets(forceRefresh: boolean = false): Observable<UserWallet[]> {
    if (!forceRefresh && this.walletsSubject.value.length > 0) {
      return this.wallets$;
    }

    this.setLoading(true);
    return this.http.get<UnifiedResponse>(`${this.apiUrl}`)
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'wallets') || []),
        tap(wallets => {
          this.walletsSubject.next(wallets);
          this.setLoading(false);
        }),
        catchError(error => {
          this.setLoading(false);
          return this.handleError(error);
        })
      );
  }

  getWalletById(walletId: string): Observable<UserWallet> {
    const cached = this.walletsSubject.value.find(w => w.id === walletId);
    if (cached) {
      return new Observable(observer => {
        observer.next(cached);
        observer.complete();
      });
    }

    return this.http.get<UnifiedResponse>(`${this.apiUrl}/${walletId}`)
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'wallet')),
        tap(wallet => {
          if (wallet) {
            const current = this.walletsSubject.value;
            if (!current.find(w => w.id === walletId)) {
              this.walletsSubject.next([...current, wallet]);
            }
          }
        }),
        catchError(this.handleError)
      );
  }

  // TODO adding the primary wallet funcationallity
  createWallet(walletData: CreateWalletRequest): Observable<UserWallet> {
    this.setLoading(true);
    return this.http.post<any>(`${this.apiUrl}/create`, walletData)
      .pipe(
       map(response => {

  return ResponseExtractor.extractData(response, 'DATA', 'wallet');
}),
        tap(newWallet => {
          if (newWallet) {
            const updated = [...this.walletsSubject.value, newWallet];
            this.walletsSubject.next(updated);
          }
          this.setLoading(false);
        }),
        catchError(error => {
          console.log("xasxa   "+ error);
          this.setLoading(false);
          return this.handleError(error);
        })
      );
  }

  updateWallet(walletId: string, walletData: UpdateWalletRequest): Observable<UserWallet> {
    this.setLoading(true);
    return this.http.put<UnifiedResponse>(`${this.apiUrl}/${walletId}`, walletData)
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'wallet')),
        tap(updatedWallet => {
          if (updatedWallet) {
            const updated = this.walletsSubject.value.map(wallet => 
              wallet.id === walletId ? updatedWallet : wallet
            );
            this.walletsSubject.next(updated);
          }
          this.setLoading(false);
        }),
        catchError(error => {
          this.setLoading(false);
          return this.handleError(error);
        })
      );
  }

  updatePrimaryWallet(walletId: string): Observable<{ success: boolean; message: string }> {
    this.setLoading(true);
    return this.http.patch<UnifiedResponse>(`${this.apiUrl}/${walletId}/set-primary`, {})
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'result') || 
          { success: false, message: 'Unknown error' }),
        tap(result => {
          if (result.success) {
            const updated = this.walletsSubject.value.map(wallet => ({
              ...wallet,
              isPrimary: wallet.id === walletId
            }));
            this.walletsSubject.next(updated);
          }
          this.setLoading(false);
        }),
        catchError(error => {
          this.setLoading(false);
          return this.handleError(error);
        })
      );
  }

  toggleWalletStatus(walletId: string, status: 'active' | 'inactive'): Observable<UserWallet> {
    this.setLoading(true);
    return this.http.patch<UnifiedResponse>(`${this.apiUrl}/${walletId}/status`, { status })
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'wallet')),
        tap(updatedWallet => {
          if (updatedWallet) {
            const updated = this.walletsSubject.value.map(wallet => 
              wallet.id === walletId ? updatedWallet : wallet
            );
            this.walletsSubject.next(updated);
          }
          this.setLoading(false);
        }),
        catchError(error => {
          this.setLoading(false);
          return this.handleError(error);
        })
      );
  }

  deleteWallet(walletId: string): Observable<{ success: boolean; message: string }> {
    this.setLoading(true);
    return this.http.delete<UnifiedResponse>(`${this.apiUrl}/${walletId}`)
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'result') || 
          { success: false, message: 'Unknown error' }),
        tap(result => {
          if (result.success) {
            const updated = this.walletsSubject.value.filter(w => w.id !== walletId);
            this.walletsSubject.next(updated);
          }
          this.setLoading(false);
        }),
        catchError(error => {
          this.setLoading(false);
          return this.handleError(error);
        })
      );
  }

  transferBetweenWallets(transferData: TransferRequest): Observable<{ success: boolean; message: string }> {
    this.setLoading(true);
    return this.http.post<UnifiedResponse>(`${this.apiUrl}/transfer`, transferData)
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'result') || 
          { success: false, message: 'Unknown error' }),
        tap(result => {
          if (result.success) {
            this.getWallets(true).subscribe();
          }
          this.setLoading(false);
        }),
        catchError(error => {
          this.setLoading(false);
          return this.handleError(error);
        })
      );
  }

  getWalletBalance(walletId: string): Observable<{ balance: number; currency: string }> {
    const cached = this.walletsSubject.value.find(w => w.id === walletId);
    if (cached?.balance !== undefined) {
      return new Observable(observer => {
        observer.next({ balance: cached.balance, currency: cached.currency });
        observer.complete();
      });
    }

    return this.http.get<UnifiedResponse>(`${this.apiUrl}/${walletId}/balance`)
      .pipe(
        map(response => ResponseExtractor.extractData(response, 'data', 'balance') || 
          { balance: 0, currency: 'USD' }),
        tap(balanceData => {
          const updated = this.walletsSubject.value.map(wallet => 
            wallet.id === walletId 
              ? { ...wallet, balance: balanceData.balance, currency: balanceData.currency }
              : wallet
          );
          this.walletsSubject.next(updated);
        }),
        catchError(this.handleError)
      );
  }

  // Utility methods
  refreshWallets(): Observable<UserWallet[]> {
    return this.getWallets(true);
  }

  getCurrentWallets(): UserWallet[] {
    return this.walletsSubject.value;
  }

  getCurrentPrimaryWallet(): UserWallet | null {
    return this.primaryWalletSubject.value;
  }

  getCurrentTotalBalance(): number {
    return this.totalBalanceSubject.value;
  }

  clearWallets(): void {
    this.walletsSubject.next([]);
    this.primaryWalletSubject.next(null);
    this.totalBalanceSubject.next(0);
    this.setLoading(false);
  }

  private setLoading(loading: boolean): void {
    this.isLoadingSubject.next(loading);
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    console.log("fasaaa  "+ error.error );
    
    return throwError(() => new Error(errorMessage));
  }
}