import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map, tap } from 'rxjs/operators';
import { Budget } from '../entity/UnifiedResponse';
import { BudgetAnalytics, CreateBudgetRequest } from './budget/budget.service';

@Injectable({
  providedIn: 'root'
})
export class BudgetManagementService {
  private budgets = new BehaviorSubject<Budget[]>([]);
  private analytics = new BehaviorSubject<BudgetAnalytics | null>(null);
  public isLoading = new BehaviorSubject<boolean>(false);
  private searchQuery = new BehaviorSubject<string>('');
  private categoryFilter = new BehaviorSubject<string>('all');
  private currentPage = new BehaviorSubject<number>(1);
  itemsPerPage = 10;

  constructor() {
    this.loadBudgets();
    this.loadAnalytics();
  }

  getBudgets(): Observable<Budget[]> { return this.budgets.asObservable(); }
  getAnalytics(): Observable<BudgetAnalytics | null> { return this.analytics.asObservable(); }

  getFilteredBudgets(): Observable<Budget[]> {
    return this.budgets.pipe(
      map(budgets => 
        budgets.filter(budget => 
          (this.categoryFilter.value === 'all' || budget.category === this.categoryFilter.value) &&
          (budget.name.toLowerCase().includes(this.searchQuery.value.toLowerCase()))
        )
      )
    );
  }

  getPaginatedBudgets(): Observable<Budget[]> {
    return this.getFilteredBudgets().pipe(
      map(budgets => {
        const startIndex = (this.currentPage.value - 1) * this.itemsPerPage;
        return budgets.slice(startIndex, startIndex + this.itemsPerPage);
      })
    );
  }

  getTotalPages(): Observable<number> {
    return this.getFilteredBudgets().pipe(
      map(budgets => Math.ceil(budgets.length / this.itemsPerPage))
    );
  }

  loadBudgets() {
    this.isLoading.next(true);
    of([
      // Mock data
    ]).pipe(delay(1000)).subscribe(budgets => {
      this.budgets.next(budgets);
      this.isLoading.next(false);
    });
  }

  loadAnalytics() {
    // Mock data
    of({ 
      totalBudget: 5000, 
      totalSpent: 2500, 
      totalRemaining: 2500, 
      utilizationRate: 50,
      categoryBreakdown: [],
      periodComparison: []
    })
      .pipe(delay(1000))
      .subscribe(analytics => this.analytics.next(analytics));
  }

  createBudget(budgetData: CreateBudgetRequest): Observable<Budget> {
    this.isLoading.next(true);
    const newBudget: Budget = {
      id: Date.now().toString(),
      userId: 'user1',
      ...budgetData,
      spent: 0,
      remaining: budgetData.amount,
      isActive: true,
      createdAt: new Date(),
      updatedAt: new Date()
    };
    return of(newBudget).pipe(
      delay(1000),
      tap(budget => {
        this.budgets.next([budget, ...this.budgets.value]);
        this.isLoading.next(false);
      })
    );
  }

  updateBudget(budgetId: string, updateData: Partial<Budget>): Observable<Budget> {
    const currentBudgets = this.budgets.value;
    const index = currentBudgets.findIndex(b => b.id === budgetId);
    const updatedBudget = { ...currentBudgets[index], ...updateData, updatedAt: new Date() };
    currentBudgets[index] = updatedBudget;
    this.budgets.next([...currentBudgets]);
    return of(updatedBudget);
  }

  deleteBudget(budgetId: string): Observable<{ success: boolean }> {
    this.budgets.next(this.budgets.value.filter(b => b.id !== budgetId));
    return of({ success: true });
  }
  
  setSearchQuery(query: string) { this.searchQuery.next(query); }
  setCategoryFilter(category: string) { this.categoryFilter.next(category); }
  nextPage() { this.currentPage.next(this.currentPage.value + 1); }
  previousPage() { this.currentPage.next(this.currentPage.value - 1); }
}