import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { VerificationResponse } from '../../../entity/UnifiedResponse';
import { ResendCodeResponse } from '../../../entity/UnifiedResponse';

@Injectable({
  providedIn: 'root'
})
export class VerificationService {
  private  apiUrl = 'https://localhost:8080'; 
   

  constructor(private http: HttpClient) {}



  verifyCode(code: string, email?: string): Observable<VerificationResponse> {
    const payload = {
      verificationCode: code,
      ...(email && { email }) // Include email if provided
    };

    return this.http.post<VerificationResponse>(
      `${this.apiUrl}/verify-code`, 
      payload
    ).pipe(
      map(response => {
        // Transform response if needed
        return {
          success: response.success,
          message: response.message || 'Code verified successfully',
          data: response.data
        };
      }),
      catchError(error => {
        // Handle HTTP errors
        let errorMessage = 'Verification failed. Please try again.';
        
        if (error.status === 400) {
          errorMessage = error.error?.message || 'Invalid verification code';
        } else if (error.status === 429) {
          errorMessage = 'Too many attempts. Please try again later.';
        } else if (error.status === 500) {
          errorMessage = 'Server error. Please try again later.';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }

 

  resendVerificationCode(email: string): Observable<ResendCodeResponse> {
    const payload = {
      email: email
    };

    return this.http.post<ResendCodeResponse>(
      `${this.apiUrl}/resend-verification-code`, 
      payload
    ).pipe(
      map(response => {
        return {
          success: response.success,
          message: response.message || 'Verification code sent successfully',
          cooldownTime: response.cooldownTime || 60 // Default cooldown time
        };
      }),
      catchError(error => {
        let errorMessage = 'Failed to resend code. Please try again.';
        
        if (error.status === 429) {
          errorMessage = 'Please wait before requesting another code.';
        } else if (error.status === 404) {
          errorMessage = 'Email not found. Please check your email address.';
        } else if (error.status === 500) {
          errorMessage = 'Server error. Please try again later.';
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }


}