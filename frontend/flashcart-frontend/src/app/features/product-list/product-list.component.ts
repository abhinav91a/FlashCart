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

  toggleTheme() {
    const next = this.theme() === 'dark' ? 'light' : 'dark';
    this.theme.set(next);
    document.documentElement.classList.toggle('dark', next === 'dark');
  }

  buyNow(productId: number) {
    this.ordering.set(productId);
    this.orderError.set(null);
    this.orderSuccess.set(null);

    this.orderService.placeOrder({
      productId,
      quantity: 1,
      userId: 'user-001'
    }).subscribe({
      next: (order) => {
        this.orderSuccess.set(`Order #${order.id} placed successfully!`);
        this.ordering.set(null);
        this.productService.loadProducts();
      },
      error: () => {
        this.orderError.set('Failed to place order. Try again.');
        this.ordering.set(null);
      }
    });
  }

  getStockWidth(stock: number): number {
    return Math.min((stock / 100) * 100, 100);
  }
}
