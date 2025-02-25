import {Component, OnDestroy, OnInit} from '@angular/core';
import {IItemByCategory} from '../models/iitem-by-category';
import {Subscription} from 'rxjs';
import {GlobalService} from '../../services/global.service';
import {ItemService} from '../services/item.service';
import {RouterLink} from '@angular/router';
import {Subcategory} from '../../enums/subcategory';

@Component({
  selector: 'app-on-sale-items',
  imports: [
    RouterLink
  ],
  templateUrl: './on-sale-items.component.html',
  styleUrl: './on-sale-items.component.css'
})
export class OnSaleItemsComponent implements OnInit, OnDestroy {
  public itemOnSale: IItemByCategory[] = []

  private getItemsOnSaleSubscription?: Subscription;

  constructor(private globalService: GlobalService,
              private itemService: ItemService) {
  }

  ngOnDestroy(): void {
    this.getItemsOnSaleSubscription?.unsubscribe();
  }

  ngOnInit(): void {
    this.getItemsOnSale();
  }

  private getItemsOnSale() {
    this.getItemsOnSaleSubscription = this.itemService.getItemsOnSaleService()
      .subscribe({
        next: (data: any) => {
          this.itemOnSale = data;
          this.globalService.logGetMessage("Items on sale", data);
        },
        error: (err: any) => {
          console.error(`Error fetching items on sale: ${err}`);
        }
      })
  }

  protected readonly Subcategory = Subcategory;
}
