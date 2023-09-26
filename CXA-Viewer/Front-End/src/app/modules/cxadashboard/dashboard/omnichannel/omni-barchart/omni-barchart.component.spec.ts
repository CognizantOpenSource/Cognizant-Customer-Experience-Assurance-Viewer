import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OmniBarchartComponent } from './omni-barchart.component';

describe('OmniBarchartComponent', () => {
  let component: OmniBarchartComponent;
  let fixture: ComponentFixture<OmniBarchartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OmniBarchartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OmniBarchartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
