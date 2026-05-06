import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environment/environment';
import { Product } from './product.service';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private http = inject(HttpClient);

  search(query: string) {
    return this.http.get<Product[]>(`${environment.apiUrl}/search?q=${encodeURIComponent(query)}`);
  }
}
