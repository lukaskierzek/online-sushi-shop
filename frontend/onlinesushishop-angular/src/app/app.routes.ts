import {Routes} from '@angular/router';
import {NavbarComponent} from './layout/navbar/navbar.component';
import {AppComponent} from './app.component';
import {ByCategoryItemComponent} from './features/by-category-item/by-category-item.component';

export const routes: Routes = [
  // {
  //   path: '',
  //   component: AppComponent,
  // },
  {
    path: 'sushishop/item',
    component: ByCategoryItemComponent,
  }
];
