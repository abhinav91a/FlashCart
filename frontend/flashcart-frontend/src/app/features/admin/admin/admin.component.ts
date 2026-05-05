import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { NgIf, NgForOf } from '@angular/common';
import { environment } from '../../../../environment/environment';

interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  priceInCents: number;
  stock: number;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [FormsModule, NgIf, NgForOf],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {
  private http = inject(HttpClient);

  products = signal<Product[]>([]);
  loading = signal(true);
  success = signal<string | null>(null);
  error = signal<string | null>(null);

  newProduct = {
    sku: '', name: '', description: '',
    priceInCents: 0, stock: 0
  };

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.http.get<Product[]>(`${environment.apiUrl}/products`).subscribe({
      next: (data) => {
        this.products.set(data);
        this.loading.set(false);
      }
    });
  }

  createProduct() {
    this.http.post<Product>(`${environment.apiUrl}/products`, this.newProduct).subscribe({
      next: () => {
        this.success.set('Product created successfully!');
        this.newProduct = { sku: '', name: '', description: '', priceInCents: 0, stock: 0 };
        this.loadProducts();
        setTimeout(() => this.success.set(null), 3000);
      },
      error: () => this.error.set('Failed to create product.')
    });
  }

  deleteProduct(id: number) {
    if (!confirm('Delete this product?')) return;
    this.http.delete(`${environment.apiUrl}/products/${id}`).subscribe({
      next: () => {
        this.success.set('Product deleted.');
        this.loadProducts();
        setTimeout(() => this.success.set(null), 3000);
      }
    });
  }

  seedInventory(productId: number, stock: number) {
    this.http.post(`${environment.apiUrl}/inventory`, { productId, stock }).subscribe({
      next: () => this.success.set('Inventory seeded!')
    });
  }
}
