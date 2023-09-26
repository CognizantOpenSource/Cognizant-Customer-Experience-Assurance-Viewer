import { Component, Inject, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Observable } from 'rxjs';
export interface slider {
  image: string,
  thumbImage: string,
  title: string
};

@Component({
  selector: 'leap-image-slider',
  templateUrl: './image-slider.component.html',
  styleUrls: ['./image-slider.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ImageSliderComponent implements OnInit {
  @Input() imageObject: any=[]

  constructor(
    public dialog: MatDialog,
    private _sanitizer: DomSanitizer) {
   
  }


  ngOnInit() {
  }
 
  onImageClick(data: slider) {
    this.dialog.open(ImageDialog, {
      data
    });
  }

}

@Component({
  selector: 'image-modal',
  templateUrl: './image-modal.html',
})
export class ImageDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: slider) { }
}