import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveLineChartComponent } from './active-line-chart.component';

describe('ActiveLineChartComponent', () => {
  let component: ActiveLineChartComponent;
  let fixture: ComponentFixture<ActiveLineChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActiveLineChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActiveLineChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
