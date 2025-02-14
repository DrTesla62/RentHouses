// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import {
    LoginRequest,
    RegisterRequest,
    JwtAuthenticationResponse,
    MessageResponse
} from '../models/auth.model';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private currentUserSubject: BehaviorSubject<User | null>;
    public currentUser: Observable<User | null>;

    constructor(private http: HttpClient) {
        this.currentUserSubject = new BehaviorSubject<User | null>(
            JSON.parse(localStorage.getItem('currentUser') || 'null')
        );
        this.currentUser = this.currentUserSubject.asObservable();
    }

    public getCurrentUserValue(): User | null {
        return this.currentUserSubject.value;
    }

  private decodeToken(token: string) {
      try {
          const payload = token.split('.')[1];
          const decoded = JSON.parse(atob(payload));
          console.log('Decoded token:', decoded);
          return decoded;
      } catch (err) {
          console.error('Error decoding token:', err);
          return null;
      }
  }

login(loginData: LoginRequest): Observable<User> {
    return this.http.post<JwtAuthenticationResponse>(
        `${environment.apiUrl}/v1/auth/login`,
        loginData
    ).pipe(
        map(response => {
            console.log('Raw login response:', response);

            const token = `${response.tokenType} ${response.accessToken}`;
            localStorage.setItem('token', token);

            const decodedToken = this.decodeToken(response.accessToken);
            console.log('Decoded token:', decodedToken);

            const user: User = {
                id: decodedToken?.id || 0,
                username: loginData.username,
                role: {
                    id: decodedToken?.roleId,
                    name: decodedToken?.roles?.[0]
                }
            };

            console.log('Created user object:', user);
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
            return user;
        })
    );
}

    register(registerData: RegisterRequest): Observable<MessageResponse> {
        console.log('Register attempt with data:', registerData);

        return this.http.post<MessageResponse>(
            `${environment.apiUrl}/v1/auth/register`,
            registerData
        ).pipe(
            tap(response => console.log('Raw register response:', response)),
            catchError((error: HttpErrorResponse) => {
                console.error('Registration error details:', {
                    status: error.status,
                    statusText: error.statusText,
                    error: error.error,
                    message: error.message
                });
                return throwError(() => error);
            })
        );
    }

    logout(): void {
        localStorage.removeItem('token');
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
    }

    public getRole(): string | null {
      const token = localStorage.getItem('token') || '';
      return this.getRoleFromToken(token);
    }

    private getRoleFromToken(token: string): string | null {
        try {
            const decodedToken = JSON.parse(atob(token.split('.')[1]));
            return decodedToken.roles?.[0] || null;
        } catch (e) {
            console.warn('Error decoding token:', e);
            return null;
        }
    }
}
