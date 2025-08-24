import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map, tap } from 'rxjs/operators';
import { Invoice } from '../entity/UnifiedResponse';
import { CreateInvoiceRequest } from './invoice/invoice.service';

export interface InvoiceSummary {
  totalInvoices: number;
  paidInvoices: number;
  pendingAmount: number;
  overdueInvoices: number;
}

@Injectable({
  providedIn: 'root'
})
export class InvoiceManagementService {
  private invoices = new BehaviorSubject<Invoice[]>([]);
  private summary = new BehaviorSubject<InvoiceSummary | null>(null);
  public isLoading = new BehaviorSubject<boolean>(false);
  private searchQuery = new BehaviorSubject<string>('');
  private statusFilter = new BehaviorSubject<string>('all');
  private currentPage = new BehaviorSubject<number>(1);
  itemsPerPage = 10;

  constructor() {
    this.loadInvoices();
  }

  getInvoices(): Observable<Invoice[]> { return this.invoices.asObservable(); }
  getSummary(): Observable<InvoiceSummary | null> { return this.summary.asObservable(); }

  getFilteredInvoices(): Observable<Invoice[]> {
    return this.invoices.pipe(
      map(invoices => 
        invoices.filter(invoice => 
          (this.statusFilter.value === 'all' || invoice.status === this.statusFilter.value) &&
          (invoice.recipient.name.toLowerCase().includes(this.searchQuery.value.toLowerCase()))
        )
      )
    );
  }

  getPaginatedInvoices(): Observable<Invoice[]> {
    return this.getFilteredInvoices().pipe(
      map(invoices => {
        const startIndex = (this.currentPage.value - 1) * this.itemsPerPage;
        return invoices.slice(startIndex, startIndex + this.itemsPerPage);
      })
    );
  }

  getTotalPages(): Observable<number> {
    return this.getFilteredInvoices().pipe(
      map(invoices => Math.ceil(invoices.length / this.itemsPerPage))
    );
  }

  loadInvoices() {
    this.isLoading.next(true);
    of([
      // Mock data
    ]).pipe(delay(1000)).subscribe(invoices => {
      this.invoices.next(invoices);
      this.calculateSummary();
      this.isLoading.next(false);
    });
  }

  createInvoice(invoiceData: CreateInvoiceRequest): Observable<Invoice> {
    this.isLoading.next(true);
    const newInvoice: Invoice = {
      id: Date.now().toString(),
      userId: 'user1',
      ...invoiceData,
      items: invoiceData.items.map((item, index) => ({
        ...item,
        id: `${Date.now()}-${index}`,
        total: item.quantity * item.unitPrice
      })),
      invoiceNumber: `INV-${Date.now()}`,
      status: 'draft',
      createdAt: new Date(),
      updatedAt: new Date(),
      issueDate: new Date()
    };
    return of(newInvoice).pipe(
      delay(1000),
      tap(invoice => {
        this.invoices.next([invoice, ...this.invoices.value]);
        this.calculateSummary();
        this.isLoading.next(false);
      })
    );
  }

  deleteInvoice(invoiceId: string): Observable<{ success: boolean }> {
    this.invoices.next(this.invoices.value.filter(i => i.id !== invoiceId));
    this.calculateSummary();
    return of({ success: true });
  }

  private calculateSummary() {
    const invoices = this.invoices.value;
    const summary: InvoiceSummary = {
      totalInvoices: invoices.length,
      paidInvoices: invoices.filter(i => i.status === 'paid').length,
      pendingAmount: invoices.filter(i => i.status !== 'paid').reduce((sum, i) => sum + i.amount, 0),
      overdueInvoices: invoices.filter(i => i.status === 'overdue').length
    };
    this.summary.next(summary);
  }
  
  setSearchQuery(query: string) { this.searchQuery.next(query); }
  setStatusFilter(status: string) { this.statusFilter.next(status); }
  nextPage() { this.currentPage.next(this.currentPage.value + 1); }
  previousPage() { this.currentPage.next(this.currentPage.value - 1); }
}