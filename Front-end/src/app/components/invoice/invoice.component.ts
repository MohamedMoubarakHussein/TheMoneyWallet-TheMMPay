import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { InvoiceService, CreateInvoiceRequest } from '../../services/invoice/invoice.service';
import { Invoice } from '../../entity/UnifiedResponse';
import { trigger, state, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-invoice',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css'],
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('slideInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('bounceIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.3)' }),
        animate('0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55)', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ]),
    trigger('staggerIn', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(100, [
            animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ]),
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('0.3s ease-out', style({ opacity: 1 }))
      ])
    ])
  ]
})
export class InvoiceComponent implements OnInit, OnDestroy {
  invoices: Invoice[] = [];
  currentStep = 1;
  isLoading = false;
  showCreateForm = false;
  selectedInvoice: Invoice | null = null;
  
  invoiceForm: FormGroup;
  searchQuery = '';
  statusFilter = 'all';
  currentPage = 1;
  itemsPerPage = 10;
  
  // Summary properties
  totalInvoices = 0;
  paidInvoices = 0;
  pendingAmount = 0;
  overdueInvoices = 0;
  draftInvoices = 0;
  sentInvoices = 0;
  
  private destroy$ = new Subject<void>();

  constructor(
    private invoiceService: InvoiceService,
    private fb: FormBuilder
  ) {
    this.invoiceForm = this.fb.group({
      walletId: ['', Validators.required],
      amount: [0, [Validators.required, Validators.min(0.01)]],
      currency: ['USD', Validators.required],
      dueDate: ['', Validators.required],
      recipientName: ['', Validators.required],
      recipientEmail: ['', [Validators.required, Validators.email]],
      recipientPhone: [''],
      recipientAddress: [''],
      items: this.fb.array([]),
      notes: [''],
      terms: ['']
    });
  }

  ngOnInit(): void {
    this.loadInvoices();
    this.setupFormListeners();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupFormListeners(): void {
    this.invoiceForm.get('amount')?.valueChanges.subscribe(() => {
      this.calculateTotal();
    });
  }

  private loadInvoices(): void {
    this.isLoading = true;
    this.invoiceService.getInvoices()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (invoices) => {
          this.invoices = invoices;
          this.calculateSummary();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading invoices:', error);
          this.isLoading = false;
        }
      });
  }

  private calculateSummary(): void {
    this.totalInvoices = this.invoices.length;
    this.paidInvoices = this.invoices.filter(i => i.status === 'paid').length;
    this.draftInvoices = this.invoices.filter(i => i.status === 'draft').length;
    this.sentInvoices = this.invoices.filter(i => i.status === 'sent').length;
    this.overdueInvoices = this.invoices.filter(i => i.status === 'overdue').length;
    
    this.pendingAmount = this.invoices
      .filter(i => i.status !== 'paid')
      .reduce((sum, invoice) => sum + invoice.amount, 0);
  }

  createInvoice(): void {
    if (this.invoiceForm.valid) {
      this.isLoading = true;
      const formValue = this.invoiceForm.value;
      
      const invoiceData: CreateInvoiceRequest = {
        walletId: formValue.walletId,
        amount: formValue.amount,
        currency: formValue.currency,
        dueDate: new Date(formValue.dueDate),
        recipient: {
          name: formValue.recipientName,
          email: formValue.recipientEmail,
          phone: formValue.recipientPhone,
          address: formValue.recipientAddress
        },
        items: formValue.items || [],
        notes: formValue.notes,
        terms: formValue.terms
      };

      this.invoiceService.createInvoice(invoiceData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (invoice) => {
            this.invoices.unshift(invoice);
            this.showCreateForm = false;
            this.invoiceForm.reset();
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error creating invoice:', error);
            this.isLoading = false;
          }
        });
    }
  }

  selectInvoice(invoice: Invoice): void {
    this.selectedInvoice = invoice;
  }

  sendInvoice(invoiceId: string): void {
    this.invoiceService.sendInvoice(invoiceId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.loadInvoices();
          }
        },
        error: (error) => {
          console.error('Error sending invoice:', error);
        }
      });
  }

  markAsPaid(invoiceId: string): void {
    this.invoiceService.markAsPaid(invoiceId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (invoice) => {
          const index = this.invoices.findIndex(i => i.id === invoiceId);
          if (index !== -1) {
            this.invoices[index] = invoice;
          }
        },
        error: (error) => {
          console.error('Error marking invoice as paid:', error);
        }
      });
  }

  cancelInvoice(invoiceId: string): void {
    this.invoiceService.cancelInvoice(invoiceId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (result) => {
          if (result.success) {
            this.loadInvoices();
          }
        },
        error: (error) => {
          console.error('Error cancelling invoice:', error);
        }
      });
  }

  deleteInvoice(invoiceId: string): void {
    if (confirm('Are you sure you want to delete this invoice?')) {
      this.invoiceService.deleteInvoice(invoiceId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.invoices = this.invoices.filter(i => i.id !== invoiceId);
            }
          },
          error: (error) => {
            console.error('Error deleting invoice:', error);
          }
        });
    }
  }

  exportInvoice(invoiceId: string): void {
    this.invoiceService.exportInvoiceAsPdf(invoiceId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `invoice-${invoiceId}.pdf`;
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Error exporting invoice:', error);
        }
      });
  }

  get filteredInvoices(): Invoice[] {
    let filtered = this.invoices;
    
    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(invoice => invoice.status === this.statusFilter);
    }
    
    if (this.searchQuery) {
      filtered = filtered.filter(invoice => 
        invoice.recipient.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        invoice.invoiceNumber.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    }
    
    return filtered;
  }

  get paginatedInvoices(): Invoice[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredInvoices.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredInvoices.length / this.itemsPerPage);
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

  getStatusColor(status: string): string {
    const colorMap: { [key: string]: string } = {
      'draft': '#757575',
      'sent': '#2196F3',
      'paid': '#4CAF50',
      'overdue': '#F44336',
      'cancelled': '#9E9E9E'
    };
    return colorMap[status] || '#757575';
  }

  getStatusClass(status: string): string {
    return status.toLowerCase();
  }

  getStatusIcon(status: string): string {
    const iconMap: { [key: string]: string } = {
      'draft': '📝',
      'sent': '📤',
      'paid': '✅',
      'overdue': '⚠️',
      'cancelled': '❌'
    };
    return iconMap[status] || '📄';
  }

  private calculateTotal(): void {
    // Implementation for calculating total from items
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.invoiceForm.reset();
    }
  }

  resetForm(): void {
    this.invoiceForm.reset();
    this.showCreateForm = false;
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPages = Math.min(5, this.totalPages);
    const start = Math.max(1, this.currentPage - Math.floor(maxPages / 2));
    const end = Math.min(this.totalPages, start + maxPages - 1);
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    
    return pages;
  }
} 