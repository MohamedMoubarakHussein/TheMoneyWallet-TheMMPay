import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { BudgetService, CreateBudgetRequest, BudgetAnalytics } from '../../services/budget/budget.service';
import { Budget } from '../../entity/UnifiedResponse';
import { trigger, state, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './budget.component.html',
  styleUrls: ['./budget.component.css'],
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
    trigger('scaleIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.8)' }),
        animate('0.4s ease-out', style({ opacity: 1, transform: 'scale(1)' }))
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
export class BudgetComponent implements OnInit, OnDestroy {
  budgets: Budget[] = [];
  analytics: BudgetAnalytics | null = null;
  isLoading = false;
  showCreateForm = false;
  selectedBudget: Budget | null = null;
  selectedPeriod = 'monthly';
  
  budgetForm: FormGroup;
  searchQuery = '';
  categoryFilter = 'all';
  currentPage = 1;
  itemsPerPage = 10;
  
  private destroy$ = new Subject<void>();

  constructor(
    private budgetService: BudgetService,
    private fb: FormBuilder
  ) {
    this.budgetForm = this.fb.group({
      walletId: ['', Validators.required],
      name: ['', Validators.required],
      amount: [0, [Validators.required, Validators.min(0.01)]],
      currency: ['USD', Validators.required],
      period: ['monthly', Validators.required],
      category: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadBudgets();
    this.loadAnalytics();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadBudgets(): void {
    this.isLoading = true;
    this.budgetService.getBudgets(undefined, true)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (budgets) => {
          this.budgets = budgets;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading budgets:', error);
          this.isLoading = false;
        }
      });
  }

  private loadAnalytics(): void {
    this.budgetService.getBudgetAnalytics(undefined, this.selectedPeriod)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (analytics) => {
          this.analytics = analytics;
        },
        error: (error) => {
          console.error('Error loading analytics:', error);
        }
      });
  }

  createBudget(): void {
    if (this.budgetForm.valid) {
      this.isLoading = true;
      const formValue = this.budgetForm.value;
      
      const budgetData: CreateBudgetRequest = {
        walletId: formValue.walletId,
        name: formValue.name,
        amount: formValue.amount,
        currency: formValue.currency,
        period: formValue.period,
        category: formValue.category,
        startDate: new Date(formValue.startDate),
        endDate: new Date(formValue.endDate)
      };

      this.budgetService.createBudget(budgetData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (budget) => {
            this.budgets.unshift(budget);
            this.showCreateForm = false;
            this.budgetForm.reset();
            this.isLoading = false;
            this.loadAnalytics();
          },
          error: (error) => {
            console.error('Error creating budget:', error);
            this.isLoading = false;
          }
        });
    }
  }

  selectBudget(budget: Budget): void {
    this.selectedBudget = budget;
  }

  updateBudget(budgetId: string, updateData: any): void {
    this.budgetService.updateBudget(budgetId, updateData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (budget) => {
          const index = this.budgets.findIndex(b => b.id === budgetId);
          if (index !== -1) {
            this.budgets[index] = budget;
          }
          this.loadAnalytics();
        },
        error: (error) => {
          console.error('Error updating budget:', error);
        }
      });
  }

  deleteBudget(budgetId: string): void {
    if (confirm('Are you sure you want to delete this budget?')) {
      this.budgetService.deleteBudget(budgetId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (result) => {
            if (result.success) {
              this.budgets = this.budgets.filter(b => b.id !== budgetId);
              this.loadAnalytics();
            }
          },
          error: (error) => {
            console.error('Error deleting budget:', error);
          }
        });
    }
  }

  toggleBudgetStatus(budgetId: string): void {
    this.budgetService.toggleBudgetStatus(budgetId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (budget) => {
          const index = this.budgets.findIndex(b => b.id === budgetId);
          if (index !== -1) {
            this.budgets[index] = budget;
          }
        },
        error: (error) => {
          console.error('Error toggling budget status:', error);
        }
      });
  }

  resetBudget(budgetId: string): void {
    if (confirm('Are you sure you want to reset this budget for the new period?')) {
      this.budgetService.resetBudget(budgetId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (budget) => {
            const index = this.budgets.findIndex(b => b.id === budgetId);
            if (index !== -1) {
              this.budgets[index] = budget;
            }
          },
          error: (error) => {
            console.error('Error resetting budget:', error);
          }
        });
    }
  }

  onPeriodChange(): void {
    this.loadAnalytics();
  }

  get filteredBudgets(): Budget[] {
    let filtered = this.budgets;
    
    if (this.categoryFilter !== 'all') {
      filtered = filtered.filter(budget => budget.category === this.categoryFilter);
    }
    
    if (this.searchQuery) {
      filtered = filtered.filter(budget => 
        budget.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        budget.category.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    }
    
    return filtered;
  }

  get paginatedBudgets(): Budget[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredBudgets.slice(startIndex, startIndex + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredBudgets.length / this.itemsPerPage);
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

  getBudgetUtilization(budget: Budget): number {
    return this.budgetService.calculateUtilization(budget);
  }

  isBudgetOver(budget: Budget): boolean {
    return this.budgetService.isBudgetOver(budget);
  }

  getBudgetStatusColor(budget: Budget): string {
    return this.budgetService.getBudgetStatusColor(budget);
  }

  getBudgetStatusIcon(budget: Budget): string {
    const utilization = this.getBudgetUtilization(budget);
    if (utilization >= 100) return '⚠️';
    if (utilization >= 80) return '🔶';
    if (utilization >= 60) return '🟡';
    return '✅';
  }

  getCategoryIcon(category: string): string {
    const iconMap: { [key: string]: string } = {
      'Food & Dining': '🍽️',
      'Transportation': '🚗',
      'Shopping': '🛍️',
      'Entertainment': '🎬',
      'Utilities': '💡',
      'Healthcare': '🏥',
      'Education': '📚',
      'Travel': '✈️',
      'Insurance': '🛡️',
      'Other': '📋'
    };
    return iconMap[category] || '📋';
  }

  getPeriodIcon(period: string): string {
    const iconMap: { [key: string]: string } = {
      'daily': '📅',
      'weekly': '📆',
      'monthly': '🗓️',
      'yearly': '📅'
    };
    return iconMap[period] || '📅';
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) {
      this.budgetForm.reset();
    }
  }

  resetForm(): void {
    this.budgetForm.reset();
    this.showCreateForm = false;
  }

  get categories(): string[] {
    const categories = [...new Set(this.budgets.map(b => b.category))];
    return categories.sort();
  }

  getBudgetsByCategory(category: string): Budget[] {
    return this.budgets.filter(b => b.category === category);
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

  editBudget(budget: Budget): void {
    this.selectedBudget = budget;
    this.budgetForm.patchValue({
      name: budget.name,
      amount: budget.amount,
      currency: budget.currency,
      period: budget.period,
      category: budget.category,
      startDate: budget.startDate.toISOString().split('T')[0],
      endDate: budget.endDate.toISOString().split('T')[0]
    });
    this.showCreateForm = true;
  }
} 