import { ChangeDetectorRef, Component, Input, OnChanges, OnInit } from '@angular/core';
import { Color, ScaleType } from '@swimlane/ngx-charts';
@Component({
  selector: 'app-bar-chart-active',
  templateUrl: './bar-chart-active.component.html',
  styleUrls: ['./bar-chart-active.component.scss']
})
export class BarChartActiveComponent implements OnChanges {

  @Input() activebarchartdata: any = [];
  pieChartData: any[] = [];
  view: [number,number]

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = true;
  xAxisLabel = 'Regions';
  showYAxisLabel = true;
  yAxisLabel = 'Time (ms)';

  colorScheme: Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ["#1C6DD0", "#FC9918","#a8385d","#7aa3e5","#a27ea8"]
  };

  constructor(private changeDetectorRefs: ChangeDetectorRef) {
    if(innerWidth<1500 && innerWidth>=800){
      const decreasedWidth = innerWidth /2.8;
      
      this.view = [decreasedWidth, 250];
      
  
    }
    else if (innerWidth < 800) {
      const decreasedWidth = innerWidth /1.40;
    const decreasedHeight = 300 * (decreasedWidth / 330); 
    this.view = [decreasedWidth, 250];
    } else {
      this.view = [500, 250]; 
    }
  }
  ngOnChanges() {
    this.pieChartData = this.activebarchartdata;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }
  onSelect(event) {
    //console.log(event);
  }

}
