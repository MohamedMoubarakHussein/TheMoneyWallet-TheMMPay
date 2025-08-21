import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface Transaction {
  id: string;
  date: Date;
  description: string;
  amount: number;
  currency: string;
  status: 'completed' | 'pending' | 'failed';
  type: string;
}

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './transaction-history.component.html',
  styleUrls: ['./transaction-history.component.css']
})
export class TransactionHistoryComponent {
  allTransactions: Transaction[] = [];
  displayedTransactions: Transaction[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  startDate: string = '';
  endDate: string = '';
  searchQuery: string = '';
  sortField: keyof Transaction = 'date';
  sortDirection: 'asc' | 'desc' = 'desc';

  constructor() {
    // Generate mock data
    this.generateMockData();
    this.updateDisplay();
  }

  private generateMockData() {
    const types = ['Transfer', 'Payment', 'Deposit', 'Withdrawal'];
    const statuses = ['completed', 'pending', 'failed'];
    
    for (let i = 0; i < 50; i++) {
      this.allTransactions.push({
        id: `TX${1000 + i}`,
        date: new Date(2024, 0, Math.floor(Math.random() * 30 + 1)),
        description: `Transaction ${1000 + i}`,
        amount: Math.random() * 1000 * (Math.random() > 0.5 ? 1 : -1),
        currency: 'USD',
        status: statuses[Math.floor(Math.random() * statuses.length)] as any,
        type: types[Math.floor(Math.random() * types.length)]
      });
    }
  }

  applyDateFilter() {
    this.currentPage = 1;
    this.updateDisplay();
  }

  sortTransactions(field: keyof Transaction) {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.updateDisplay();
  }

  updateDisplay() {
    let filtered = this.allTransactions.filter(t => {
      const matchesDate = (!this.startDate || new Date(t.date) >= new Date(this.startDate)) &&
                         (!this.endDate || new Date(t.date) <= new Date(this.endDate));
      const matchesSearch = t.description.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchesDate && matchesSearch;
    });

    // Sorting
    filtered = filtered.sort((a, b) => {
      const valueA = a[this.sortField];
      const valueB = b[this.sortField];
      
      if (typeof valueA === 'number' && typeof valueB === 'number') {
        return this.sortDirection === 'asc' ? valueA - valueB : valueB - valueA;
      }
      return String(valueA).localeCompare(String(valueB)) * (this.sortDirection === 'asc' ? 1 : -1);
    });

    // Pagination
    this.totalPages = Math.ceil(filtered.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.displayedTransactions = filtered.slice(startIndex, startIndex + this.itemsPerPage);
  }

  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updateDisplay();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updateDisplay();
    }
  }
}