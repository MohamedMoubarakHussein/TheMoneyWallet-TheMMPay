import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError, of } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { UserWallet, CreateWalletRequest, UnifiedResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';
import { ResponseExtractor } from '../../utilities/responseExtractor';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  private readonly apiUrl = `${environment.apiUrl}/wallet`;
  
  private walletsSubject = new BehaviorSubject<UserWallet[]>([]);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  
  public wallets$ = this.walletsSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Gets all wallets for the current user.
   * @param forceRefresh If true, fetches from the server even if cached data exists.
   * @returns An observable of the user's wallets.
   */
  getWallets(forceRefresh = false): Observable<UserWallet[]> {
    if (!forceRefresh && this.walletsSubject.value.length > 0) {
      return of(this.walletsSubject.value);
    }
    this.isLoadingSubject.next(true);
    return this.http.get<UnifiedResponse>(`${this.apiUrl}`).pipe(
      map(response => ResponseExtractor.extractData(response, 'data', 'wallets') || []),
      tap(wallets => {
        this.walletsSubject.next(wallets);
        this.isLoadingSubject.next(false);
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Creates a new wallet.
   * @param walletData The data for the new wallet.
   * @returns An observable of the new wallet.
   */
  createWallet(walletData: CreateWalletRequest): Observable<UserWallet> {
    this.isLoadingSubject.next(true);
    return this.http.post<UnifiedResponse>(`${this.apiUrl}/create`, walletData).pipe(
      map(response => ResponseExtractor.extractData(response, 'DATA', 'wallet')),
      tap(newWallet => {
        this.walletsSubject.next([...this.walletsSubject.value, newWallet]);
        this.isLoadingSubject.next(false);
      }),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      // Client-side errors
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side errors
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    this.isLoadingSubject.next(false);
    return throwError(() => new Error(errorMessage));
  }
}