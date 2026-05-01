import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductListComponent } from '../features/product-list/product-list.component';

@Component({
  selector: 'app-flash-sale',
  standalone: true,
  imports: [CommonModule, ProductListComponent],
  templateUrl: './flash-sale.component.html',
})
export class FlashSaleComponent implements OnInit {

  countdown = signal('00:00:00');

  ngOnInit(): void {
    this.startCountdown();
  }

  private startCountdown() {
    const saleEnd = new Date();
    saleEnd.setMinutes(saleEnd.getMinutes() + 10); // 10 min sale

    setInterval(() => {
      const now = new Date().getTime();
      const diff = saleEnd.getTime() - now;

      if (diff <= 0) {
        this.countdown.set('SALE ENDED');
        return;
      }

      const minutes = Math.floor((diff / 1000 / 60) % 60);
      const seconds = Math.floor((diff / 1000) % 60);

      this.countdown.set(
        `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
      );
    }, 1000);
  }
}
