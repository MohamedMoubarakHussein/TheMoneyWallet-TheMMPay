// recent-transactions.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

export interface Transaction {
  id: string;
  type: string;
  amount: number;
  description: string;
  date: Date;
  category: string;
  status: string;
}

@Component({
  selector: 'app-recent-transactions',
  templateUrl: './recentTransactions.component.html',
  imports : [CommonModule]
  ,
  styleUrls: ['./recentTransactions.component.scss']
})
export class RecentTransactionsComponent implements OnInit {
  recentTransactions: Transaction[] = [];
  
  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadRecentTransactions();
  }

  loadRecentTransactions(): void {
    // Simulate API call - replace with your actual service
    this.recentTransactions = [
      {
        id: '1',
        type: 'income',
        amount: 2500.00,
        description: 'Salary Payment',
        date: new Date('2024-01-15'),
        category: 'Salary',
        status: 'completed'
      },
      {
        id: '2',
        type: 'expense',
        amount: -150.75,
        description: 'Grocery Shopping',
        date: new Date('2024-01-14'),
        category: 'Food',
        status: 'completed'
      },
      {
        id: '3',
        type: 'transfer',
        amount: -500.00,
        description: 'Transfer to Savings',
        date: new Date('2024-01-13'),
        category: 'Transfer',
        status: 'completed'
      },
      {
        id: '4',
        type: 'expense',
        amount: -89.99,
        description: 'Utility Bill',
        date: new Date('2024-01-12'),
        category: 'Bills',
        status: 'pending'
      },
      {
        id: '5',
        type: 'income',
        amount: 325.50,
        description: 'Freelance Project',
        date: new Date('2024-01-11'),
        category: 'Freelance',
        status: 'completed'
      }
      // Add 5 more transactions to make it 10
    ].slice(0, 10); // Ensure only 10 transactions
  }

  viewAllTransactions(): void {
    this.router.navigate(['/transactions']);
  }

  getTransactionIcon(type: string): string {
    switch (type) {
      case 'income': return 'arrow-down-circle';
      case 'expense': return 'arrow-up-circle';
      case 'transfer': return 'arrow-left-right';
      default: return 'circle';
    }
  }

  getTransactionColor(type: string): string {
    switch (type) {
      case 'income': return 'success';
      case 'expense': return 'danger';
      case 'transfer': return 'info';
      default: return 'secondary';
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'completed': return 'success';
      case 'pending': return 'warning';
      case 'failed': return 'danger';
      default: return 'secondary';
    }
  }
}