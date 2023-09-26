import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CxaSuiteDialogComponent } from './cxa-suite-dialog.component';

describe('CxaSuiteDialogComponent', () => {
  let component: CxaSuiteDialogComponent;
  let fixture: ComponentFixture<CxaSuiteDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CxaSuiteDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CxaSuiteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
