import { ChangeDetectorRef, Component, Input, OnChanges, OnInit } from '@angular/core';
import { Color, ScaleType } from '@swimlane/ngx-charts';
@Component({
  selector: 'app-stacked-chart',
  templateUrl: './stacked-chart.component.html',
  styleUrls: ['./stacked-chart.component.scss']
})
export class StackedChartComponent implements OnChanges{

  @Input() seobarchartdata: any = [];
  pieChartData: any[] = [];
  view:[number,number];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = true;
  xAxisLabel = 'OS - Browser';
  showYAxisLabel = true;
  yAxisLabel = 'Time(ms)';

  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#41a05a', '#be0d0d', '#AAAAAA']
  };
  
  constructor(private changeDetectorRefs: ChangeDetectorRef) {
    if (innerWidth < 800) {
      const decreasedWidth = innerWidth /1.35;
    const decreasedHeight = 300 * (decreasedWidth / 500); 
    this.view = [decreasedWidth, 300];
    } else {
      this.view = [500, 250]; 
    }
  }
  ngOnChanges() {
    this.pieChartData = this.seobarchartdata;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }
  onSelect(event) {
    //console.log(event);
  }

}
