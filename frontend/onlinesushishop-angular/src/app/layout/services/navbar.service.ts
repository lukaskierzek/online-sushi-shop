import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NavbarService {

  constructor(private http: HttpClient) { }

  getMainCategories(): Observable<any> {
    return this.http.get<any>('http://localhost:8080/api/v1/onlinesushishop/main-category/non-hidden');
  }
}
