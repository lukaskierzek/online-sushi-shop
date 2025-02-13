import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ItemService} from '../services/item.service';
import {GlobalService} from '../../services/global.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {IItemByCategory} from '../models/iitem-by-category';

@Component({
  selector: 'app-details-item',
  imports: [],
  templateUrl: './details-item.component.html',
  styleUrl: './details-item.component.css'
})
export class DetailsItemComponent implements OnInit, OnDestroy {
  public itemId: any;
  public itemById: IItemByCategory[] = [];

  private getItemByIdSubscription?: Subscription;

  constructor(private itemService: ItemService, private globalService: GlobalService, private route: ActivatedRoute) {
  }

  ngOnDestroy(): void {
    this.getItemByIdSubscription?.unsubscribe();
  }

  ngOnInit(): void {
    this.route.paramMap
      .subscribe((params: ParamMap) => {
        this.itemId = params.get('id');
        console.log("Item id: " + this.itemId);
        this.getItemById(this.itemId);
      })
  }

  private getItemById(itemId: any) {
    //TODO: add getItemById
  }
}
