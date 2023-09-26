import { Component, Input, OnChanges,OnInit,ChangeDetectionStrategy,ChangeDetectorRef, SimpleChanges } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { Color, ScaleType } from '@swimlane/ngx-charts';

@Component({
  selector: 'leap-barchart',
  templateUrl: './barchart.component.html',
  styleUrls: ['./barchart.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BarchartComponent implements OnChanges {
  @Input() multi: any[];
  barChartData:any[]=[];
  view: [number, number]

  // options
  showXAxis: boolean = true;
  showYAxis: boolean = true;
  gradient: boolean = false;
  showLegend: boolean = true;
  showLegendPosition:any = 'below'
  showLegendTitle:any=''
  showXAxisLabel: boolean = false;
  // xAxisLabel: string = 'Country';
  showYAxisLabel: boolean = false;
  // yAxisLabel: string = 'Population';
  //legendTitle: string = 'Years';

  // colorScheme = {
  //   domain: ['#5AA454', '#C7B42C', '#5B8B8B']
  // };
  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5aa454', '#a30000', '#c7b42c','#767a7e']
  };


  constructor(private changeDetectorRefs: ChangeDetectorRef) {
    if(innerWidth<1500 && innerWidth>=800){
      const decreasedWidth = innerWidth /2.8;
      
      this.view = [decreasedWidth, 250];
      
  
    }
    else if (innerWidth < 800) {
      const decreasedWidth = innerWidth /1.35;
    const decreasedHeight = 300 * (decreasedWidth / 500); 
    this.view = [decreasedWidth, decreasedHeight];
    } else {
      this.view = [480, 250]; 
    }
  
  }
  ngOnChanges(changes: SimpleChanges) {
    this.barChartData=this.multi;
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
