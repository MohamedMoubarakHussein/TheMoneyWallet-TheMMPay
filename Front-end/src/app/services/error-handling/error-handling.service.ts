import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

export interface AppError {
  id: string;
  type: 'error' | 'warning' | 'info';
  title: string;
  message: string;
  details?: string;
  timestamp: Date;
  isDismissed: boolean;
  actionUrl?: string;
  actionLabel?: string;
  retryable: boolean;
  retryAction?: () => void;
}

export interface ErrorConfig {
  showToast?: boolean;
  logToConsole?: boolean;
  showInUI?: boolean;
  autoDismiss?: boolean;
  dismissDelay?: number;
  retryable?: boolean;
  retryAction?: () => void;
  actionUrl?: string;
  actionLabel?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlingService {
  private errorsSubject = new BehaviorSubject<AppError[]>([]);
  private isGlobalErrorSubject = new BehaviorSubject<boolean>(false);
  
  public errors$ = this.errorsSubject.asObservable();
  public isGlobalError$ = this.isGlobalErrorSubject.asObservable();

  private errorIdCounter = 0;

  constructor() {}

  // Handle HTTP errors
  handleHttpError(error: HttpErrorResponse, context?: string, config?: ErrorConfig): AppError {
    const errorConfig: ErrorConfig = {
      showToast: true,
      logToConsole: true,
      showInUI: true,
      autoDismiss: false,
      dismissDelay: 5000,
      retryable: this.isRetryableError(error),
      ...config
    };

    const appError = this.createError(
      this.getHttpErrorMessage(error),
      this.getHttpErrorTitle(error),
      'error',
      context,
      errorConfig
    );

    this.processError(appError, errorConfig);
    return appError;
  }

  // Handle general application errors
  handleError(
    error: Error | string,
    title?: string,
    context?: string,
    config?: ErrorConfig
  ): AppError {
    const errorConfig: ErrorConfig = {
      showToast: true,
      logToConsole: true,
      showInUI: true,
      autoDismiss: false,
      dismissDelay: 5000,
      retryable: false,
      ...config
    };

    const errorMessage = typeof error === 'string' ? error : error.message;
    const errorTitle = title || (typeof error === 'string' ? 'Error' : error.name);

    const appError = this.createError(
      errorMessage,
      errorTitle,
      'error',
      context,
      errorConfig
    );

    this.processError(appError, errorConfig);
    return appError;
  }

  // Handle warnings
  handleWarning(
    message: string,
    title: string = 'Warning',
    context?: string,
    config?: ErrorConfig
  ): AppError {
    const errorConfig: ErrorConfig = {
      showToast: true,
      logToConsole: false,
      showInUI: true,
      autoDismiss: true,
      dismissDelay: 3000,
      retryable: false,
      ...config
    };

    const appError = this.createError(
      message,
      title,
      'warning',
      context,
      errorConfig
    );

    this.processError(appError, errorConfig);
    return appError;
  }

  // Handle info messages
  handleInfo(
    message: string,
    title: string = 'Information',
    context?: string,
    config?: ErrorConfig
  ): AppError {
    const errorConfig: ErrorConfig = {
      showToast: true,
      logToConsole: false,
      showInUI: true,
      autoDismiss: true,
      dismissDelay: 2000,
      retryable: false,
      ...config
    };

    const appError = this.createError(
      message,
      title,
      'info',
      context,
      errorConfig
    );

    this.processError(appError, errorConfig);
    return appError;
  }

  // Dismiss an error
  dismissError(errorId: string): void {
    const currentErrors = this.errorsSubject.value;
    const updatedErrors = currentErrors.map(error => 
      error.id === errorId ? { ...error, isDismissed: true } : error
    );
    this.errorsSubject.next(updatedErrors);

    // Remove dismissed errors after a delay
    setTimeout(() => {
      const filteredErrors = this.errorsSubject.value.filter(error => !error.isDismissed);
      this.errorsSubject.next(filteredErrors);
    }, 300);
  }

  // Dismiss all errors
  dismissAllErrors(): void {
    const currentErrors = this.errorsSubject.value;
    const updatedErrors = currentErrors.map(error => ({ ...error, isDismissed: true }));
    this.errorsSubject.next(updatedErrors);

    setTimeout(() => {
      this.errorsSubject.next([]);
    }, 300);
  }

  // Retry an error
  retryError(errorId: string): void {
    const error = this.errorsSubject.value.find(e => e.id === errorId);
    if (error && error.retryable && error.retryAction) {
      error.retryAction();
      this.dismissError(errorId);
    }
  }

  // Clear all errors
  clearErrors(): void {
    this.errorsSubject.next([]);
    this.isGlobalErrorSubject.next(false);
  }

  // Get current errors
  getCurrentErrors(): AppError[] {
    return this.errorsSubject.value;
  }

  // Check if there are any active errors
  hasActiveErrors(): boolean {
    return this.errorsSubject.value.length > 0;
  }

  // Check if there are any global errors
  hasGlobalErrors(): boolean {
    return this.isGlobalErrorSubject.value;
  }

  // Get errors by type
  getErrorsByType(type: 'error' | 'warning' | 'info'): AppError[] {
    return this.errorsSubject.value.filter(error => error.type === type);
  }

  // Get errors by context
  getErrorsByContext(context: string): AppError[] {
    return this.errorsSubject.value.filter(error => error.details === context);
  }

  // Private methods
  private createError(
    message: string,
    title: string,
    type: 'error' | 'warning' | 'info',
    context?: string,
    config?: ErrorConfig
  ): AppError {
    return {
      id: `error_${++this.errorIdCounter}`,
      type,
      title,
      message,
      details: context,
      timestamp: new Date(),
      isDismissed: false,
      retryable: config?.retryable || false,
      retryAction: config?.retryAction,
      actionUrl: config?.actionUrl,
      actionLabel: config?.actionLabel
    };
  }

  private processError(error: AppError, config: ErrorConfig): void {
    // Add to errors list
    const currentErrors = this.errorsSubject.value;
    this.errorsSubject.next([error, ...currentErrors]);

    // Log to console if configured
    if (config.logToConsole) {
      this.logErrorToConsole(error);
    }

    // Set global error flag for critical errors
    if (error.type === 'error' && !config.retryable) {
      this.isGlobalErrorSubject.next(true);
    }

    // Auto-dismiss if configured
    if (config.autoDismiss) {
      setTimeout(() => {
        this.dismissError(error.id);
      }, config.dismissDelay || 5000);
    }
  }

  private logErrorToConsole(error: AppError): void {
    const timestamp = error.timestamp.toISOString();
    const context = error.details ? ` [${error.details}]` : '';
    
    switch (error.type) {
      case 'error':
        console.error(`[${timestamp}]${context} ${error.title}: ${error.message}`);
        break;
      case 'warning':
        console.warn(`[${timestamp}]${context} ${error.title}: ${error.message}`);
        break;
      case 'info':
        console.info(`[${timestamp}]${context} ${error.title}: ${error.message}`);
        break;
    }
  }

  private getHttpErrorMessage(error: HttpErrorResponse): string {
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      // Client-side error
      return error.error.message;
    } else {
      // Server-side error
      if (error.error?.message) {
        return error.error.message;
      }
      
      switch (error.status) {
        case 400:
          return 'Bad request. Please check your input and try again.';
        case 401:
          return 'Unauthorized. Please log in again.';
        case 403:
          return 'Access denied. You don\'t have permission to perform this action.';
        case 404:
          return 'Resource not found. Please check the URL and try again.';
        case 409:
          return 'Conflict. The resource already exists or has been modified.';
        case 422:
          return 'Validation error. Please check your input and try again.';
        case 429:
          return 'Too many requests. Please wait a moment and try again.';
        case 500:
          return 'Internal server error. Please try again later.';
        case 502:
          return 'Bad gateway. Please try again later.';
        case 503:
          return 'Service unavailable. Please try again later.';
        case 504:
          return 'Gateway timeout. Please try again later.';
        default:
          return `An error occurred (${error.status}). Please try again.`;
      }
    }
  }

