import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';

export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  priceInCents: number;
  stock: number;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/products`;

  // State managed via Signals
  products = signal<Product[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  loadProducts(): void {
    this.loading.set(true);
    this.error.set(null);
    this.http.get<Product[]>(this.baseUrl).subscribe({
      next: (data) => {
        console.log('Products received:', data);
        try {
          this.products.set(data);
          this.loading.set(false);
        } catch (e) {
          console.error('Error setting products:', e);
          this.loading.set(false);
        }
      },
      error: (e) => {
        console.error('HTTP error:', e);
        this.error.set('Failed to load products. Please try again later.');
        this.loading.set(false);
      },
    });
  }
}
