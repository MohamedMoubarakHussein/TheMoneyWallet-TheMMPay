import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';
import { Invoice, ApiResponse } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';
import { isClientError, getSafeErrorMessage } from '../../utilities/error.utils';

export interface CreateInvoiceRequest {
  walletId: string;
  amount: number;
  currency: string;
  dueDate: Date;
  recipient: {
    name: string;
    email: string;
    phone?: string;
    address?: string;
  };
  items: {
    description: string;
    quantity: number;
    unitPrice: number;
  }[];
  notes?: string;
  terms?: string;
}

export interface UpdateInvoiceRequest {
  amount?: number;
  dueDate?: Date;
  recipient?: {
    name?: string;
    email?: string;
    phone?: string;
    address?: string;
  };
  items?: {
    description: string;
    quantity: number;
    unitPrice: number;
  }[];
  notes?: string;
  terms?: string;
}

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private readonly apiUrl = `${environment.apiUrl}/invoices`;
  private invoicesSubject = new BehaviorSubject<Invoice[]>([]);
  
  public invoices$ = this.invoicesSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  // Get all invoices for a user
  getInvoices(walletId?: string, status?: string): Observable<Invoice[]> {
    let params = new URLSearchParams();
    if (walletId) params.set('walletId', walletId);
    if (status) params.set('status', status);

    return this.http.get<ApiResponse<Invoice[]>>(`${this.apiUrl}?${params.toString()}`)
      .pipe(
        map(response => response.data || []),
        tap(invoices => this.invoicesSubject.next(invoices)),
        catchError(this.handleError)
      );
  }

  // Get invoice by ID
  getInvoiceById(invoiceId: string): Observable<Invoice> {
    return this.http.get<ApiResponse<Invoice>>(`${this.apiUrl}/${invoiceId}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Create new invoice
  createInvoice(invoiceData: CreateInvoiceRequest): Observable<Invoice> {
    return this.http.post<ApiResponse<Invoice>>(`${this.apiUrl}`, invoiceData)
      .pipe(
        map(response => response.data),
        tap(newInvoice => {
          const currentInvoices = this.invoicesSubject.value;
          this.invoicesSubject.next([newInvoice, ...currentInvoices]);
        }),
        catchError(this.handleError)
      );
  }

  // Update invoice
  updateInvoice(invoiceId: string, updateData: UpdateInvoiceRequest): Observable<Invoice> {
    return this.http.put<ApiResponse<Invoice>>(`${this.apiUrl}/${invoiceId}`, updateData)
      .pipe(
        map(response => response.data),
        tap(updatedInvoice => {
          const currentInvoices = this.invoicesSubject.value;
          const index = currentInvoices.findIndex(i => i.id === invoiceId);
          if (index !== -1) {
            currentInvoices[index] = updatedInvoice;
            this.invoicesSubject.next([...currentInvoices]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Send invoice
  sendInvoice(invoiceId: string): Observable<{ success: boolean; message: string }> {
    return this.http.post<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${invoiceId}/send`,
      {}
    ).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Mark invoice as paid
  markAsPaid(invoiceId: string): Observable<Invoice> {
    return this.http.patch<ApiResponse<Invoice>>(`${this.apiUrl}/${invoiceId}/paid`, {})
      .pipe(
        map(response => response.data),
        tap(updatedInvoice => {
          const currentInvoices = this.invoicesSubject.value;
          const index = currentInvoices.findIndex(i => i.id === invoiceId);
          if (index !== -1) {
            currentInvoices[index] = updatedInvoice;
            this.invoicesSubject.next([...currentInvoices]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Cancel invoice
  cancelInvoice(invoiceId: string): Observable<{ success: boolean; message: string }> {
    return this.http.patch<ApiResponse<{ success: boolean; message: string }>>(
      `${this.apiUrl}/${invoiceId}/cancel`,
      {}
    ).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Delete invoice
  deleteInvoice(invoiceId: string): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/${invoiceId}`)
      .pipe(
        map(response => response.data),
        tap(() => {
          const currentInvoices = this.invoicesSubject.value;
          this.invoicesSubject.next(currentInvoices.filter(i => i.id !== invoiceId));
        }),
        catchError(this.handleError)
      );
  }

  // Get invoice statistics
  getInvoiceStats(walletId?: string): Observable<{
    totalInvoices: number;
    totalAmount: number;
    paidAmount: number;
    pendingAmount: number;
    overdueAmount: number;
    currency: string;
  }> {
    let params = '';
    if (walletId) params = `?walletId=${walletId}`;

    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/stats${params}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Export invoice as PDF
  exportInvoiceAsPdf(invoiceId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${invoiceId}/export/pdf`, {
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  // Get current invoices value
  getCurrentInvoices(): Invoice[] {
    return this.invoicesSubject.value;
  }

  // Clear invoices (for logout)
  clearInvoices(): void {
    this.invoicesSubject.next([]);
  }

  // Refresh invoices
  refreshInvoices(walletId?: string, status?: string): void {
    this.getInvoices(walletId, status).subscribe();
  }

  private handleError = (error: HttpErrorResponse): Observable<never> => {
    const errorMessage = getSafeErrorMessage(error, this.platformId);
    console.error('InvoiceService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
} 