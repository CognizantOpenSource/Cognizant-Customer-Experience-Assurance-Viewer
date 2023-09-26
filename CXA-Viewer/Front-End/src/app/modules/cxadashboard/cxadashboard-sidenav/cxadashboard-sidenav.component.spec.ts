import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CxadashboardSidenavComponent } from './cxadashboard-sidenav.component';

describe('CxadashboardSidenavComponent', () => {
  let component: CxadashboardSidenavComponent;
  let fixture: ComponentFixture<CxadashboardSidenavComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CxadashboardSidenavComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CxadashboardSidenavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
