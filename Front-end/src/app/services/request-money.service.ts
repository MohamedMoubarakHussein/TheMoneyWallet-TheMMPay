import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, tap } from 'rxjs/operators';

export interface Contact {
  id: string;
  name: string;
  email: string;
  avatar: string;
}

export interface Currency {
  code: string;
  name: string;
  symbol: string;
}

export interface RequestMoneyForm {
  selectedContact?: Contact;
  newRecipientEmail: string;
  requestAmount: number;
  selectedCurrency: string;
  requestNote: string;
}

@Injectable({
  providedIn: 'root'
})
export class RequestMoneyService {
  private currentStep = new BehaviorSubject<number>(1);
  private requestSent = new BehaviorSubject<boolean>(false);
  private formData = new BehaviorSubject<RequestMoneyForm | null>(null);

  recentContacts: Contact[] = [
    { id: '1', name: 'John Doe', email: 'john@example.com', avatar: 'assets/avatar1.png' },
    { id: '2', name: 'Jane Smith', email: 'jane@example.com', avatar: 'assets/avatar2.png' }
  ];
  currencies: Currency[] = [
    { code: 'USD', name: 'US Dollar', symbol: '$' },
    { code: 'EUR', name: 'Euro', symbol: '€' }
  ];

  constructor() { }

  getCurrentStep(): Observable<number> { return this.currentStep.asObservable(); }
  isRequestSent(): Observable<boolean> { return this.requestSent.asObservable(); }
  getFormData(): Observable<RequestMoneyForm | null> { return this.formData.asObservable(); }

  nextStep(formData: RequestMoneyForm) {
    this.formData.next(formData);
    if (this.currentStep.value < 3) {
      this.currentStep.next(this.currentStep.value + 1);
    }
  }

  previousStep() {
    this.currentStep.next(Math.max(1, this.currentStep.value - 1));
  }

  sendRequest(): Observable<{ success: boolean }> {
    // Simulate API call
    return of({ success: true }).pipe(
      delay(1500),
      tap(() => this.requestSent.next(true))
    );
  }

  reset() {
    this.currentStep.next(1);
    this.requestSent.next(false);
    this.formData.next(null);
  }
}