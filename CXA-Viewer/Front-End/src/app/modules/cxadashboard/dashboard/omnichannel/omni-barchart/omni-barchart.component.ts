import { ChangeDetectorRef, Component, Input, OnChanges, OnInit } from '@angular/core';
import { Color, ScaleType } from '@swimlane/ngx-charts';
@Component({
  selector: 'app-omni-barchart',
  templateUrl: './omni-barchart.component.html',
  styleUrls: ['./omni-barchart.component.scss']
})
export class OmniBarchartComponent implements OnChanges{

  @Input() uiuxpiedata: any = [];
  pieChartData: any[] = [];
  view: [number,number];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = false;
  showXAxisLabel = true;
  xAxisLabel = 'OS - Browser';
  showYAxisLabel = true;
  yAxisLabel = 'Time (ms)';

  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#072F5F','#1261A0','#3895D3','#58CCED']//['#d2222d', 'orange', '#ffdf00','#CA8606']
  };
  
  constructor(private changeDetectorRefs: ChangeDetectorRef) {
    if(innerWidth<1500 && innerWidth>=800){
      const decreasedWidth = innerWidth /2.8;
      
      this.view = [decreasedWidth, 300];
      
  
    }
    else if (innerWidth < 500) {
      const decreasedWidth = innerWidth /1.35;
    const decreasedHeight = 300 * (decreasedWidth / 500); 
    this.view = [decreasedWidth, 300];
    } else {
      this.view = [500, 300]; 
    }
  }
  ngOnChanges() {
    this.pieChartData = this.uiuxpiedata;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }
  onSelect(event) {
    //console.log(event);
  }
}
