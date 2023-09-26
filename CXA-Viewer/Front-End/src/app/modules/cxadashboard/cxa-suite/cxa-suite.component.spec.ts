import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CxaSuiteComponent } from './cxa-suite.component';

describe('CxaSuiteComponent', () => {
  let component: CxaSuiteComponent;
  let fixture: ComponentFixture<CxaSuiteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CxaSuiteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CxaSuiteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
