import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { Budget, ApiResponse, BudgetRecommendation, BudgetAlert } from '../../entity/UnifiedResponse';
import { environment } from '../../environments/environment';

export interface CreateBudgetRequest {
  walletId: string;
  name: string;
  amount: number;
  currency: string;
  period: 'daily' | 'weekly' | 'monthly' | 'yearly';
  category: string;
  startDate: Date;
  endDate: Date;
}

export interface UpdateBudgetRequest {
  name?: string;
  amount?: number;
  period?: 'daily' | 'weekly' | 'monthly' | 'yearly';
  category?: string;
  startDate?: Date;
  endDate?: Date;
  isActive?: boolean;
}

export interface BudgetAnalytics {
  totalBudget: number;
  totalSpent: number;
  totalRemaining: number;
  utilizationRate: number;
  categoryBreakdown: {
    category: string;
    budgeted: number;
    spent: number;
    remaining: number;
    utilizationRate: number;
  }[];
  periodComparison: {
    period: string;
    budgeted: number;
    spent: number;
    variance: number;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private readonly apiUrl = `${environment.apiUrl}/budgets`;
  private budgetsSubject = new BehaviorSubject<Budget[]>([]);
  
  public budgets$ = this.budgetsSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Get all budgets for a user
  getBudgets(walletId?: string, isActive?: boolean): Observable<Budget[]> {
    const params = new URLSearchParams();
    if (walletId) params.set('walletId', walletId);
    if (isActive !== undefined) params.set('isActive', isActive.toString());

    return this.http.get<ApiResponse<Budget[]>>(`${this.apiUrl}?${params.toString()}`)
      .pipe(
        map(response => response.data || []),
        tap(budgets => this.budgetsSubject.next(budgets)),
        catchError(this.handleError)
      );
  }

  // Get budget by ID
  getBudgetById(budgetId: string): Observable<Budget> {
    return this.http.get<ApiResponse<Budget>>(`${this.apiUrl}/${budgetId}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Create new budget
  createBudget(budgetData: CreateBudgetRequest): Observable<Budget> {
    return this.http.post<ApiResponse<Budget>>(`${this.apiUrl}`, budgetData)
      .pipe(
        map(response => response.data),
        tap(newBudget => {
          const currentBudgets = this.budgetsSubject.value;
          this.budgetsSubject.next([newBudget, ...currentBudgets]);
        }),
        catchError(this.handleError)
      );
  }

  // Update budget
  updateBudget(budgetId: string, updateData: UpdateBudgetRequest): Observable<Budget> {
    return this.http.put<ApiResponse<Budget>>(`${this.apiUrl}/${budgetId}`, updateData)
      .pipe(
        map(response => response.data),
        tap(updatedBudget => {
          const currentBudgets = this.budgetsSubject.value;
          const index = currentBudgets.findIndex(b => b.id === budgetId);
          if (index !== -1) {
            currentBudgets[index] = updatedBudget;
            this.budgetsSubject.next([...currentBudgets]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Delete budget
  deleteBudget(budgetId: string): Observable<{ success: boolean; message: string }> {
    return this.http.delete<ApiResponse<{ success: boolean; message: string }>>(`${this.apiUrl}/${budgetId}`)
      .pipe(
        map(response => response.data),
        tap(() => {
          const currentBudgets = this.budgetsSubject.value;
          this.budgetsSubject.next(currentBudgets.filter(b => b.id !== budgetId));
        }),
        catchError(this.handleError)
      );
  }

  // Toggle budget active status
  toggleBudgetStatus(budgetId: string): Observable<Budget> {
    return this.http.patch<ApiResponse<Budget>>(`${this.apiUrl}/${budgetId}/toggle`, {})
      .pipe(
        map(response => response.data),
        tap(updatedBudget => {
          const currentBudgets = this.budgetsSubject.value;
          const index = currentBudgets.findIndex(b => b.id === budgetId);
          if (index !== -1) {
            currentBudgets[index] = updatedBudget;
            this.budgetsSubject.next([...currentBudgets]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Get budget analytics
  getBudgetAnalytics(walletId?: string, period?: string): Observable<BudgetAnalytics> {
    const params = new URLSearchParams();
    if (walletId) params.set('walletId', walletId);
    if (period) params.set('period', period);

    return this.http.get<ApiResponse<BudgetAnalytics>>(`${this.apiUrl}/analytics?${params.toString()}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  // Get budget recommendations
  getBudgetRecommendations(walletId: string): Observable<BudgetRecommendation[]> {
    return this.http.get<ApiResponse<BudgetRecommendation[]>>(`${this.apiUrl}/recommendations?walletId=${walletId}`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Get budget alerts (when spending approaches limit)
  getBudgetAlerts(walletId?: string): Observable<BudgetAlert[]> {
    let params = '';
    if (walletId) params = `?walletId=${walletId}`;

    return this.http.get<ApiResponse<BudgetAlert[]>>(`${this.apiUrl}/alerts${params}`)
      .pipe(
        map(response => response.data || []),
        catchError(this.handleError)
      );
  }

  // Reset budget for new period
  resetBudget(budgetId: string): Observable<Budget> {
    return this.http.post<ApiResponse<Budget>>(`${this.apiUrl}/${budgetId}/reset`, {})
      .pipe(
        map(response => response.data),
        tap(updatedBudget => {
          const currentBudgets = this.budgetsSubject.value;
          const index = currentBudgets.findIndex(b => b.id === budgetId);
          if (index !== -1) {
            currentBudgets[index] = updatedBudget;
            this.budgetsSubject.next([...currentBudgets]);
          }
        }),
        catchError(this.handleError)
      );
  }

  // Get current budgets value
  getCurrentBudgets(): Budget[] {
    return this.budgetsSubject.value;
  }

  // Clear budgets (for logout)
  clearBudgets(): void {
    this.budgetsSubject.next([]);
  }

  // Refresh budgets
  refreshBudgets(walletId?: string, isActive?: boolean): void {
    this.getBudgets(walletId, isActive).subscribe();
  }

  // Utility method to calculate budget utilization
  calculateUtilization(budget: Budget): number {
    if (budget.amount === 0) return 0;
    return (budget.spent / budget.amount) * 100;
  }

  // Utility method to check if budget is over
  isBudgetOver(budget: Budget): boolean {
    return budget.spent > budget.amount;
  }

  // Utility method to get budget status color
  getBudgetStatusColor(budget: Budget): string {
    const utilization = this.calculateUtilization(budget);
    if (utilization >= 100) return '#F44336'; // Red
    if (utilization >= 80) return '#FF9800'; // Orange
    if (utilization >= 60) return '#FFC107'; // Yellow
    return '#4CAF50'; // Green
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      errorMessage = error.error?.message || `Server Error: ${error.status} ${error.statusText}`;
    }
    
    console.error('BudgetService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}