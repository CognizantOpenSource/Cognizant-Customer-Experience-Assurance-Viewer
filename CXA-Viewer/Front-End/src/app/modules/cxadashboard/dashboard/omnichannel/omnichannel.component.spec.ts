import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OmnichannelComponent } from './omnichannel.component';

describe('OmnichannelComponent', () => {
  let component:OmnichannelComponent;
  let fixture: ComponentFixture<OmnichannelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OmnichannelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OmnichannelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
