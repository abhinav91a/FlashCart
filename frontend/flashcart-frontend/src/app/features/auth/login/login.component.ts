import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  email = signal('');
  password = signal('');
  isRegister = signal(false);
  error = signal<string | null>(null);
  loading = signal(false);

  submit() {
    this.loading.set(true);
    this.error.set(null);

    const req = { email: this.email(), password: this.password() };
    const action = this.isRegister() ? this.auth.register(req) : this.auth.login(req);

    action.subscribe({
      next: (user) => {
        this.auth.saveUser(user);
        this.router.navigate(['/']);
      },
      error: () => {
        this.error.set(this.isRegister() ? 'Registration failed.' : 'Invalid email or password.');
        this.loading.set(false);
      }
    });
  }
}
