import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
title = 'flashcart-frontend';
auth = inject(AuthService);

theme = signal<'dark' | 'light'>('dark');

  ngOnInit(): void {
    // Check local storage or system preference on load
    const savedTheme = localStorage.getItem('theme') as 'dark' | 'light' | null;
    const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

    const initialTheme = savedTheme || (systemDark ? 'dark' : 'light');
    this.applyTheme(initialTheme);
  }

  toggleTheme() {
    const next = this.theme() === 'dark' ? 'light' : 'dark';
    this.applyTheme(next);
  }

  private applyTheme(newTheme: 'dark' | 'light') {
    this.theme.set(newTheme);
    localStorage.setItem('theme', newTheme);

    if (newTheme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }
}
