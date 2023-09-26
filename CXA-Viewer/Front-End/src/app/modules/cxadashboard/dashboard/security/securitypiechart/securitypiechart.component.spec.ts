import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecuritypiechartComponent } from './securitypiechart.component';

describe('SecuritypiechartComponent', () => {
  let component: SecuritypiechartComponent;
  let fixture: ComponentFixture<SecuritypiechartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SecuritypiechartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecuritypiechartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
