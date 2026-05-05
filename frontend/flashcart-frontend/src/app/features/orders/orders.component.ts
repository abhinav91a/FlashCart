import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgIf, NgForOf, NgClass } from '@angular/common';
import { environment } from '../../../environment/environment';

interface Order {
  id: number;
  productId: number;
  quantity: number;
  userId: string;
  status: string;
}

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [NgIf, NgForOf, NgClass],
  templateUrl: './orders.component.html'
})
export class OrdersComponent implements OnInit {
  private http = inject(HttpClient);

  orders = signal<Order[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.http.get<Order[]>(`${environment.apiUrl}/orders/my`).subscribe({
      next: (data) => {
        this.orders.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load orders.');
        this.loading.set(false);
      }
    });
  }
}
