import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivemonitorComponent } from './activemonitor.component';

describe('ActivemonitorComponent', () => {
  let component:ActivemonitorComponent;
  let fixture: ComponentFixture<ActivemonitorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActivemonitorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivemonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
