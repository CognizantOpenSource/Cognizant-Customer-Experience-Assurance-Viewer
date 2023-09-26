import { Component, OnInit, Input, ChangeDetectionStrategy } from '@angular/core';
import { UnSubscribable } from '@components/unsub';

@Component({
  selector: 'leap-app-links',
  templateUrl: './app-links.component.html',
  styleUrls: ['./app-links.component.css'],
  changeDetection:ChangeDetectionStrategy.OnPush
})
export class AppLinksComponent extends UnSubscribable implements OnInit {

  @Input() user: any;
  @Input() links : any[];
  constructor() {
    super();
  }
  ngOnInit(): void {
    
  }
}
