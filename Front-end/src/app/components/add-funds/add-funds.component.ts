import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AddFundsService, PaymentMethod, BankAccount, CardDetails } from '../../services/add-funds.service';
import { trigger, transition, style, animate, query, group } from '@angular/animations';

@Component({
  selector: 'app-add-funds',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './add-funds.component.html',
  animations: [
    trigger('stepAnimation', [
      transition(':increment', [
        style({ position: 'relative' }),
        query(':enter, :leave', [
          style({
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%'
          })
        ]),
        query(':enter', [
          style({ left: '100%', opacity: 0 })
        ]),
        group([
          query(':leave', [
            animate('300ms ease-out', style({ left: '-100%', opacity: 0 }))
          ]),
          query(':enter', [
            animate('300ms ease-out', style({ left: '0%', opacity: 1 }))
          ])
        ]),
      ]),
      transition(':decrement', [
        style({ position: 'relative' }),
        query(':enter, :leave', [
          style({
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%'
          })
        ]),
        query(':enter', [
          style({ left: '-100%', opacity: 0 })
        ]),
        group([
          query(':leave', [
            animate('300ms ease-out', style({ left: '100%', opacity: 0 }))
          ]),
          query(':enter', [
            animate('300ms ease-out', style({ left: '0%', opacity: 1 }))
          ])
        ]),
      ])
    ])
  ]
})
export class AddFundsComponent implements OnInit, OnDestroy {
  currentStep$: Observable<number>;
  selectedMethod$: Observable<string>;
  amount$: Observable<number>;
  processing$: Observable<boolean>;
  transactionComplete$: Observable<boolean>;

  paymentMethods: PaymentMethod[];
  savedBanks: BankAccount[];
  
  amount: number = 0;
  cardDetails: CardDetails = { number: '', expiry: '', cvc: '' };
  selectedBank: string = '';

  currencyCode = 'USD';
  currencySymbol = '$';
  processingFee = 2.5;

  private destroy$ = new Subject<void>();

  constructor(private addFundsService: AddFundsService) {
    this.currentStep$ = this.addFundsService.getCurrentStep();
    this.selectedMethod$ = this.addFundsService.getSelectedMethod();
    this.amount$ = this.addFundsService.getAmount();
    this.processing$ = this.addFundsService.isProcessing();
    this.transactionComplete$ = this.addFundsService.isTransactionComplete();
    this.paymentMethods = this.addFundsService.getPaymentMethods();
    this.savedBanks = this.addFundsService.getSavedBanks();
  }

  ngOnInit(): void {
    this.amount$.pipe(takeUntil(this.destroy$)).subscribe(amount => this.amount = amount);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.addFundsService.reset();
  }

  selectMethod(methodId: string) {
    this.addFundsService.selectMethod(methodId);
  }

  nextStep() {
    this.addFundsService.setAmount(this.amount);
    this.addFundsService.nextStep();
  }

  previousStep() {
    this.addFundsService.previousStep();
  }

  getMethodName(methodId: string): string {
    return this.paymentMethods.find(m => m.id === methodId)?.name || '';
  }

  processPayment() {
    this.addFundsService.processPayment(this.cardDetails, this.selectedBank);
  }

  connectWallet(walletType: string) {
    console.log(`Connecting to ${walletType}`);
  }
}
