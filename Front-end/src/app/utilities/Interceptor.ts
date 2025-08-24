import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { AuthService } from './../services/auth/auth.service';
import { User } from '../entity/UnifiedResponse';

/**
 * Enhanced HTTP Interceptor that:
 * 1. Automatically adds authentication tokens to requests
 * 2. Includes HTTP-only cookies (withCredentials: true)
 * 3. Handles token refresh on 401 errors
 * 4. Prevents multiple simultaneous refresh attempts
 * 5. Queues requests during token refresh
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  // Track if token refresh is in progress to prevent multiple simultaneous refreshes
  private isRefreshing = false;
  
  // Subject to queue requests while token is being refreshed
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Add authentication headers and credentials to the request
    const authRequest = this.addAuthHeaders(request);
    
    // Process the request and handle potential authentication errors
    return next.handle(authRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        
        // Check if the error is related to authentication
        if (error.status === 401 && !authRequest.url.includes('/auth/')) {
          // This is an authentication error, attempt to refresh token
          return this.handle401Error(authRequest, next);
        }
        
        // For non-auth errors, just pass them through
        return throwError(error);
      })
    );
  }

  /**
   * Add authentication headers and credentials to the request
   * This method ensures every request has the necessary authentication information
   */
  private addAuthHeaders(request: HttpRequest<unknown>): HttpRequest<unknown> {
    // Get the current authentication token
    const authToken = this.authService.getToken();
    
    // Clone the request to add headers (HTTP requests are immutable)
    const authRequest = request.clone({
      // Always include credentials to send HTTP-only cookies
      setHeaders: {
        // Only add Authorization header if we have a token and it's not a login/signup request
        ...(authToken && !this.isAuthRequest(request.url) ? {
          'Authorization': `Bearer ${authToken}`
        } : {})
      },
      // This is crucial: withCredentials ensures HTTP-only cookies are sent
      withCredentials: true
    });

    return authRequest;
  }

  /**
   * Check if the request is an authentication-related request
   * We don't want to add tokens to login/signup requests
   */
  private isAuthRequest(url: string): boolean {
    return url.includes('/auth/signin') || 
           url.includes('/auth/signup') || 
           url.includes('/auth/google') ||
           url.includes('/auth/refresh');
  }

  /**
   * Handle 401 Unauthorized errors by attempting token refresh
   * This is where the magic happens - automatic token refresh on authentication failure
   */
  private handle401Error(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    
    if (!this.isRefreshing) {
      // Start the refresh process
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      // Attempt to refresh the token using the auth service
      return this.authService.refreshToken().pipe(
        switchMap((authResponse: User) => {
          // Token refresh successful
          this.isRefreshing = false;
          
          // Notify waiting requests that new token is available
          this.refreshTokenSubject.next(authResponse.token);
          
          // Retry the original request with the new token
          const retryRequest = this.addAuthHeaders(request);
          return next.handle(retryRequest);
        }),
        catchError((refreshError) => {
          // Token refresh failed - user needs to log in again
          this.isRefreshing = false;
          
          // The auth service will handle logout and navigation
          console.error('Token refresh failed in interceptor:', refreshError);
          
          return throwError(refreshError);
        })
      );
    } else {
      // Token refresh is already in progress, queue this request
      return this.refreshTokenSubject.pipe(
        // Wait for the refresh to complete
        filter(token => token !== null),
        take(1), // Take only the first emission (when refresh completes)
        switchMap(() => {
          // Retry the request with the new token
          const retryRequest = this.addAuthHeaders(request);
          return next.handle(retryRequest);
        })
      );
    }
  }
}

/**
 * How to register this interceptor in your app.module.ts:
 * 
 * import { HTTP_INTERCEPTORS } from '@angular/common/http';
 * import { AuthInterceptor } from './path/to/auth.interceptor';
 * 
 * @NgModule({
 *   // ... other module configuration
 *   providers: [
 *     {
 *       provide: HTTP_INTERCEPTORS,
 *       useClass: AuthInterceptor,
 *       multi: true // This allows multiple interceptors
 *     }
 *   ]
 * })
 * export class AppModule { }
 */

/**
 * What this interceptor does step by step:
 * 
 * 1. BEFORE REQUEST: Adds Authorization header and ensures withCredentials is true
 * 2. SEND REQUEST: Request goes to server with authentication info
 * 3. IF SUCCESS: Response is returned normally
 * 4. IF 401 ERROR: 
 *    a. Check if token refresh is already in progress
 *    b. If not, start refresh process using HTTP-only cookie
 *    c. If refresh succeeds, retry original request with new token
 *    d. If refresh fails, logout user
 *    e. If refresh is in progress, queue the request until refresh completes
 * 
 * This creates a seamless experience where users never see authentication errors
 * due to expired tokens - the app automatically handles it behind the scenes.
 */