import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { Transaction } from '../../entity/UnifiedResponse';
import { TransactionHistoryService } from '../../services/transaction-history.service';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './transaction-history.component.html',
  animations: [
    trigger('listAnimation', [
      transition('* <=> *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(-10px)' }),
          stagger('50ms', animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })))
        ], { optional: true })
      ])
    ])
  ]
})
export class TransactionHistoryComponent {
  displayedTransactions$: Observable<Transaction[]>;
  currentPage$: Observable<number>;
  totalPages$: Observable<number>;

  startDate: string = '';
  endDate: string = '';
  searchQuery: string = '';

  constructor(private transactionHistoryService: TransactionHistoryService) {
    this.displayedTransactions$ = this.transactionHistoryService.getDisplayedTransactions();
    this.currentPage$ = this.transactionHistoryService.getCurrentPage();
    this.totalPages$ = this.transactionHistoryService.getTotalPages();
  }

  applyDateFilter() {
    this.transactionHistoryService.setStartDate(this.startDate);
    this.transactionHistoryService.setEndDate(this.endDate);
  }

  onSearchQueryChanged() {
    this.transactionHistoryService.setSearchQuery(this.searchQuery);
  }

  sortTransactions(field: keyof Transaction) {
    this.transactionHistoryService.setSort(field);
  }

  previousPage() {
    this.transactionHistoryService.previousPage();
  }

  nextPage() {
    this.transactionHistoryService.nextPage();
  }
}