  private getHttpErrorTitle(error: HttpErrorResponse): string {
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      return 'Network Error';
    } else {
      switch (error.status) {
        case 400:
          return 'Bad Request';
        case 401:
          return 'Unauthorized';
        case 403:
          return 'Access Denied';
        case 404:
          return 'Not Found';
        case 409:
          return 'Conflict';
        case 422:
          return 'Validation Error';
        case 429:
          return 'Too Many Requests';
        case 500:
          return 'Server Error';
        case 502:
          return 'Bad Gateway';
        case 503:
          return 'Service Unavailable';
        case 504:
          return 'Gateway Timeout';
        default:
          return 'Error';
      }
    }
  }

  private isRetryableError(error: HttpErrorResponse): boolean {
    // Retry on network errors and 5xx server errors
    if (typeof ErrorEvent !== 'undefined' && error.error instanceof ErrorEvent) {
      return true; // Network errors are retryable
    }
    
    // Retry on server errors (5xx) and some client errors
    return error.status >= 500 || 
           error.status === 408 || // Request Timeout
           error.status === 429;   // Too Many Requests
  }

  // Utility methods for common error scenarios
  handleNetworkError(context?: string): AppError {
    return this.handleError(
      'Network connection error. Please check your internet connection and try again.',
      'Network Error',
      context,
      { retryable: true, retryAction: () => window.location.reload() }
    );
  }

  handleValidationError(errors: any, context?: string): AppError {
    const errorMessages = Object.values(errors).join(', ');
    return this.handleError(
      `Validation failed: ${errorMessages}`,
      'Validation Error',
      context,
      { retryable: false }
    );
  }

  handleAuthenticationError(context?: string): AppError {
    return this.handleError(
      'Your session has expired. Please log in again.',
      'Authentication Error',
      context,
      { retryable: false, actionUrl: '/signin', actionLabel: 'Sign In' }
    );
  }

  handlePermissionError(context?: string): AppError {
    return this.handleError(
      'You don\'t have permission to perform this action.',
      'Permission Denied',
      context,
      { retryable: false }
    );
  }

  handleRateLimitError(context?: string): AppError {
    return this.handleError(
      'Too many requests. Please wait a moment and try again.',
      'Rate Limit Exceeded',
      context,
      { retryable: true, retryAction: () => window.location.reload() }
    );
  }
} 