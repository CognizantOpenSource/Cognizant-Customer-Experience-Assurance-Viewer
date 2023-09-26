import { Component, Input, OnChanges,OnInit,ChangeDetectionStrategy,ChangeDetectorRef, SimpleChanges } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { Color, ScaleType } from '@swimlane/ngx-charts';

@Component({
  selector: 'omni-stacked-bar-chart',
  templateUrl: './omni-stacked-bar-chart.component.html',
  styleUrls: ['./omni-stacked-bar-chart.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OmniStackedBarChartComponent implements OnChanges {
  @Input() multi: any[];
  omnichannelStacked:any[]=[];
  view: [number,number] ;

  // options
  showXAxis: boolean = true;
  showYAxis: boolean = true;
  gradient: boolean = false;
  showLegend: boolean = true;
  showLegendPosition:any = 'right'
  showLegendTitle:any=''
  showXAxisLabel: boolean = false;
  // xAxisLabel: string = 'Country';
  showYAxisLabel: boolean = true;
  // yAxisLabel: string = 'Population';
  //legendTitle: string = 'Years';
 
  yAxisLabel = 'Count of Errors';

  // colorScheme = {
  //   domain: ['#5AA454', '#C7B42C', '#5B8B8B']
  // };
  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#3895D3','#072F5F','#1261A0','#58CCED']
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
  ngOnChanges(changes: SimpleChanges) {
    this.omnichannelStacked=this.multi;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }

 onSelect(data): void {
  }

  onActivate(data): void {
  }

  onDeactivate(data): void {
  }

}
