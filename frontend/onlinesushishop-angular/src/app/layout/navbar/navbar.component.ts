import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {Subscription} from 'rxjs';
import {NavbarService} from '../services/navbar.service';
import {GlobalService} from '../../services/global.service';
import {IMainCategories} from '../models/interface-main-categories';
import {Subcategory} from '../../enums/subcategory';

@Component({
  selector: 'app-navbar',
  imports: [
    RouterLink
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit, OnDestroy {
  public mainCategories: IMainCategories[] = [];

  private getMainCategoriesSubscription?: Subscription;

  constructor(private navbarService: NavbarService, private globalService: GlobalService, private router: Router ) {}

  ngOnDestroy(): void {
    if (this.getMainCategoriesSubscription) {
      this.getMainCategoriesSubscription.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.getMainCategories();
  }

  private getMainCategories() {
    this.getMainCategoriesSubscription = this.navbarService.getMainCategories()
      .subscribe({
        next: (data: any) => {
          this.mainCategories = data;
          this.globalService.logGetMessage("Main categories", data);

          // Navigate to default page
          this.router.navigate(['/sushishop/item/'], { queryParams: { category: this.Subcategory.NEW_ITEM } });
        },
        error: (err) => {
          console.log("Error fetching main categories: ", err);
        }
      })
  }

  protected readonly Subcategory = Subcategory;
}
