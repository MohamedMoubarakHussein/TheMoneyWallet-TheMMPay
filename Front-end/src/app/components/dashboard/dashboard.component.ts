import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  transactions = [
    { name: 'Amazon Purchase', date: '2024-02-15', amount: -149.99, type: 'shopping' },
    { name: 'Salary Deposit', date: '2024-02-01', amount: 4500.00, type: 'payment' },
    { name: 'Restaurant Payment', date: '2024-02-14', amount: -85.50, type: 'food' },
    { name: 'Electric Bill', date: '2024-02-10', amount: -120.00, type: 'payment' }
  ];
constructor(private router: Router) {}
  showNotification(){
    this.router.navigate(['/notifications']);
  }
  sendMoney(){
    this.router.navigate(['/send-money']);
  }
  addFunds(){
    this.router.navigate(['/add-funds']);
  }
  invoice(){
    this.router.navigate(['/pay-bill']);
  }
  requestMoney(){
    this.router.navigate(['/request-money']);
  }
  
}