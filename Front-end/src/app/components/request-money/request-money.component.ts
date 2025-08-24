import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { RequestMoneyService, Contact, Currency, RequestMoneyForm } from '../../services/request-money.service';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-request-money',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './request-money.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class RequestMoneyComponent implements OnDestroy {
  currentStep$: Observable<number>;
  requestSent$: Observable<boolean>;
  
  recentContacts: Contact[];
  currencies: Currency[];
  
  formData: RequestMoneyForm = {
    newRecipientEmail: '',
    requestAmount: 0,
    selectedCurrency: 'USD',
    requestNote: ''
  };

  private destroy$ = new Subject<void>();

  constructor(private requestMoneyService: RequestMoneyService, private router: Router) {
    this.currentStep$ = this.requestMoneyService.getCurrentStep();
    this.requestSent$ = this.requestMoneyService.isRequestSent();
    this.recentContacts = this.requestMoneyService.recentContacts;
    this.currencies = this.requestMoneyService.currencies;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.requestMoneyService.reset();
  }

  nextStep() {
    this.requestMoneyService.nextStep(this.formData);
  }

  previousStep() {
    this.requestMoneyService.previousStep();
  }

  sendRequest() {
    this.requestMoneyService.sendRequest().subscribe();
  }

  selectContact(contact: Contact) {
    this.formData.selectedContact = contact;
    this.formData.newRecipientEmail = '';
  }

  get currencySymbol() {
    return this.currencies.find(c => c.code === this.formData.selectedCurrency)?.symbol || '$';
  }
  
  copyPaymentLink() {
    // Implement copy functionality
  }
}