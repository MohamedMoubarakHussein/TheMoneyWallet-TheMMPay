import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { trigger, state, style, transition, animate, query, stagger, useAnimation } from '@angular/animations';
import { RecurringPaymentService, CreateRecurringPaymentRequest } from '../../services/recurring-payment/recurring-payment.service';
import { RecurringPayment } from '../../entity/UnifiedResponse';
import { AnimationService } from '../../services/animation/animation.service';

@Component({
  selector: 'app-recurring-payments',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './recurring-payments.component.html',
  styleUrls: ['./recurring-payments.component.css'],
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(15px) scale(0.97)' }),
        animate('0.4s {{delay}}ms cubic-bezier(0.35, 0, 0.25, 1)', 
          style({ opacity: 1, transform: 'translateY(0) scale(1)' })
        )
      ], { params: { delay: 0 } }),
      transition(':leave', [
        animate('0.3s ease-out', style({ opacity: 0, transform: 'scale(0.97)' }))
      ])
    ]),
    trigger('errorAnimation', [
      transition(':enter', [
        style({ opacity: 0, height: 0 }),
        animate('0.2s ease-out', style({ opacity: 1, height: '*' }))
      ]),
      transition(':leave', [
        animate('0.2s ease-out', style({ opacity: 0, height: 0 }))
      ])
    ])
  ]
})
export class RecurringPaymentsComponent implements OnInit, OnDestroy {
  recurringPayments: RecurringPayment[] = [];
  isLoading = false;
  showCreateForm = false;
  selectedPayment: RecurringPayment | null = null;
  
  paymentForm: FormGroup;
  searchQuery = '';
  statusFilter = 'all';
  currentPage = 1;
  itemsPerPage = 10;
  
  private destroy$ = new Subject<void>();

  constructor(
    private recurringPaymentService: RecurringPaymentService,
    private fb: FormBuilder,
    private animationService: AnimationService
  ) {
    this.paymentForm = this.fb.group({
      walletId: ['', Validators.required],
      name: ['', Validators.required],
      amount: [0, [Validators.required, Validators.min(0.01)]],
      currency: ['USD', Validators.required],
      frequency: ['monthly', Validators.required],
      nextPaymentDate: ['', Validators.required],
      endDate: [''],
      recipientName: ['', Validators.required],
      recipientEmail: [''],
      recipientPhone: [''],
      recipientAccountNumber: [''],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.loadRecurringPayments();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadRecurringPayments(): void {
    this.isLoading = true;
    this.recurringPaymentService.getRecurringPayments()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (payments) => {
          this.recurringPayments = payments;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading recurring payments:', error);
          this.isLoading = false;
        }
      });
  }

  createRecurringPayment(): void {
    if (this.paymentForm.valid) {
      this.isLoading = true;
      const formValue = this.paymentForm.value;
      
      const paymentData: CreateRecurringPaymentRequest = {
        walletId: formValue.walletId,
        name: formValue.name,
        amount: formValue.amount,
        currency: formValue.currency,
        frequency: formValue.frequency,
        nextPaymentDate: new Date(formValue.nextPaymentDate),
        endDate: formValue.endDate ? new Date(formValue.endDate) : undefined,
        recipient: {
          name: formValue.recipientName,
          email: formValue.recipientEmail,
          phone: formValue.recipientPhone,
          accountNumber: formValue.recipientAccountNumber
        },
        description: formValue.description
      };

      this.recurringPaymentService.createRecurringPayment(paymentData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (payment) => {
            this.recurringPayments.unshift(payment);
            this.showCreateForm = false;
            this.paymentForm.reset();
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error creating recurring payment:', error);
            this.isLoading = false;
          }
        });
    }
  }

  selectPayment(payment: RecurringPayment): void {
    this.selectedPayment = payment;
  }

  togglePaymentStatus(paymentId: string): void {
    this.recurringPaymentService.toggleRecurringPaymentStatus(paymentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (payment) => {
          const index = this.recurringPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            this.recurringPayments[index] = payment;
          }
        },
        error: (error) => {
          console.error('Error toggling payment status:', error);
        }
      });
  }

  pausePayment(paymentId: string): void {
    this.recurringPaymentService.pauseRecurringPayment(paymentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (payment) => {
          const index = this.recurringPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            this.recurringPayments[index] = payment;
          }
        },
        error: (error) => {
          console.error('Error pausing payment:', error);
        }
      });
  }

  resumePayment(paymentId: string): void {
    this.recurringPaymentService.resumeRecurringPayment(paymentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (payment) => {
          const index = this.recurringPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            this.recurringPayments[index] = payment;
          }
        },
        error: (error) => {
          console.error('Error resuming payment:', error);
        }
      });
  }

  skipNextPayment(paymentId: string): void {
    this.recurringPaymentService.skipNextPayment(paymentId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (payment) => {
          const index = this.recurringPayments.findIndex(p => p.id === paymentId);
          if (index !== -1) {
            this.recurringPayments[index] = payment;
          }
        },
        error: (error) => {
          console.error('Error skipping next payment:', error);
        }
      });
  }

  deletePayment(paymentId: string): void {
    if (confirm('Are you sure you want to delete this recurring payment?')) {
      this.recurringPaymentService.deleteRecurringPayment(paymentId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.recurringPayments = this.recurringPayments.filter(p => p.id !== paymentId);
            }
          },
          error: (error) => {
            console.error('Error deleting recurring payment:', error);
          }
        });
    }
  }

  get filteredPayments(): RecurringPayment[] {
    let filtered = this.recurringPayments;
    
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(payment => 
        this.statusFilter === 'active' ? payment.isActive : !payment.isActive
      );
    }
    
    if (this.searchQuery) {
      filtered = filtered.filter(payment => 
        payment.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        payment.recipient.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    }
    
    return filtered;
  }

  get paginatedPayments(): RecurringPayment[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredPayments.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredPayments.length / this.itemsPerPage);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  getFrequencyIcon(frequency: string): string {
    return this.recurringPaymentService.getFrequencyIcon(frequency);
  }

  getFrequencyDisplayName(frequency: string): string {
    return this.recurringPaymentService.getFrequencyDisplayName(frequency);
  }

  isPaymentOverdue(payment: RecurringPayment): boolean {
    return this.recurringPaymentService.isPaymentOverdue(payment);
  }

  getDaysUntilNextPayment(payment: RecurringPayment): number {
    return this.recurringPaymentService.getDaysUntilNextPayment(payment);
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.paymentForm.reset();
    }
  }

  resetForm(): void {
    this.paymentForm.reset();
    this.showCreateForm = false;
  }
} 