import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {IItemByCategory} from '../models/iitem-by-category';

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  constructor(private http: HttpClient) { }

  getItemByCategory(category: string): Observable<IItemByCategory> {
    return this.http.get<IItemByCategory>(`http://localhost:8080/api/v1/onlinesushishop/item/non-hidden/by-category?category=${category}`);
  }
}
