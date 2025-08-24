import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { PayBillService, Biller, PaymentMethod } from '../../services/pay-bill.service';
import { trigger, transition, style, animate, query, group } from '@angular/animations';

@Component({
  selector: 'app-pay-bill',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './pay-bill.component.html',
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
export class PayBillComponent implements OnDestroy {
  currentStep$: Observable<number>;
  transactionComplete$: Observable<boolean>;
  processing$: Observable<boolean>;
  selectedBiller$: Observable<Biller | null>;
  filteredBillers$: Observable<Biller[]>;

  categories: string[];
  paymentMethods: PaymentMethod[];
  
  searchQuery = '';
  selectedCategory = 'All';
  paymentAmount = 0;
  paymentDate = new Date().toISOString().split('T')[0];
  selectedPaymentMethod = '';
  saveBiller = false;

  private destroy$ = new Subject<void>();

  constructor(private payBillService: PayBillService) {
    this.currentStep$ = this.payBillService.getCurrentStep();
    this.transactionComplete$ = this.payBillService.isTransactionComplete();
    this.processing$ = this.payBillService.isProcessing();
    this.selectedBiller$ = this.payBillService.getSelectedBiller();
    this.filteredBillers$ = this.payBillService.filteredBillers$;
    this.categories = this.payBillService.getCategories();
    this.paymentMethods = this.payBillService.getPaymentMethods();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.payBillService.reset();
  }

  nextStep() {
    this.payBillService.setPaymentDetails(this.paymentAmount, this.paymentDate, this.selectedPaymentMethod);
    this.payBillService.nextStep();
  }

  previousStep() {
    this.payBillService.previousStep();
  }

  selectBiller(biller: Biller) {
    this.payBillService.selectBiller(biller);
  }

  onSearchQueryChanged() {
    this.payBillService.setSearchQuery(this.searchQuery);
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
    this.payBillService.selectCategory(category);
  }

  getPaymentMethodName(methodId: string): string {
    const method = this.paymentMethods.find(m => m.id === methodId);
    return method ? `${method.name} (****${method.last4})` : '';
  }

  processPayment() {
    this.payBillService.processPayment();
  }
}
