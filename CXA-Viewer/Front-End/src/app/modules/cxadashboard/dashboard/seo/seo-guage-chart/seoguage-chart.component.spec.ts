import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SeoGuageChartComponent } from './seoguage-chart.component';

describe('GuageChartComponent', () => {
  let component: SeoGuageChartComponent;
  let fixture: ComponentFixture<SeoGuageChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SeoGuageChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeoGuageChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
