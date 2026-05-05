import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { DecimalPipe, NgClass, NgIf, NgForOf } from '@angular/common';
import { ProductService } from '../../core/services/product.service';
import { OrderService } from '../../core/services/order.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [DecimalPipe, NgClass, NgIf, NgForOf],
  templateUrl: './product-list.component.html',
})
export class ProductListComponent implements OnInit, OnDestroy {
  private productService = inject(ProductService);
  private orderService = inject(OrderService);
  private notificationService = inject(NotificationService);
  quantities = signal<Record<number, number>>({});

  readonly products = this.productService.products;
  readonly loading = this.productService.loading;
  readonly error = this.productService.error;
  readonly notifications = this.notificationService.notifications;
  readonly connected = this.notificationService.connected;

  theme = signal<'dark' | 'light'>('dark');
  ordering = signal<number | null>(null);
  orderError = signal<string | null>(null);
  orderSuccess = signal<string | null>(null);

  ngOnInit(): void {
    this.productService.loadProducts();
    this.notificationService.connect();

    // Refresh products when stock update notification arrives
    this.notificationService.stockUpdate$.subscribe(() => {
      this.productService.loadProducts();
    });
    document.documentElement.classList.add('dark');
  }

  ngOnDestroy(): void {
    this.notificationService.disconnect();
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
