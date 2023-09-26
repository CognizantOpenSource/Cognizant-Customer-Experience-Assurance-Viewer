import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProjectNameService {
  itemValue = new BehaviorSubject(this.projectName);

  constructor() { }

  set projectName(value: any) {
    //console.log(" Project Name" + value);
    this.itemValue.next(value);
    localStorage.setItem('projectName', value);
  }

  get projectName() {
    return localStorage.getItem('projectName');
  }
}
