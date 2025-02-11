import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {GlobalService} from '../../services/global.service';
import {IItemByCategory} from '../models/iitem-by-category';
import {Subscription} from 'rxjs';
import {ItemService} from '../services/item.service';

@Component({
  selector: 'app-by-category-item',
  imports: [
    RouterLink
  ],
  templateUrl: './by-category-item.component.html',
  styleUrl: './by-category-item.component.css'
})
export class ByCategoryItemComponent implements OnInit, OnDestroy {
  public category: any;
  public itemByCategory: IItemByCategory[] = [];

  private getItemByCategorySubscription?: Subscription;

  constructor(private route: ActivatedRoute, private globalService: GlobalService, private itemService: ItemService) {}

  ngOnDestroy(): void {
        this.getItemByCategorySubscription?.unsubscribe();
    }

  ngOnInit(): void {
        this.route.queryParams
          .subscribe((params) => {
          this.category = params['category'];
          this.globalService.logGetMessage("Category", this.category);
          this.getItemByCategory(this.category);
        })
    }

  private getItemByCategory(category: any): void {
    if (category === undefined) {
    } else {
      this.getItemByCategorySubscription = this.itemService.getItemByCategory(category)
        .subscribe({
          next: (data: any) => {
            this.itemByCategory = data;
            this.globalService.logGetMessage(`Data from get item by category '${this.category}'`, this.itemByCategory);
          },
          error: (err: any) => {
            console.log(err);
          }
        })
    }
  }
}
