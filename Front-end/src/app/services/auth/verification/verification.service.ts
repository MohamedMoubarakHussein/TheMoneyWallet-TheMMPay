import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { VerificationResponse, ResendCodeResponse } from '../../../entity/UnifiedResponse';


@Injectable({
  providedIn: 'root'
})
export class VerificationService {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  
  verifyCode(code: string, token: string): Observable<VerificationResponse> {

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'UserCode': code,    
      'Authorization': token     
    });

    return this.http.post<VerificationResponse>(
      `${this.apiUrl}/auth/verify`,
      {}, 
      { headers }
    ).pipe(
      map(response => {
      
        return {
          success: response.success,
          message: response.message || 'Code verified successfully',
          data: response.data
        };
      }),
      catchError(error => {
        let errorMessage = 'Verification failed. Please try again.';
        
        switch (error.status) {
          case 400:
            errorMessage = error.error?.message || 'Invalid verification code';
            break;
          case 429:
            errorMessage = 'Too many attempts. Please try again later.';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          default:
            errorMessage = 'Verification failed. Please try again.';
        }
        
        console.error('Verification error:', error);
        return throwError(() => new Error(errorMessage));
      })
    );
  }

 
  resendVerificationCode(token: string): Observable<ResendCodeResponse> {
   const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token     
    });

    return this.http.post<ResendCodeResponse>(
      `${this.apiUrl}/auth/resend`,
      
        headers
      
    ).pipe(
      map(response => {
        return {
          success: response.success,
          message: response.message || 'Verification code sent successfully',
          cooldownTime: response.cooldownTime || 60 
        };
      }),
      catchError(error => {
        let errorMessage = 'Failed to resend code. Please try again.';
        
        switch (error.status) {
          case 429:
            errorMessage = 'Please wait before requesting another code.';
            break;
          case 404:
            errorMessage = 'Email not found. Please check your email address.';
            break;
          case 500:
            errorMessage = 'Server error. Please try again later.';
            break;
          default:
            errorMessage = 'Failed to resend code. Please try again.';
        }
        
        console.error('Resend verification error:', error);
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  
  validateCodeFormat(code: string): boolean {
    const cleanCode = code.trim();
    
    if (!cleanCode) {
      return false;
    }
    
    
    if (cleanCode.length !== 6) {
      return false;
    }
    
   return true;
  }

  
  formatCodeForDisplay(code: string): string {
    const cleanCode = code.replace(/\D/g, '');
    
   
    return cleanCode.replace(/(\d{3})(\d{3})/, '$1 $2');
  }
}