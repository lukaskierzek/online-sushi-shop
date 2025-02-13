import {Routes} from '@angular/router';
import {NavbarComponent} from './layout/navbar/navbar.component';
import {AppComponent} from './app.component';
import {ByCategoryItemComponent} from './features/by-category-item/by-category-item.component';
import {DetailsItemComponent} from './features/details-item/details-item.component';

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
    path: 'sushishop/item/:id',
    component: DetailsItemComponent,
  }
];
