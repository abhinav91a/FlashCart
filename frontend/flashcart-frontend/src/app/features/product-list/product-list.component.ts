import { Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ProductService } from '../../core/services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './product-list.component.html',
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);

  readonly products = this.productService.products;
  readonly loading = this.productService.loading;
  readonly error = this.productService.error;

  ngOnInit(): void {
    this.productService.loadProducts();
  }
}
