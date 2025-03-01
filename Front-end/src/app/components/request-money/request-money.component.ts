import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface Contact {
  id: string;
  name: string;
  email: string;
  avatar: string;
}

interface Currency {
  code: string;
  name: string;
  symbol: string;
}

@Component({
  selector: 'app-request-money',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './request-money.component.html',
  styleUrls: ['./request-money.component.scss']
})
export class RequestMoneyComponent {
  currentStep = 1;
  requestSent = false;
  currencies: Currency[] = [
    { code: 'USD', name: 'US Dollar', symbol: '$' },
    { code: 'EUR', name: 'Euro', symbol: '€' }
  ];
  selectedCurrency = 'USD';
  requestAmount = 0;
  requestNote = '';
  newRecipientEmail = '';
  
  recentContacts: Contact[] = [
    { id: '1', name: 'John Doe', email: 'john@example.com', avatar: 'assets/avatar1.png' },
    { id: '2', name: 'Jane Smith', email: 'jane@example.com', avatar: 'assets/avatar2.png' }
  ];
  selectedContact?: Contact;

  selectContact(contact: Contact) {
    this.selectedContact = contact;
    this.newRecipientEmail = '';
  }

  nextStep() {
    if (this.currentStep < 3) this.currentStep++;
  }

  previousStep() {
    this.currentStep = Math.max(1, this.currentStep - 1);
  }

  copyPaymentLink() {
    // Implement copy functionality
    console.log('Payment link copied');
  }

  sendRequest() {
    // Simulate API call
    setTimeout(() => {
      this.requestSent = true;
    }, 1500);
  }

  get currencySymbol() {
    return this.currencies.find(c => c.code === this.selectedCurrency)?.symbol || '$';
  }
}