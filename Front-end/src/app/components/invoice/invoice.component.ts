import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { Invoice } from '../../entity/UnifiedResponse';
import { InvoiceManagementService, InvoiceSummary } from '../../services/invoice-management.service';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-invoice',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './invoice.component.html',
  animations: [
    trigger('fadeInUp', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
  ]
})
export class InvoiceComponent {
  invoices$: Observable<Invoice[]>;
  summary$: Observable<InvoiceSummary | null>;
  isLoading$: Observable<boolean>;
  paginatedInvoices$: Observable<Invoice[]>;
  totalPages$: Observable<number>;

  showCreateForm = false;
  invoiceForm: FormGroup;
  searchQuery = '';
  statusFilter = 'all';
  currentPage = 1;

  constructor(
    private invoiceService: InvoiceManagementService,
    private fb: FormBuilder
  ) {
    this.invoices$ = this.invoiceService.getInvoices();
    this.summary$ = this.invoiceService.getSummary();
    this.isLoading$ = this.invoiceService.isLoading.asObservable();
    this.paginatedInvoices$ = this.invoiceService.getPaginatedInvoices();
    this.totalPages$ = this.invoiceService.getTotalPages();

    this.invoiceForm = this.fb.group({
      // Form definition
    });
  }

  createInvoice(): void {
    if (this.invoiceForm.valid) {
      this.invoiceService.createInvoice(this.invoiceForm.value).subscribe(() => {
        this.resetForm();
      });
    }
  }

  deleteInvoice(invoiceId: string): void {
    if (confirm('Are you sure?')) {
      this.invoiceService.deleteInvoice(invoiceId).subscribe();
    }
  }

  onSearchQueryChanged() { this.invoiceService.setSearchQuery(this.searchQuery); }
  onStatusFilterChanged() { this.invoiceService.setStatusFilter(this.statusFilter); }
  nextPage() { this.invoiceService.nextPage(); this.currentPage++; }
  previousPage() { this.invoiceService.previousPage(); this.currentPage--; }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) this.resetForm();
  }

  resetForm(): void {
    this.invoiceForm.reset();
    this.showCreateForm = false;
  }
  
  getStatusClass(status: string): string { return status.toLowerCase(); }
}