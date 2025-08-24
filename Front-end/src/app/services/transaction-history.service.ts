import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { Transaction } from '../entity/UnifiedResponse'; // Assuming you have this from the previous refactoring

@Injectable({
  providedIn: 'root'
})
export class TransactionHistoryService {
  private allTransactions = new BehaviorSubject<Transaction[]>([]);
  private displayedTransactions = new BehaviorSubject<Transaction[]>([]);
  private currentPage = new BehaviorSubject<number>(1);
  private itemsPerPage = new BehaviorSubject<number>(10);
  private totalPages = new BehaviorSubject<number>(1);
  private startDate = new BehaviorSubject<string>('');
  private endDate = new BehaviorSubject<string>('');
  private searchQuery = new BehaviorSubject<string>('');
  private sortField = new BehaviorSubject<keyof Transaction>('createdAt');
  private sortDirection = new BehaviorSubject<'asc' | 'desc'>('desc');

  constructor() {
    this.generateMockData();
    this.subscribeToChanges();
  }

  private generateMockData() {
    const types: ('sent' | 'received' | 'deposit' | 'withdrawal' | 'transfer' | 'payment' | 'refund')[] = ['transfer', 'payment', 'deposit', 'withdrawal'];
    const statuses = ['completed', 'pending', 'failed'];
    const mockTransactions: Transaction[] = [];
    for (let i = 0; i < 50; i++) {
      mockTransactions.push({
        id: `TX${1000 + i}`,
        createdAt: new Date(2024, 0, Math.floor(Math.random() * 30 + 1)),
        updatedAt: new Date(2024, 0, Math.floor(Math.random() * 30 + 1)),
        description: `Transaction ${1000 + i}`,
        amount: Math.random() * 1000 * (Math.random() > 0.5 ? 1 : -1),
        currency: 'USD',
        status: statuses[Math.floor(Math.random() * statuses.length)] as ('completed' | 'pending' | 'failed'),
        type: types[Math.floor(Math.random() * types.length)],
        walletId: ''
      });
    }
    this.allTransactions.next(mockTransactions);
  }

  private subscribeToChanges() {
    combineLatest([
      this.allTransactions,
      this.currentPage,
      this.itemsPerPage,
      this.startDate,
      this.endDate,
      this.searchQuery,
      this.sortField,
      this.sortDirection
    ]).pipe(
      map(([transactions, currentPage, itemsPerPage, startDate, endDate, searchQuery, sortField, sortDirection]) => {
        let filtered = transactions.filter(t => {
          const tDate = new Date(t.createdAt);
          const start = startDate ? new Date(startDate) : null;
          const end = endDate ? new Date(endDate) : null;
          const matchesDate = (!start || tDate >= start) && (!end || tDate <= end);
          const matchesSearch = t.description?.toLowerCase().includes(searchQuery.toLowerCase()) ?? false;
          return matchesDate && matchesSearch;
        });

        filtered = filtered.sort((a, b) => {
          const valueA = a[sortField];
          const valueB = b[sortField];
          if (typeof valueA === 'number' && typeof valueB === 'number') {
            return sortDirection === 'asc' ? valueA - valueB : valueB - valueA;
          }
          return String(valueA).localeCompare(String(valueB)) * (sortDirection === 'asc' ? 1 : -1);
        });

        const totalPages = Math.ceil(filtered.length / itemsPerPage);
        const startIndex = (currentPage - 1) * itemsPerPage;
        const displayed = filtered.slice(startIndex, startIndex + itemsPerPage);

        return { displayed, totalPages };
      })
    ).subscribe(({ displayed, totalPages }) => {
      this.displayedTransactions.next(displayed);
      this.totalPages.next(totalPages);
    });
  }

  // Getters for observables
  getDisplayedTransactions(): Observable<Transaction[]> { return this.displayedTransactions.asObservable(); }
  getCurrentPage(): Observable<number> { return this.currentPage.asObservable(); }
  getTotalPages(): Observable<number> { return this.totalPages.asObservable(); }

  // Setters for state
  setStartDate(date: string) { this.startDate.next(date); }
  setEndDate(date: string) { this.endDate.next(date); }
  setSearchQuery(query: string) { this.searchQuery.next(query); }
  
  setSort(field: keyof Transaction) {
    if (this.sortField.value === field) {
      this.sortDirection.next(this.sortDirection.value === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.next(field);
      this.sortDirection.next('asc');
    }
  }

  previousPage() {
    if (this.currentPage.value > 1) {
      this.currentPage.next(this.currentPage.value - 1);
    }
  }

  nextPage() {
    if (this.currentPage.value < this.totalPages.value) {
      this.currentPage.next(this.currentPage.value + 1);
    }
  }
}