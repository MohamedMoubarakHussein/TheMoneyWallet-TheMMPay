import { isPlatformBrowser } from '@angular/common';

/**
 * Utility functions for safe error handling in both browser and server environments
 */

/**
 * Safely checks if an error is a client-side error (ErrorEvent)
 * Only works in browser environment, returns false in server environment
 */
export function isClientError(error: any, platformId: Object): boolean {
  if (!isPlatformBrowser(platformId)) {
    return false;
  }
  
  try {
    return error?.error instanceof ErrorEvent;
  } catch {
    return false;
  }
}

/**
 * Safely checks if an error is a network error
 * Only works in browser environment, returns false in server environment
 */
export function isNetworkError(error: any, platformId: Object): boolean {
  if (!isPlatformBrowser(platformId)) {
    return false;
  }
  
  try {
    return error?.error instanceof ErrorEvent && error.error.message === 'Network Error';
  } catch {
    return false;
  }
}

/**
 * Safely checks if an error is a timeout error
 * Only works in browser environment, returns false in server environment
 */
export function isTimeoutError(error: any, platformId: Object): boolean {
  if (!isPlatformBrowser(platformId)) {
    return false;
  }
  
  try {
    return error?.error instanceof ErrorEvent && error.error.message === 'Timeout Error';
  } catch {
    return false;
  }
}

/**
 * Gets a safe error message that works in both environments
 */
export function getSafeErrorMessage(error: any, platformId: Object): string {
  if (isClientError(error, platformId)) {
    return error.error.message || 'Client error occurred';
  }
  
  if (error?.message) {
    return error.message;
  }
  
  if (error?.error?.message) {
    return error.error.message;
  }
  
  if (error?.status) {
    return `Server error: ${error.status}`;
  }
  
  return 'An unexpected error occurred';
} 