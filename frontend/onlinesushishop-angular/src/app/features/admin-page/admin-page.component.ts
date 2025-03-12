import {Component, OnDestroy, OnInit} from '@angular/core';
import {GlobalService} from '../../services/global.service';
import {Subscription} from 'rxjs';
import {ItemService} from '../services/item.service';
import {Subcategory} from '../../enums/subcategory';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-admin-page',
  imports: [
    RouterLink
  ],
  templateUrl: './admin-page.component.html',
  styleUrl: './admin-page.component.css'
})
export class AdminPageComponent implements OnInit, OnDestroy {
  public allItem: any = [];

  private getAllItemSubscription?: Subscription;

  protected readonly Subcategory = Subcategory;

  constructor(private globalService: GlobalService,
              private itemService: ItemService) {
  }

  ngOnDestroy(): void {
    this.getAllItemSubscription?.unsubscribe();
  }

  ngOnInit(): void {
    this.getAllItem();
  }

  private getAllItem() {
    this.getAllItemSubscription = this.itemService.getAllItem()
      .subscribe({
        next: (data: any) => {
          this.allItem = data.sort(this.sortItems);
          this.globalService.logGetMessage(`Data from get item`, this.allItem);
        },
        error: err => {
          console.error(err);
        }
      });
  }

  sortItems(a: any, b: any): number {
    if (a.itemIsHidden === b.itemIsHidden) {
      return a.itemId - b.itemId;
    } else {
      return a.itemIsHidden - b.itemIsHidden;
    }
  }
}
