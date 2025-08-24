import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { trigger, style, transition, animate } from '@angular/animations';

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
  standalone: true,
  imports : [CommonModule, RouterModule],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class RecentTransactionsComponent implements OnInit {
  recentTransactions: Transaction[] = [];
  
  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadRecentTransactions();
  }

  loadRecentTransactions(): void {
    // Mock data
    this.recentTransactions = [
      // ...
    ];
  }

  viewAllTransactions(): void {
    this.router.navigate(['/transactions']);
  }

  getTransactionIcon(type: string): string {
    if (type === 'sent' || type === 'withdrawal') return 'fa-arrow-up';
    if (type === 'received' || type === 'deposit') return 'fa-arrow-down';
    return 'fa-exchange-alt';
  }

  getTransactionColor(type: string): string {
    if (type === 'sent' || type === 'withdrawal') return 'text-red-500';
    if (type === 'received' || type === 'deposit') return 'text-green-500';
    return 'text-gray-500';
  }
}
