import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CxadashboardComponent } from './cxadashboard.component';

describe('CxadashboardComponent', () => {
  let component: CxadashboardComponent;
  let fixture: ComponentFixture<CxadashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CxadashboardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CxadashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
