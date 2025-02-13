import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {IItemByCategory} from '../models/iitem-by-category';
import {IItemById} from '../models/iitem-by-id';

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  constructor(private http: HttpClient) {
  }

  getItemByCategory(category: string): Observable<IItemByCategory> {
    return this.http.get<IItemByCategory>(`http://localhost:8080/api/v1/onlinesushishop/item/non-hidden/by-category?category=${category}`);
  }

  getItemById(itemId: any): Observable<IItemById> {
    return this.http.get<IItemById>(`http://localhost:8080/api/v1/onlinesushishop/item/non-hidden/${itemId}`);
  }
}
