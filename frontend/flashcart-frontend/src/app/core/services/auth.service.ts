import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environment/environment';

export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  currentUser = signal<AuthResponse | null>(this.loadFromStorage());
  isLoggedIn = signal(!!this.loadFromStorage());
  isAdmin = signal(this.loadFromStorage()?.role === 'ADMIN');

  private loadFromStorage(): AuthResponse | null {
    const stored = localStorage.getItem('flashcart_user');
    return stored ? JSON.parse(stored) : null;
  }

  getToken(): string | null {
    return this.currentUser()?.token ?? null;
  }

  login(req: AuthRequest) {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, req);
  }

  register(req: AuthRequest) {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, req);
  }

  saveUser(user: AuthResponse) {
    localStorage.setItem('flashcart_user', JSON.stringify(user));
    this.currentUser.set(user);
    this.isLoggedIn.set(true);
    this.isAdmin.set(user.role === 'ADMIN');
  }

  logout() {
    localStorage.removeItem('flashcart_user');
    this.currentUser.set(null);
    this.isLoggedIn.set(false);
    this.isAdmin.set(false);
    this.router.navigate(['/login']);
  }
}
