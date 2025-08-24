import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map, tap } from 'rxjs/operators';
import { RecurringPayment } from '../entity/UnifiedResponse';
import { CreateRecurringPaymentRequest } from './recurring-payment/recurring-payment.service';

@Injectable({
  providedIn: 'root'
})
export class RecurringPaymentsManagementService {
  private payments = new BehaviorSubject<RecurringPayment[]>([]);
  private isLoading = new BehaviorSubject<boolean>(false);
  private searchQuery = new BehaviorSubject<string>('');
  private statusFilter = new BehaviorSubject<string>('all');
  private currentPage = new BehaviorSubject<number>(1);
  itemsPerPage = 10;

  constructor() {
    this.loadPayments();
  }

  getPayments(): Observable<RecurringPayment[]> { return this.payments.asObservable(); }
  isLoading$(): Observable<boolean> { return this.isLoading.asObservable(); }

  getFilteredPayments(): Observable<RecurringPayment[]> {
    return this.payments.pipe(
      map(payments => 
        payments.filter(p => 
          (this.statusFilter.value === 'all' || (this.statusFilter.value === 'active' ? p.isActive : !p.isActive)) &&
          (p.name.toLowerCase().includes(this.searchQuery.value.toLowerCase()))
        )
      )
    );
  }

  getPaginatedPayments(): Observable<RecurringPayment[]> {
    return this.getFilteredPayments().pipe(
      map(payments => {
        const startIndex = (this.currentPage.value - 1) * this.itemsPerPage;
        return payments.slice(startIndex, startIndex + this.itemsPerPage);
      })
    );
  }

  getTotalPages(): Observable<number> {
    return this.getFilteredPayments().pipe(
      map(payments => Math.ceil(payments.length / this.itemsPerPage))
    );
  }

  loadPayments() {
    this.isLoading.next(true);
    of([
      // Mock data
    ]).pipe(delay(1000)).subscribe(payments => {
      this.payments.next(payments);
      this.isLoading.next(false);
    });
  }

  createPayment(paymentData: CreateRecurringPaymentRequest): Observable<RecurringPayment> {
    this.isLoading.next(true);
    const newPayment: RecurringPayment = {
      id: Date.now().toString(),
      userId: 'user1',
      ...paymentData,
      isActive: true,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    return of(newPayment).pipe(
      delay(1000),
      tap(p => {
        this.payments.next([p, ...this.payments.value]);
        this.isLoading.next(false);
      })
    );
  }

  deletePayment(paymentId: string): Observable<{ success: boolean }> {
    this.payments.next(this.payments.value.filter(p => p.id !== paymentId));
    return of({ success: true });
  }
  
  setSearchQuery(query: string) { this.searchQuery.next(query); }
  setStatusFilter(status: string) { this.statusFilter.next(status); }
  nextPage() { this.currentPage.next(this.currentPage.value + 1); }
  previousPage() { this.currentPage.next(this.currentPage.value - 1); }
}