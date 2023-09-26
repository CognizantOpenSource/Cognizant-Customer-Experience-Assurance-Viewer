import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarChartActiveComponent } from './bar-chart-active.component';

describe('BarChartActiveComponent', () => {
  let component: BarChartActiveComponent;
  let fixture: ComponentFixture<BarChartActiveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BarChartActiveComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarChartActiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
