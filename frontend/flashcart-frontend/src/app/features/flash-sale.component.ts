import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductListComponent } from '../features/product-list/product-list.component';
import { ProductService } from '../core/services/product.service';

@Component({
  selector: 'app-flash-sale',
  standalone: true,
  imports: [CommonModule, ProductListComponent],
  templateUrl: './flash-sale.component.html',
})
export class FlashSaleComponent implements OnInit {
  private productService = inject(ProductService);

  countdown = signal('00:00');
  activeTab = signal<'flash' | 'all'>('flash');

  ngOnInit(): void {
    this.startCountdown();
    this.productService.loadProducts(this.activeTab());
  }

  switchTab(tab: 'flash' | 'all') {
    this.activeTab.set(tab);
    this.productService.loadProducts(tab);
  }

  private startCountdown() {
    const saleEnd = new Date();
    saleEnd.setMinutes(saleEnd.getMinutes() + 10);
    setInterval(() => {
      const diff = saleEnd.getTime() - new Date().getTime();
      if (diff <= 0) { this.countdown.set('SALE ENDED'); return; }
      const m = Math.floor(diff / 1000 / 60 % 60);
      const s = Math.floor(diff / 1000 % 60);
      this.countdown.set(`${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`);
    }, 1000);
  }
}
