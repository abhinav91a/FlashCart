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
  imageUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/products`;
  private readonly flashUrl = `${environment.apiUrl}/products/flash`;

  // State managed via Signals
  products = signal<Product[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  loadProducts(): void {
  this.loading.set(true);
  this.error.set(null);
  this.http.get<Product[]>(this.flashUrl).subscribe({
    next: (data) => {
      this.products.set(data);
      this.loading.set(false);
    },
    error: () => {
      this.error.set('Failed to load products.');
      this.loading.set(false);
    }
  });
  }
}
