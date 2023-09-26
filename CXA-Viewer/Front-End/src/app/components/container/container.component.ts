import { Component, OnInit } from '@angular/core';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-container',
  templateUrl: './container.component.html',
  styleUrls: ['./container.component.css'],
  animations: [
    trigger(
      'enterAnimation', [
        transition(':enter', [
          style({  opacity: 0.99 }),
          animate('300ms', style({ opacity: 1 }))
        ])
      ]
    )
  ]
})
export class ContainerComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
