import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {IMainCategories} from '../models/interface-main-categories';

@Injectable({
  providedIn: 'root'
})
export class NavbarService {

  constructor(private http: HttpClient) { }

  getMainCategories(): Observable<IMainCategories> {
    return this.http.get<IMainCategories>('http://localhost:8080/api/v1/onlinesushishop/main-category/non-hidden');
  }
}
