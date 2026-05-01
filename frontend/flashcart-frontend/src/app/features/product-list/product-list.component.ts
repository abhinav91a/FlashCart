import { Component, OnInit, inject, signal } from '@angular/core';
import { DecimalPipe, NgClass, NgForOf, NgIf } from '@angular/common';
import { ProductService } from '../../core/services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [DecimalPipe, NgClass, NgIf, NgForOf],
  templateUrl: './product-list.component.html',
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  theme = signal<'dark' | 'light'>('dark');

  readonly products = this.productService.products;
  readonly loading = this.productService.loading;
  readonly error = this.productService.error;

  ngOnInit(): void {
    this.productService.loadProducts();
    document.documentElement.classList.add('dark');
  }

  toggleTheme() {
    const next = this.theme() === 'dark' ? 'light' : 'dark';
    this.theme.set(next);
    document.documentElement.classList.toggle('dark', next === 'dark');
  }

  getStockWidth(stock: number): number {
    return Math.min((stock / 100) * 100, 100);
  }
}
