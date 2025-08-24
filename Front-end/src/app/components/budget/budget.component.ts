import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { Budget } from '../../entity/UnifiedResponse';
import { BudgetManagementService } from '../../services/budget-management.service';
import { BudgetAnalytics } from '../../services/budget/budget.service';
import { trigger, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './budget.component.html',
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('500ms ease-out', style({ opacity: 1 }))
      ])
    ]),
    trigger('scaleIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.9)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'scale(1)' }))
      ])
    ])
  ]
})
export class BudgetComponent implements OnInit {
  budgets$: Observable<Budget[]>;
  analytics$: Observable<BudgetAnalytics | null>;
  isLoading$: Observable<boolean>;
  paginatedBudgets$: Observable<Budget[]>;
  totalPages$: Observable<number>;

  showCreateForm = false;
  selectedBudget: Budget | null = null;
  budgetForm: FormGroup;
  searchQuery = '';
  categoryFilter = 'all';
  currentPage = 1;
  categories: string[] = []; // Populate this from a service or static data

  constructor(
    private budgetService: BudgetManagementService,
    private fb: FormBuilder
  ) {
    this.budgets$ = this.budgetService.getBudgets();
    this.analytics$ = this.budgetService.getAnalytics();
    this.isLoading$ = this.budgetService.isLoading.asObservable();
    this.paginatedBudgets$ = this.budgetService.getPaginatedBudgets();
    this.totalPages$ = this.budgetService.getTotalPages();

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
    this.budgets$.subscribe(budgets => {
      this.categories = [...new Set(budgets.map(b => b.category))];
    });
  }

  createBudget(): void {
    if (this.budgetForm.valid) {
      this.budgetService.createBudget(this.budgetForm.value).subscribe(() => {
        this.resetForm();
      });
    }
  }

  editBudget(budget: Budget): void {
    this.selectedBudget = budget;
    this.budgetForm.patchValue(budget);
    this.showCreateForm = true;
  }

  deleteBudget(budgetId: string): void {
    if (confirm('Are you sure?')) {
      this.budgetService.deleteBudget(budgetId).subscribe();
    }
  }
  
  onSearchQueryChanged() { this.budgetService.setSearchQuery(this.searchQuery); }
  onCategoryFilterChanged() { this.budgetService.setCategoryFilter(this.categoryFilter); }
  nextPage() { this.budgetService.nextPage(); this.currentPage++; }
  previousPage() { this.budgetService.previousPage(); this.currentPage--; }
  
  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    if (!this.showCreateForm) this.resetForm();
  }

  resetForm(): void {
    this.budgetForm.reset();
    this.selectedBudget = null;
    this.showCreateForm = false;
  }
  
  getCategoryIcon(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    category: string
  ): string { 
    // Logic to return icon based on category, if needed in the future
    return '💰'; 
  }
  getPeriodIcon(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    period: string
  ): string { 
    // Logic to return icon based on period, if needed in the future
    return '📅'; 
  }
  getBudgetUtilization(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    budget: Budget
  ): number { return (budget.spent / budget.amount) * 100; }
  getBudgetStatusColor(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    budget: Budget
  ): string { 
    // Logic to return color based on budget status, if needed in the future
    return 'text-green-500'; 
  }
  getBudgetStatusIcon(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    budget: Budget
  ): string { 
    // Logic to return icon based on budget status, if needed in the future
    return '✅'; 
  }
}