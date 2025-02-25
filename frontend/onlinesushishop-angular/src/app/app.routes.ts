import {Routes} from '@angular/router';
import {ByCategoryItemComponent} from './features/by-category-item/by-category-item.component';
import {DetailsItemComponent} from './features/details-item/details-item.component';
import {EditItemComponent} from './features/edit-item/edit-item.component';
import {OnSaleItemsComponent} from './features/on-sale-items/on-sale-items.component';

export const routes: Routes = [
  {
    path: '',
    component: ByCategoryItemComponent,
  },
  {
    path: 'sushishop/item',
    component: ByCategoryItemComponent,
  },
  {
    path: 'sushishop/item/sale',
    component: OnSaleItemsComponent
  },
  {
    path: 'sushishop/item/:id',
    component: DetailsItemComponent,
  },
  {
    path: 'sushishop/item/:id/edit',
    component: EditItemComponent
  }
];
