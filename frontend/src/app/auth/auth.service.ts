import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface AuthUser {
  token: string;
  userId: number;
  role: 'EMPLOYEE' | 'MANAGER' | 'SECRETARY';
  firstName: string;
  lastName: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  private apiUrl = import.meta.env['NG_APP_API_URL'];

  currentUser = signal<AuthUser | null>(this.loadUser());

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.apiUrl}/auth/login`, { email, password }).pipe(
      tap(user => {
        if (typeof localStorage !== 'undefined') {
          localStorage.setItem(this.TOKEN_KEY, user.token);
          localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        }
        this.currentUser.set(user);
      })
    );
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  getRole(): string | null {
    return this.currentUser()?.role ?? null;
  }

  private loadUser(): AuthUser | null {
    if (typeof localStorage === 'undefined') return null;
    const stored = localStorage.getItem(this.USER_KEY);
    return stored ? JSON.parse(stored) : null;
  }
}
