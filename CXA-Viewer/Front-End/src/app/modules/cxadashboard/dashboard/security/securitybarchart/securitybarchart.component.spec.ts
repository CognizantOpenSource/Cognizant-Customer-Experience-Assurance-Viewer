import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SecuritybarchartComponent } from './securitybarchart.component';

describe('SecuritybarchartComponent', () => {
  let component: SecuritybarchartComponent;
  let fixture: ComponentFixture<SecuritybarchartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SecuritybarchartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SecuritybarchartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
