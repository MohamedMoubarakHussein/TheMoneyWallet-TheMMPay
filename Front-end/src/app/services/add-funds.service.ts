import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface PaymentMethod {
  id: string;
  name: string;
  description: string;
}

export interface BankAccount {
  id: string;
  name: string;
  last4: string;
}

export interface CardDetails {
  number: string;
  expiry: string;
  cvc: string;
}

@Injectable({
  providedIn: 'root'
})
export class AddFundsService {
  private currentStep = new BehaviorSubject<number>(1);
  private selectedMethod = new BehaviorSubject<string>('');
  private amount = new BehaviorSubject<number>(0);
  private processing = new BehaviorSubject<boolean>(false);
  private transactionComplete = new BehaviorSubject<boolean>(false);

  private paymentMethods: PaymentMethod[] = [
    { id: 'card', name: 'Credit/Debit Card', description: 'Instant deposit' },
    { id: 'bank', name: 'Bank Transfer', description: '1-3 business days' },
    { id: 'wallet', name: 'Digital Wallet', description: 'PayPal, Google Pay' }
  ];

  private savedBanks: BankAccount[] = [
    { id: '1', name: 'Chase Bank', last4: '1234' },
    { id: '2', name: 'Bank of America', last4: '5678' }
  ];

  constructor() { }

  // Observables for component to subscribe to
  getCurrentStep(): Observable<number> { return this.currentStep.asObservable(); }
  getSelectedMethod(): Observable<string> { return this.selectedMethod.asObservable(); }
  getAmount(): Observable<number> { return this.amount.asObservable(); }
  isProcessing(): Observable<boolean> { return this.processing.asObservable(); }
  isTransactionComplete(): Observable<boolean> { return this.transactionComplete.asObservable(); }

  // Getters for static data
  getPaymentMethods(): PaymentMethod[] { return this.paymentMethods; }
  getSavedBanks(): BankAccount[] { return this.savedBanks; }
  
  // Actions
  selectMethod(methodId: string) {
    this.selectedMethod.next(methodId);
  }

  setAmount(amount: number) {
    this.amount.next(amount);
  }

  nextStep() {
    if (this.currentStep.value === 1 && !this.selectedMethod.value) return;
    this.currentStep.next(this.currentStep.value + 1);
  }

  previousStep() {
    this.currentStep.next(this.currentStep.value - 1);
  }

  processPayment(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    cardDetails: CardDetails,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    selectedBank: string
  ) {
    this.processing.next(true);
    // Simulate payment processing
    setTimeout(() => {
      this.processing.next(false);
      this.transactionComplete.next(true);
    }, 2000);
  }

  reset() {
    this.currentStep.next(1);
    this.selectedMethod.next('');
    this.amount.next(0);
    this.processing.next(false);
    this.transactionComplete.next(false);
  }
}