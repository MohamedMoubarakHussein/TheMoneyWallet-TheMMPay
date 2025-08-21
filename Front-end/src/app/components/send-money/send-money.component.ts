import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface Recipient {
  id: string;
  name: string;
  accountNumber: string;
  avatar: string;
}

interface Currency {
  code: string;
  name: string;
  symbol: string;
}

@Component({
  selector: 'app-send-money',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './send-money.component.html',

})
export class SendMoneyComponent {
  currentStep = 1;
  transactionComplete = false;
  processing = false;
  fee = 2.5;
  
  recentRecipients: Recipient[] = [
    { id: '1', name: 'John Doe', accountNumber: '**** 1234', avatar: 'assets/avatar1.png' },
    { id: '2', name: 'Jane Smith', accountNumber: '**** 5678', avatar: 'assets/avatar2.png' },
    { id: '3', name: 'Acme Corp', accountNumber: '**** 9012', avatar: 'assets/avatar3.png' }
  ];

  currencies: Currency[] = [
    { code: 'USD', name: 'US Dollar', symbol: '$' },
    { code: 'EUR', name: 'Euro', symbol: '€' },
    { code: 'GBP', name: 'British Pound', symbol: '£' }
  ];

  selectedCurrency = 'USD';
  selectedRecipient?: Recipient;
  newAccountNumber = '';
  
  transactionData = {
    amount: 0,
    note: ''
  };

  nextStep() {
    this.currentStep = Math.min(this.currentStep + 1, 3);
  }

  previousStep() {
    this.currentStep = Math.max(this.currentStep - 1, 1);
  }

  selectRecipient(recipient: Recipient) {
    this.selectedRecipient = recipient;
    this.newAccountNumber = '';
  }

  confirmTransaction() {
    this.processing = true;
    // Simulate API call
    setTimeout(() => {
      this.processing = false;
      this.transactionComplete = true;
    }, 2000);
  }
}