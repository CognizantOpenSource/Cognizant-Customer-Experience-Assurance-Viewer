import { Component, OnInit } from '@angular/core';
import { LoaderService } from '@services/loader.service';
import { Subject,BehaviorSubject } from 'rxjs';

@Component({
  selector: 'leap-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.css']
})
export class LoaderComponent implements OnInit {
  isLoading: BehaviorSubject<boolean> = this.loaderService.isLoading;

  constructor(private loaderService: LoaderService) {
  }

  ngOnInit() {
  }

}
