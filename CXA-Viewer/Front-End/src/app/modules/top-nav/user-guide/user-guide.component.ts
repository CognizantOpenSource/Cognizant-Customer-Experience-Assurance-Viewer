import { Component, OnInit, Input, ChangeDetectionStrategy } from '@angular/core';
import { UnSubscribable } from '@components/unsub';

@Component({
  selector: 'user-guide',
  templateUrl: './user-guide.component.html',
  styleUrls: ['./user-guide.component.css'],
  changeDetection:ChangeDetectionStrategy.OnPush
})
export class UserGuideComponent extends UnSubscribable implements OnInit {

  @Input() user: any;
  @Input() links : any[];
  constructor() {
    super();
  }
  ngOnInit(): void {
    
  }
}
