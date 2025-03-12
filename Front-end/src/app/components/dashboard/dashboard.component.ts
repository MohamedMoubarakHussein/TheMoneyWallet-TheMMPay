import { Component , HostListener} from '@angular/core';
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
  userName = "Sarah"; // Dynamic user name
  showNotifications = false;
  showUserMenu = false;
  transactions = [
    { name: 'Amazon Purchase', date: '2024-02-15', amount: -149.99, type: 'shopping' },
    { name: 'Salary Deposit', date: '2024-02-01', amount: 4500.00, type: 'payment' },
    { name: 'Restaurant Payment', date: '2024-02-14', amount: -85.50, type: 'food' },
    { name: 'Electric Bill', date: '2024-02-10', amount: -120.00, type: 'payment' }
  ];

  // Sample notifications
// In your DashboardComponent class
notifications = [
  { id: 0 ,text: 'New message received', date: '10 min ago', read: false },
  { id: 1 ,text: 'Payment processed successfully', date: '2 hours ago', read: true },
  { id: 2 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 3 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 4 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 5 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 6 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 7 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 8 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 9 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 10 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 11 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 12 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 13 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 14 ,text: 'Account security update', date: '1 day ago', read: true },
  { id: 15 ,text: 'Account security update', date: '1 day ago', read: true }
];
// Add this to your DashboardComponent class
get unreadCount(): number {
  return this.notifications.filter(notif => !notif.read).length;
}
   // Close dropdowns when clicking outside
   @HostListener('document:click', ['$event'])
   onClick(event: MouseEvent) {
     if (!(event.target as Element).closest('.header-actions')) {
       this.showNotifications = false;
       this.showUserMenu = false;
     }
   }
 
   toggleNotifications() {
     this.showNotifications = !this.showNotifications;
     this.showUserMenu = false;
   }
 
   toggleUserMenu() {
     this.showUserMenu = !this.showUserMenu;
     this.showNotifications = false;
   }
 
   logout() {
     // Add your logout logic here
     this.router.navigate(['/login']);
   }

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