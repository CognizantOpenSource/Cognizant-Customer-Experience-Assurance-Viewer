import { Component, Input, OnChanges, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { Color, ScaleType } from '@swimlane/ngx-charts';
@Component({
  selector: 'leap-horizondal-bar-chart',
  templateUrl: './horizondal-bar-chart.component.html',
  styleUrls: ['./horizondal-bar-chart.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HorizondalBarChartComponent implements OnChanges {
  @Input() multi: any[];
  barChartData: any[] = [];
  view: [number,number]

  // options
  showXAxis: boolean = true;
  showYAxis: boolean = true;
  gradient: boolean = false;
  showLegend: boolean = true;
  showXAxisLabel: boolean = true;
  showYAxisLabel: boolean = true;
  showLabelPosition:any
  xAxisLabel: string = 'Page load timings';

  colorScheme :Color = {
    name: 'myScheme',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#5AA454', '#C7B42C', 'lightgrey'],
  };

  constructor(private changeDetectorRefs: ChangeDetectorRef) {
    if(innerWidth<1500 && innerWidth>=500){
      const decreasedWidth = innerWidth /2.5;
      
      this.view = [decreasedWidth, 180];
      
      this.showLabelPosition='right'
    }
    else if (innerWidth < 500) {
      const decreasedWidth = innerWidth /1.5;
    const decreasedHeight = 300 * (decreasedWidth / 800); 
    this.view = [decreasedWidth, decreasedHeight];
    //console.log(this.view)
    this.showLabelPosition='bottom'
    
    } else {
      this.view = [450, 180]; 
      this.showLabelPosition='right'
    }
  }

  onSelect(event) {
    //console.log(event);
  }
  ngOnChanges() {
    this.barChartData = this.multi;
    this.changeDetectorRefs.detectChanges();
    this.changeDetectorRefs.markForCheck();
  }

}
