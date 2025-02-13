import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Subcategory} from '../enums/subcategory';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  constructor(private router: Router) {
  }

  public logGetMessage(getName: string, data: any): void {
    const separator: string = "=".repeat(30);
    console.log(separator);
    console.log(`${getName}:`);
    console.log(data);
    console.log(separator);
  }

  public routerToDefaultPage(): void {
    this.router.navigate(['/sushishop/item/'], {queryParams: {category: Subcategory.NEW_ITEM}});
    console.log(">>>Redirect to default page<<<")
  }
}
