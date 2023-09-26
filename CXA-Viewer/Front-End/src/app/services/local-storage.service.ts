import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class LocalStorage {

  constructor() { }

  getItem(itemName: string): string | null {
    return localStorage.getItem(itemName);
  }

  setItem(itemName: string, item: string): void {
    return localStorage.setItem(itemName, item);
  }

  deleteItem(itemName: string): void {
    return localStorage.removeItem(itemName);
  }

  clearAll(): void {
    return localStorage.clear();
  }

  removeItem(index: string): Observable<any> {
    return of(localStorage.removeItem(index));
  }

  hasKey(itemName: string): boolean {
    return localStorage.getItem(itemName) === null;
  }

  allKeys(): any[] {
    return Object.entries(localStorage);
  }
}
