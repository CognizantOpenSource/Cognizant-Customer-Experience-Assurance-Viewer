import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HorizondalBarChartComponent } from './horizondal-bar-chart.component';

describe('HorizondalBarChartComponent', () => {
  let component: HorizondalBarChartComponent;
  let fixture: ComponentFixture<HorizondalBarChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HorizondalBarChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HorizondalBarChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
