import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { RecurringPayment } from '../../entity/UnifiedResponse';
import { RecurringPaymentsManagementService } from '../../services/recurring-payments-management.service';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-recurring-payments',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './recurring-payments.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class RecurringPaymentsComponent {
  payments$: Observable<RecurringPayment[]>;
  isLoading$: Observable<boolean>;
  paginatedPayments$: Observable<RecurringPayment[]>;
  totalPages$: Observable<number>;

  showCreateForm = false;
  paymentForm: FormGroup;
  searchQuery = '';
  statusFilter = 'all';
  currentPage = 1;

  constructor(
    private paymentsService: RecurringPaymentsManagementService,
    private fb: FormBuilder
  ) {
    this.payments$ = this.paymentsService.getPayments();
    this.isLoading$ = this.paymentsService.isLoading$();
    this.paginatedPayments$ = this.paymentsService.getPaginatedPayments();
    this.totalPages$ = this.paymentsService.getTotalPages();

    this.paymentForm = this.fb.group({
      // Form definition
    });
  }

  createPayment(): void {
    if (this.paymentForm.valid) {
      this.paymentsService.createPayment(this.paymentForm.value).subscribe(() => {
        this.resetForm();
      });
    }
  }

  deletePayment(paymentId: string): void {
    if (confirm('Are you sure?')) {
      this.paymentsService.deletePayment(paymentId).subscribe();
    }
  }

  onSearchQueryChanged() { this.paymentsService.setSearchQuery(this.searchQuery); }
  onStatusFilterChanged() { this.paymentsService.setStatusFilter(this.statusFilter); }
  nextPage() { this.paymentsService.nextPage(); this.currentPage++; }
  previousPage() { this.paymentsService.previousPage(); this.currentPage--; }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) this.resetForm();
  }

  resetForm(): void {
    this.paymentForm.reset();
    this.showCreateForm = false;
  }
  
  getFrequencyIcon(): string {
    // Logic to return icon based on frequency, if needed in the future
    return 'fas fa-redo';
  }
  getFrequencyDisplayName(): string {
    // Logic to return display name based on frequency, if needed in the future
    return '';
  }
  isPaymentOverdue(): boolean {
    // Logic to check if payment is overdue based on payment object
    return false;
  }
  getDaysUntilNextPayment(): number {
    // Logic to calculate days until next payment based on payment object
    return 0;
  }
}