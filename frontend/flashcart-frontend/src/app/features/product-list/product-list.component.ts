import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { DecimalPipe, NgClass, NgIf, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { OrderService } from '../../core/services/order.service';
import { NotificationService } from '../../core/services/notification.service';
import { SearchService } from '../../core/services/search.service';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [DecimalPipe, NgClass, NgIf, NgForOf, FormsModule],
  templateUrl: './product-list.component.html',
})
export class ProductListComponent implements OnInit, OnDestroy {
  private productService = inject(ProductService);
  private orderService = inject(OrderService);
  private notificationService = inject(NotificationService);
  private searchService = inject(SearchService);

  private searchInput$ = new Subject<string>();

  quantities = signal<Record<number, number>>({});
  readonly products = this.productService.products;
  readonly loading = this.productService.loading;
  readonly error = this.productService.error;
  readonly notifications = this.notificationService.notifications;
  readonly connected = this.notificationService.connected;

  searchQuery = signal('');
  isSearching = signal(false);

  ordering = signal<number | null>(null);
  orderError = signal<string | null>(null);
  orderSuccess = signal<string | null>(null);

  ngOnInit(): void {
    this.productService.loadProducts();
    this.notificationService.connect();

    this.notificationService.stockUpdate$.subscribe(() => {
      if (!this.searchQuery()) this.productService.loadProducts();
    });

    // Debounced search
    this.searchInput$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => {
        if (!q.trim()) {
          this.isSearching.set(false);
          this.productService.loadProducts();
          return [];
        }
        this.isSearching.set(true);
        return this.searchService.search(q);
      })
    ).subscribe({
      next: (results) => this.productService.products.set(results),
      error: () => this.productService.products.set([])
    });

    document.documentElement.classList.add('dark');
  }

  ngOnDestroy(): void {
    this.notificationService.disconnect();
  }

  onSearch(query: string): void {
    this.searchQuery.set(query);
    this.searchInput$.next(query);
  }

  clearSearch(): void {
    this.searchQuery.set('');
    this.searchInput$.next('');
    this.isSearching.set(false);
  }

  getStockWidth(stock: number): number {
    return Math.min((stock / 100) * 100, 100);
  }

  getQty(productId: number): number {
    return this.quantities()[productId] ?? 1;
  }

  incrementQty(productId: number, maxStock: number) {
    const current = this.getQty(productId);
    if (current < maxStock) {
      this.quantities.update(q => ({ ...q, [productId]: current + 1 }));
    }
  }

  decrementQty(productId: number) {
    const current = this.getQty(productId);
    if (current > 1) {
      this.quantities.update(q => ({ ...q, [productId]: current - 1 }));
    }
  }

  buyNow(productId: number) {
    const quantity = this.getQty(productId);
    this.ordering.set(productId);
    this.orderError.set(null);
    this.orderSuccess.set(null);
    this.orderService.placeOrder({ productId, quantity }).subscribe({
      next: (order) => {
        this.orderSuccess.set(`Order #${order.id} placed successfully!`);
        this.ordering.set(null);
        this.quantities.update(q => ({ ...q, [productId]: 1 }));
        setTimeout(() => this.productService.loadProducts(), 1500);
      },
      error: () => {
        this.orderError.set('Failed to place order. Try again.');
        this.ordering.set(null);
      }
    });
  }
}
