import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Biller {
  id: string;
  name: string;
  category: string;
  accountNumber: string;
  amountDue: number;
  currency: string;
  logo: string;
}

export interface PaymentMethod {
  id: string;
  name: string;
  last4: string;
}

@Injectable({
  providedIn: 'root'
})
export class PayBillService {
  private currentStep = new BehaviorSubject<number>(1);
  private transactionComplete = new BehaviorSubject<boolean>(false);
  private processing = new BehaviorSubject<boolean>(false);
  private selectedBiller = new BehaviorSubject<Biller | null>(null);
  private paymentAmount = new BehaviorSubject<number>(0);
  private paymentDate = new BehaviorSubject<string>(new Date().toISOString().split('T')[0]);
  private selectedPaymentMethod = new BehaviorSubject<string>('');
  private searchQuery = new BehaviorSubject<string>('');
  private selectedCategory = new BehaviorSubject<string>('All');

  private categories = ['All', 'Utilities', 'Internet', 'Credit Cards', 'Insurance'];
  private billers: Biller[] = [
    { id: '1', name: 'City Power Utility', category: 'Utilities', accountNumber: '**** 1234', amountDue: 150.00, currency: 'USD', logo: 'assets/power-company.png' },
    { id: '2', name: 'Global Internet Co', category: 'Internet', accountNumber: '**** 5678', amountDue: 75.00, currency: 'USD', logo: 'assets/internet-company.png' }
  ];
  private paymentMethods: PaymentMethod[] = [
    { id: '1', name: 'Visa', last4: '1234' },
    { id: '2', name: 'Checking Account', last4: '5678' }
  ];

  filteredBillers$: Observable<Biller[]>;

  constructor() {
    this.filteredBillers$ = combineLatest([
      this.searchQuery,
      this.selectedCategory
    ]).pipe(
      map(([query, category]) => 
        this.billers.filter(biller => {
          const matchesCategory = category === 'All' || biller.category === category;
          const matchesSearch = biller.name.toLowerCase().includes(query.toLowerCase());
          return matchesCategory && matchesSearch;
        })
      )
    );
  }

  // Observables
  getCurrentStep(): Observable<number> { return this.currentStep.asObservable(); }
  isTransactionComplete(): Observable<boolean> { return this.transactionComplete.asObservable(); }
  isProcessing(): Observable<boolean> { return this.processing.asObservable(); }
  getSelectedBiller(): Observable<Biller | null> { return this.selectedBiller.asObservable(); }
  
  // Getters for static data
  getCategories(): string[] { return this.categories; }
  getPaymentMethods(): PaymentMethod[] { return this.paymentMethods; }

  // Actions
  nextStep() {
    if (this.currentStep.value === 1 && !this.selectedBiller.value) return;
    this.currentStep.next(this.currentStep.value + 1);
  }

  previousStep() {
    this.currentStep.next(this.currentStep.value - 1);
  }

  selectBiller(biller: Biller) {
    this.selectedBiller.next(biller);
    this.paymentAmount.next(biller.amountDue);
  }

  setSearchQuery(query: string) {
    this.searchQuery.next(query);
  }

  selectCategory(category: string) {
    this.selectedCategory.next(category);
  }

  setPaymentDetails(amount: number, date: string, method: string) {
    this.paymentAmount.next(amount);
    this.paymentDate.next(date);
    this.selectedPaymentMethod.next(method);
  }

  processPayment() {
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
    this.selectedBiller.next(null);
    this.paymentAmount.next(0);
    this.paymentDate.next(new Date().toISOString().split('T')[0]);
    this.selectedPaymentMethod.next('');
    this.searchQuery.next('');
    this.selectedCategory.next('All');
  }
}