import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { SendMoneyService, Recipient, Currency, TransactionData } from '../../services/send-money.service';
import { trigger, transition, style, animate, query, group } from '@angular/animations';

@Component({
  selector: 'app-send-money',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './send-money.component.html',
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
export class SendMoneyComponent implements OnInit, OnDestroy {
  currentStep$: Observable<number>;
  transactionComplete$: Observable<boolean>;
  processing$: Observable<boolean>;
  selectedRecipient$: Observable<Recipient | null>;
  
  recentRecipients: Recipient[];
  currencies: Currency[];
  
  newAccountNumber: string = '';
  transactionData: TransactionData = { amount: 0, note: '' };
  selectedCurrency = 'USD';
  fee = 2.5;

  private destroy$ = new Subject<void>();

  constructor(private sendMoneyService: SendMoneyService) {
    this.currentStep$ = this.sendMoneyService.getCurrentStep();
    this.transactionComplete$ = this.sendMoneyService.isTransactionComplete();
    this.processing$ = this.sendMoneyService.isProcessing();
    this.selectedRecipient$ = this.sendMoneyService.getSelectedRecipient();
    this.recentRecipients = this.sendMoneyService.getRecentRecipients();
    this.currencies = this.sendMoneyService.getCurrencies();
  }

  ngOnInit(): void {
    this.sendMoneyService.getNewAccountNumber().pipe(takeUntil(this.destroy$)).subscribe(accNum => this.newAccountNumber = accNum);
    this.sendMoneyService.getTransactionData().pipe(takeUntil(this.destroy$)).subscribe(data => this.transactionData = data);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.sendMoneyService.reset();
  }

  nextStep() {
    this.sendMoneyService.setTransactionData(this.transactionData);
    this.sendMoneyService.setNewAccountNumber(this.newAccountNumber);
    this.sendMoneyService.nextStep();
  }

  previousStep() {
    this.sendMoneyService.previousStep();
  }

  selectRecipient(recipient: Recipient) {
    this.sendMoneyService.selectRecipient(recipient);
  }

  confirmTransaction() {
    this.sendMoneyService.confirmTransaction();
  }
}
