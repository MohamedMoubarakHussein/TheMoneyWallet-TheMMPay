import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SigninService {

  constructor() { }



  signin(email: string, password: string): Observable<User> {
    return this.http.post<UnifiedResponse>(`${this.apiUrl}auth/signin`, { email, password }, {
      withCredentials: true,
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }).pipe(
      switchMap(response => {
        const token = response.data?.['auth']?.['token'];
        if (!token) {
          return throwError(() => new Error('Token missing'));
        }
  
        return this.extractUserFromResponse(token).pipe(
          map(user => {
            this.updateSession(user, token , 60*60*606*6);
            return user;
          })
        );
      }),
      retry(1), // retry only on network/temporary error
      catchError(err => {
        return throwError(() => new Error('Signin failed: ' + err.message));
      })
    );
  }
  
   
}
