import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Recipient {
  id: string;
  name: string;
  accountNumber: string;
  avatar: string;
}

export interface Currency {
  code: string;
  name: string;
  symbol: string;
}

export interface TransactionData {
  amount: number;
  note: string;
}

@Injectable({
  providedIn: 'root'
})
export class SendMoneyService {
  private currentStep = new BehaviorSubject<number>(1);
  private transactionComplete = new BehaviorSubject<boolean>(false);
  private processing = new BehaviorSubject<boolean>(false);
  private selectedRecipient = new BehaviorSubject<Recipient | null>(null);
  private newAccountNumber = new BehaviorSubject<string>('');
  private transactionData = new BehaviorSubject<TransactionData>({ amount: 0, note: '' });

  private recentRecipients: Recipient[] = [
    { id: '1', name: 'John Doe', accountNumber: '**** 1234', avatar: 'assets/avatar1.png' },
    { id: '2', name: 'Jane Smith', accountNumber: '**** 5678', avatar: 'assets/avatar2.png' },
    { id: '3', name: 'Acme Corp', accountNumber: '**** 9012', avatar: 'assets/avatar3.png' }
  ];

  private currencies: Currency[] = [
    { code: 'USD', name: 'US Dollar', symbol: '$' },
    { code: 'EUR', name: 'Euro', symbol: '€' },
    { code: 'GBP', name: 'British Pound', symbol: '£' }
  ];

  constructor() { }

  // Observables
  getCurrentStep(): Observable<number> { return this.currentStep.asObservable(); }
  isTransactionComplete(): Observable<boolean> { return this.transactionComplete.asObservable(); }
  isProcessing(): Observable<boolean> { return this.processing.asObservable(); }
  getSelectedRecipient(): Observable<Recipient | null> { return this.selectedRecipient.asObservable(); }
  getNewAccountNumber(): Observable<string> { return this.newAccountNumber.asObservable(); }
  getTransactionData(): Observable<TransactionData> { return this.transactionData.asObservable(); }

  // Getters for static data
  getRecentRecipients(): Recipient[] { return this.recentRecipients; }
  getCurrencies(): Currency[] { return this.currencies; }

  // Actions
  nextStep() {
    this.currentStep.next(Math.min(this.currentStep.value + 1, 3));
  }

  previousStep() {
    this.currentStep.next(Math.max(this.currentStep.value - 1, 1));
  }

  selectRecipient(recipient: Recipient) {
    this.selectedRecipient.next(recipient);
    this.newAccountNumber.next('');
  }

  setNewAccountNumber(accountNumber: string) {
    this.newAccountNumber.next(accountNumber);
    this.selectedRecipient.next(null);
  }

  setTransactionData(data: TransactionData) {
    this.transactionData.next(data);
  }

  confirmTransaction() {
    this.processing.next(true);
    setTimeout(() => {
      this.processing.next(false);
      this.transactionComplete.next(true);
    }, 2000);
  }

  reset() {
    this.currentStep.next(1);
    this.transactionComplete.next(false);
    this.processing.next(false);
    this.selectedRecipient.next(null);
    this.newAccountNumber.next('');
    this.transactionData.next({ amount: 0, note: '' });
  }
}