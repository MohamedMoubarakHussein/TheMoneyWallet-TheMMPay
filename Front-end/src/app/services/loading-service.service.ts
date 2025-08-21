import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type LoadingType = 'default' | 'dots' | 'progress' | 'bars' | 'pulse';

export interface LoadingState {
  isLoading: boolean;
  message?: string;
  subtitle?: string;
  type: LoadingType;
  progress: number;
  id?: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  // Default initial state
  private initialState: LoadingState = {
    isLoading: false,
    message: 'Loading',
    subtitle: '',
    type: 'default',
    progress: 0
  };
  
  // BehaviorSubject to track loading state
  private loadingState = new BehaviorSubject<LoadingState>(this.initialState);
  
  // Public observables
  public isLoading$: Observable<boolean> = new Observable(observer => {
    this.loadingState.subscribe(state => {
      observer.next(state.isLoading);
    });
  });
  
  public loadingState$ = this.loadingState.asObservable();
  
  // Active loading tasks map to handle multiple loading states
  private activeLoadings: Map<string, LoadingState> = new Map();

  constructor() { }

  /**
   * Show the loading indicator
   * @param options Loading options
   */
  show(options: Partial<LoadingState> = {}): string {
    const id = options.id || this.generateId();
    
    const loadingState: LoadingState = {
      ...this.initialState,
      ...options,
      isLoading: true,
      id
    };
    
    this.activeLoadings.set(id, loadingState);
    this.updateLoadingState();
    
    return id;
  }

  /**
   * Hide the loading indicator
   * @param id Optional ID of the loading task to hide
   */
  hide(id?: string): void {
    if (id) {
      this.activeLoadings.delete(id);
    } else {
      this.activeLoadings.clear();
    }
    
    this.updateLoadingState();
  }

  /**
   * Update the progress of a specific loading task
   * @param progress Progress value (0-100)
   * @param id ID of the loading task
   */
  updateProgress(progress: number, id?: string): void {
    if (id && this.activeLoadings.has(id)) {
      const state = this.activeLoadings.get(id);
      if (state) {
        state.progress = Math.min(Math.max(0, progress), 100);
        this.activeLoadings.set(id, state);
        this.updateLoadingState();
      }
    } else if (this.activeLoadings.size > 0) {
      // Update the first loading task if no ID is provided
      const firstKey = this.activeLoadings.keys().next().value;
      if (firstKey) {
        const state = this.activeLoadings.get(firstKey);
        if (state) {
          state.progress = Math.min(Math.max(0, progress), 100);
          this.activeLoadings.set(firstKey, state);
          this.updateLoadingState();
        }
      }
    }
  }

  /**
   * Update the message of a specific loading task
   * @param message New message
   * @param subtitle New subtitle (optional)
   * @param id ID of the loading task
   */
  updateMessage(message: string, subtitle?: string, id?: string): void {
    if (id && this.activeLoadings.has(id)) {
      const state = this.activeLoadings.get(id);
      if (state) {
        state.message = message;
        if (subtitle !== undefined) {
          state.subtitle = subtitle;
        }
        this.activeLoadings.set(id, state);
        this.updateLoadingState();
      }
    } else if (this.activeLoadings.size > 0) {
      // Update the first loading task if no ID is provided
      const firstKey = this.activeLoadings.keys().next().value;
      if (firstKey) {
        const state = this.activeLoadings.get(firstKey);
        if (state) {
          state.message = message;
          if (subtitle !== undefined) {
            state.subtitle = subtitle;
          }
          this.activeLoadings.set(firstKey, state);
          this.updateLoadingState();
        }
      }
    }
  }

  /**
   * Change the loading indicator type
   * @param type Loading type
   * @param id ID of the loading task
   */
  changeType(type: LoadingType, id?: string): void {
    if (id && this.activeLoadings.has(id)) {
      const state = this.activeLoadings.get(id);
      if (state) {
        state.type = type;
        this.activeLoadings.set(id, state);
        this.updateLoadingState();
      }
    } else if (this.activeLoadings.size > 0) {
      // Update the first loading task if no ID is provided
      const firstKey = this.activeLoadings.keys().next().value;
      if (firstKey) {
        const state = this.activeLoadings.get(firstKey);
        if (state) {
          state.type = type;
          this.activeLoadings.set(firstKey, state);
          this.updateLoadingState();
        }
      }
    }
  }

  /**
   * Create a progress-based loading indicator
   * @param message Loading message
   * @param subtitle Optional subtitle
   * @returns ID of the loading task
   */
  showProgressLoading(message: string = 'Loading', subtitle: string = ''): string {
    return this.show({
      message,
      subtitle,
      type: 'progress',
      progress: 0
    });
  }

  /**
   * Private method to update the loading state based on active loading tasks
   */
  private updateLoadingState(): void {
    if (this.activeLoadings.size === 0) {
      this.loadingState.next({
        ...this.initialState
      });
      return;
    }
    
    // Use the first active loading as the current state
    const currentLoading = this.activeLoadings.values().next().value;
    if (currentLoading) {
      this.loadingState.next({...currentLoading});
    } else {
      this.loadingState.next({...this.initialState});
    }
  }

  /**
   * Generate a random ID for loading tasks
   */
  private generateId(): string {
    return 'loading_' + Math.random().toString(36).substring(2, 9);
  }
}