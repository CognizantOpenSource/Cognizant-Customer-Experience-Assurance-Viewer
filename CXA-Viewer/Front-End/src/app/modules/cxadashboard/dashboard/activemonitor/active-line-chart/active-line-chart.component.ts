import { ChangeDetectorRef, Component, Input, OnChanges } from '@angular/core';
import { Color, ScaleType } from '@swimlane/ngx-charts';
@Component({
  selector: 'app-active-line-chart',
  templateUrl: './active-line-chart.component.html',
  styleUrls: ['./active-line-chart.component.scss']
})
export class ActiveLineChartComponent implements OnChanges {
  @Input() activelinechartdata: any = [];
  lineChartData: any[] = [];
  showLegend: boolean = true;
  showLegendPosition:any = 'right'
  showLegendTitle:any=''
  xAxisLabel = 'Minutes';
  yAxisLabel = 'Time (ms)';
  view:[number,number]
  colorScheme: Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ["#1C6DD0", "#FC9918","#a8385d","#7aa3e5","#a27ea8"]
  };
  constructor(private changeDetectorRefs: ChangeDetectorRef) {
    if(innerWidth<1500 && innerWidth>=800){
      const decreasedWidth = innerWidth /2.8;
      
      this.view = [decreasedWidth, 270];
      
  
    }
    else if (innerWidth < 800) {
      const decreasedWidth = innerWidth /1.25;
      ; 
      this.view = [decreasedWidth, 270];
    } else {
      this.view = [550, 270]; 
    }
  }

  ngOnChanges() {
    this.lineChartData = this.activelinechartdata;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }

}
