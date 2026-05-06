import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';

export interface CreateOrderRequest {
  productId: number;
  quantity: number;
  userId: string;
}

export interface OrderResponse {
  id: number;
  productId: number;
  quantity: number;
  userId: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/orders`;

  placeOrder(req: { productId: number; quantity: number }) {
    return this.http.post<OrderResponse>(this.baseUrl, req);
  }
}
