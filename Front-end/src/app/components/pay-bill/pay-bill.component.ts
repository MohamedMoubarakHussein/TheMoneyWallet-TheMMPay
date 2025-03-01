import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface Biller {
  id: string;
  name: string;
  category: string;
  accountNumber: string;
  amountDue: number;
  currency: string;
  logo: string;
}

interface PaymentMethod {
  id: string;
  name: string;
  last4: string;
}

@Component({
  selector: 'app-pay-bill',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './pay-bill.component.html',
  styleUrls: ['./pay-bill.component.scss']
})
export class PayBillComponent {
  currentStep = 1;
  transactionComplete = false;
  processing = false;
  categories = ['Utilities', 'Internet', 'Credit Cards', 'Insurance'];
  selectedCategory = 'All';
  searchQuery = '';
  selectedBiller?: Biller;
  paymentAmount = 0;
  paymentDate = new Date().toISOString().split('T')[0];
  paymentMethods: PaymentMethod[] = [
    { id: '1', name: 'Visa', last4: '1234' },
    { id: '2', name: 'Checking Account', last4: '5678' }
  ];
  selectedPaymentMethod = '';
  saveBiller = false;

  billers: Biller[] = [
    { 
      id: '1', 
      name: 'City Power Utility', 
      category: 'Utilities', 
      accountNumber: '**** 1234', 
      amountDue: 150.00, 
      currency: 'USD',
      logo: 'assets/power-company.png'
    },
    { 
      id: '2', 
      name: 'Global Internet Co', 
      category: 'Internet', 
      accountNumber: '**** 5678', 
      amountDue: 75.00, 
      currency: 'USD',
      logo: 'assets/internet-company.png'
    }
  ];

  get filteredBillers() {
    return this.billers.filter(biller => {
      const matchesCategory = this.selectedCategory === 'All' || 
                            biller.category === this.selectedCategory;
      const matchesSearch = biller.name.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }

  selectBiller(biller: Biller) {
    this.selectedBiller = biller;
    this.paymentAmount = biller.amountDue;
  }

  nextStep() {
    if (this.currentStep === 1 && !this.selectedBiller) return;
    this.currentStep++;
  }

  previousStep() {
    this.currentStep--;
  }

  getPaymentMethod() {
    const method = this.paymentMethods.find(m => m.id === this.selectedPaymentMethod);
    return method ? `${method.name} (****${method.last4})` : '';
  }

  processPayment() {
    this.processing = true;
    // Simulate payment processing
    setTimeout(() => {
      this.processing = false;
      this.transactionComplete = true;
    }, 2000);
  }
}