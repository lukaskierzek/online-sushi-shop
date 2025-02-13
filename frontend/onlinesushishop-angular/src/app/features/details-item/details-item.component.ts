import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ItemService} from '../services/item.service';
import {GlobalService} from '../../services/global.service';
import {ActivatedRoute, ParamMap, RouterLink} from '@angular/router';

@Component({
  selector: 'app-details-item',
  imports: [
    RouterLink
  ],
  templateUrl: './details-item.component.html',
  styleUrl: './details-item.component.css'
})
export class DetailsItemComponent implements OnInit, OnDestroy {
  public itemId: any;
  public itemById: any = {};

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
        this.globalService.logGetMessage("Item id", this.itemId);
        this.getItemById(this.itemId);
      })
  }

  private getItemById(itemId: any) {
    if (itemId === undefined) {
      this.globalService.routerToDefaultPage();
    } else {
      this.getItemByIdSubscription = this.itemService.getItemById(itemId)
        .subscribe({
          next: (data: any) => {
            this.itemById = data;
            this.globalService.logGetMessage(`Data from get item by Id '${this.itemId}'`, this.itemById);

            if (Object.keys(this.itemById).length === 0) {
              this.globalService.routerToDefaultPage();
            }
          },
          error: (err: any) => {
            console.error(err);
            this.globalService.routerToDefaultPage();
          }
        });
    }


  }
}
