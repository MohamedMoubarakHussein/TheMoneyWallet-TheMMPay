import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError, timer } from 'rxjs';
import { catchError, tap, map, switchMap, retry } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { CurrencyConversion, ApiResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';

export interface Currency {
  code: string;
  name: string;
  symbol: string;
  flag?: string;
  isActive: boolean;
  exchangeRate?: number;
  lastUpdated?: Date;
}

export interface ExchangeRate {
  fromCurrency: string;
  toCurrency: string;
  rate: number;
  inverseRate: number;
  lastUpdated: Date;
  source: string;
}

export interface CurrencyConversionRequest {
  fromCurrency: string;
  toCurrency: string;
  amount: number;
}

export interface CurrencyConversionResponse {
  fromCurrency: string;
  toCurrency: string;
  fromAmount: number;
  toAmount: number;
  rate: number;
  fee: number;
  totalAmount: number;
  lastUpdated: Date;
}

export interface CurrencyStats {
  totalConversions: number;
  totalAmountConverted: number;
  mostPopularCurrencies: string[];
  conversionFees: number;
  lastUpdated: Date;
}

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  private readonly apiUrl = `${environment.apiUrl}/currency`;
  private readonly EXCHANGE_RATE_CACHE_KEY = 'exchange_rates_cache';
  private readonly CACHE_DURATION = 5 * 60 * 1000; // 5 minutes
  
  private currenciesSubject = new BehaviorSubject<Currency[]>([]);
  private exchangeRatesSubject = new BehaviorSubject<ExchangeRate[]>([]);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  
  public currencies$ = this.currenciesSubject.asObservable();
  public exchangeRates$ = this.exchangeRatesSubject.asObservable();
  public isLoading$ = this.isLoadingSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: object
  ) {
    this.initializeService();
  }

  private initializeService(): void {
    this.loadCurrencies();
    this.loadExchangeRates();
    this.setupAutoRefresh();
  }

  private setupAutoRefresh(): void {
    // Refresh exchange rates every 5 minutes
    timer(0, this.CACHE_DURATION).pipe(
      switchMap(() => this.refreshExchangeRates())
    ).subscribe();
  }

  // Get all available currencies
  getCurrencies(): Observable<Currency[]> {
    return this.http.get<ApiResponse<Currency[]>>(`${this.apiUrl}/currencies`)
      .pipe(
        map(response => response.data || []),
        tap(currencies => this.currenciesSubject.next(currencies)),
        catchError(this.handleError)
      );
  }

  // Get active currencies only
  getActiveCurrencies(): Observable<Currency[]> {
    return this.currencies$.pipe(
      map(currencies => currencies.filter(c => c.isActive))
    );
  }

  // Get currency by code
  getCurrencyByCode(code: string): Observable<Currency | null> {
    return this.http.get<ApiResponse<Currency>>(`${this.apiUrl}/currencies/${code}`)
      .pipe(
        map(response => response.data || null),
        catchError(this.handleError)
      );
  }

  // Get exchange rates
  getExchangeRates(baseCurrency: string = 'USD'): Observable<ExchangeRate[]> {
    return this.http.get<ApiResponse<ExchangeRate[]>>(`${this.apiUrl}/rates?base=${baseCurrency}`)
      .pipe(
        map(response => response.data || []),
        tap(rates => this.exchangeRatesSubject.next(rates)),
        catchError(this.handleError)
      );
  }

  // Get specific exchange rate
  getExchangeRate(fromCurrency: string, toCurrency: string): Observable<ExchangeRate | null> {
    if (fromCurrency === toCurrency) {
      return new Observable(observer => {
        observer.next({
          fromCurrency,
          toCurrency,
          rate: 1,
          inverseRate: 1,
          lastUpdated: new Date(),
          source: 'internal'
        });
        observer.complete();
      });
    }

    return this.http.get<ApiResponse<ExchangeRate>>(
      `${this.apiUrl}/rates/${fromCurrency}/${toCurrency}`
    ).pipe(
      map(response => response.data || null),
      catchError(this.handleError)
    );
  }

  // Convert currency
  convertCurrency(request: CurrencyConversionRequest): Observable<CurrencyConversionResponse> {
    return this.http.post<ApiResponse<CurrencyConversionResponse>>(`${this.apiUrl}/convert`, request)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get conversion fee
  getConversionFee(fromCurrency: string, toCurrency: string, amount: number): Observable<{
    fee: number;
    feePercentage: number;
    totalAmount: number;
  }> {
    return this.http.get<ApiResponse<ConversionFeeResponse>>(
      `${this.apiUrl}/fees?from=${fromCurrency}&to=${toCurrency}&amount=${amount}`
    ).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Get currency conversion history
  getConversionHistory(
    walletId?: string,
    limit: number = 20,
    offset: number = 0
  ): Observable<CurrencyConversion[]> {
    const params = new URLSearchParams();
    if (walletId) params.set('walletId', walletId);
    params.set('limit', limit.toString());
    params.set('offset', offset.toString());

    return this.http.get<ApiResponse<CurrencyConversion[]>>(`${this.apiUrl}/history?${params.toString()}`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Get currency statistics
  getCurrencyStats(): Observable<CurrencyStats> {
    return this.http.get<ApiResponse<CurrencyStats>>(`${this.apiUrl}/stats`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get popular currency pairs
  getPopularCurrencyPairs(): Observable<{
    fromCurrency: string;
    toCurrency: string;
    conversionCount: number;
    averageAmount: number;
  }[]> {
    return this.http.get<ApiResponse<PopularCurrencyPair[]>>(`${this.apiUrl}/popular-pairs`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Refresh exchange rates
  refreshExchangeRates(): Observable<ExchangeRate[]> {
    this.setLoading(true);
    return this.getExchangeRates().pipe(
      tap(() => this.setLoading(false)),
      retry(3)
    );
  }

  // Get cached exchange rate
  getCachedExchangeRate(fromCurrency: string, toCurrency: string): ExchangeRate | null {
    const cached = this.exchangeRatesSubject.value.find(
      rate => rate.fromCurrency === fromCurrency && rate.toCurrency === toCurrency
    );
    
    if (cached && this.isCacheValid(cached.lastUpdated)) {
      return cached;
    }
    
    return null;
  }

  // Check if cache is valid
  private isCacheValid(lastUpdated: Date): boolean {
    const now = new Date();
    const diff = now.getTime() - lastUpdated.getTime();
    return diff < this.CACHE_DURATION;
  }

  // Load currencies from cache or API
  private loadCurrencies(): void {
    const cached = this.getCachedCurrencies();
    if (cached) {
      this.currenciesSubject.next(cached);
    } else {
      this.getCurrencies().subscribe();
    }
  }

  // Load exchange rates from cache or API
  private loadExchangeRates(): void {
    const cached = this.getCachedExchangeRates();
    if (cached) {
      this.exchangeRatesSubject.next(cached);
    } else {
      this.getExchangeRates().subscribe();
    }
  }

  // Get cached currencies
  private getCachedCurrencies(): Currency[] | null {
    // Only access localStorage in browser environment
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }
    
    try {
      const cached = localStorage.getItem('currencies_cache');
      if (cached) {
        const data = JSON.parse(cached);
        if (this.isCacheValid(new Date(data.timestamp))) {
          return data.currencies;
        }
      }
    } catch (error) {
      console.error('Error reading cached currencies:', error);
    }
    return null;
  }

  // Get cached exchange rates
  private getCachedExchangeRates(): ExchangeRate[] | null {
    try {
      // Only access localStorage in browser environment
      if (!isPlatformBrowser(this.platformId)) {
        return null;
      }
      // Only access localStorage in browser environment
      if (!isPlatformBrowser(this.platformId)) {
        return null;
      }
      const cached = localStorage.getItem(this.EXCHANGE_RATE_CACHE_KEY);
      if (cached) {
        const data = JSON.parse(cached);
        if (this.isCacheValid(new Date(data.timestamp))) {
          return data.rates;
        }
      }
    } catch (error) {
      console.error('Error reading cached exchange rates:', error);
    }
    return null;
  }

  // Cache currencies
  private cacheCurrencies(currencies: Currency[]): void {
    try {
      localStorage.setItem('currencies_cache', JSON.stringify({
        currencies,
        timestamp: new Date().toISOString()
      }));
    } catch (error) {
      console.error('Error caching currencies:', error);
    }
  }

  // Cache exchange rates
  private cacheExchangeRates(rates: ExchangeRate[]): void {
    try {
      localStorage.setItem(this.EXCHANGE_RATE_CACHE_KEY, JSON.stringify({
        rates,
        timestamp: new Date().toISOString()
      }));
    } catch (error) {
      console.error('Error caching exchange rates:', error);
    }
  }

  // Utility methods
  formatCurrency(amount: number, currency: string, locale: string = 'en-US'): string {
    try {
      return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currency
      }).format(amount);
    } catch (error) {
      return `${currency} ${amount.toFixed(2)}`;
    }
  }

  getCurrencySymbol(currencyCode: string): string {
    const currency = this.currenciesSubject.value.find(c => c.code === currencyCode);
    return currency?.symbol || currencyCode;
  }

  getCurrencyName(currencyCode: string): string {
    const currency = this.currenciesSubject.value.find(c => c.code === currencyCode);
    return currency?.name || currencyCode;
  }

  isSupportedCurrency(currencyCode: string): boolean {
    return this.currenciesSubject.value.some(c => c.code === currencyCode && c.isActive);
  }

  getCurrentCurrencies(): Currency[] {
    return this.currenciesSubject.value;
  }

  getCurrentExchangeRates(): ExchangeRate[] {
    return this.exchangeRatesSubject.value;
  }

  clearCache(): void {
    localStorage.removeItem('currencies_cache');
    localStorage.removeItem(this.EXCHANGE_RATE_CACHE_KEY);
  }

  private setLoading(loading: boolean): void {
    this.isLoadingSubject.next(loading);
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      errorMessage = error.error?.message || `Server Error: ${error.status} ${error.statusText}`;
    }
    
    console.error('CurrencyService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
} 