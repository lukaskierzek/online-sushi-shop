import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  constructor() {
  }

  public logGetMessage(getName: string, data: any): void {
    const separator: string = "=".repeat(30);
    console.log(separator);
    console.log(`${getName}:`);
    console.log(data);
    console.log(separator);
  }
}
