import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-add-funds',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './add-funds.component.html',

})
export class AddFundsComponent {
  currentStep = 1;
  selectedMethod: string = '';
  amount: number = 0;
  processing = false;
  transactionComplete = false;
  currencyCode = 'USD';
  currencySymbol = '$';
  processingFee = 2.5;

  paymentMethods = [
    { id: 'card', name: 'Credit/Debit Card', description: 'Instant deposit' },
    { id: 'bank', name: 'Bank Transfer', description: '1-3 business days' },
    { id: 'wallet', name: 'Digital Wallet', description: 'PayPal, Google Pay' }
  ];

  savedBanks = [
    { id: '1', name: 'Chase Bank', last4: '1234' },
    { id: '2', name: 'Bank of America', last4: '5678' }
  ];

  cardDetails = {
    number: '',
    expiry: '',
    cvc: ''
  };

  selectedBank = '';

  selectMethod(methodId: string) {
    this.selectedMethod = methodId;
  }

  nextStep() {
    if (this.currentStep === 1 && !this.selectedMethod) return;
    this.currentStep++;
  }

  previousStep() {
    this.currentStep--;
  }

  getMethodName(methodId: string) {
    return this.paymentMethods.find(m => m.id === methodId)?.name || '';
  }

  processPayment() {
    this.processing = true;
    // Simulate payment processing
    setTimeout(() => {
      this.processing = false;
      this.transactionComplete = true;
    }, 2000);
  }

  connectWallet(walletType: string) {
    // Implement wallet connection logic
    console.log(`Connecting to ${walletType}`);
  }
}