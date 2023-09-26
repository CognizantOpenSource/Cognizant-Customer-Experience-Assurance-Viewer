import { Component, OnInit, Input, ChangeDetectionStrategy } from '@angular/core';

type IconType = 'image' | 'clr-icon' | 'fa-icon';
interface Icon {
  type: IconType;
  data: string;
  name?: string;
  desc?: string;
}
interface IconConfig extends Icon {
  icons?: Array<Icon>;
}
const getTitle = (icon: Icon) => icon.desc || icon.name;

@Component({
  selector: 'app-icon',
  templateUrl: './icon.component.html',
  styleUrls: ['./icon.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class IconComponent implements OnInit {

  @Input() icon: IconConfig;
  constructor() { }

  @Input() round = false;
  @Input() size = '2rem';
  get icon1(): Icon|undefined {
    return this.icon && this.icon.icons && this.icon.icons[0];
  }
  get icon2(): Icon|undefined {
    return this.icon && this.icon.icons && this.icon.icons[1];
  }
  ngOnInit() {
  }
  isImage(icon: Icon): boolean {
    return 'image' === icon.type;
  }
  isClrIcon(icon: Icon): boolean {
    return 'clr-icon' === icon.type;
  }
  isFaIcon(icon: Icon): boolean {
    return 'fa-icon' === icon.type;
  }
  getTitle(icon: IconConfig): string|undefined {
    if (icon.icons) {
      return `${getTitle(icon)} ${getTitle(icon.icons[0])} ${getTitle(icon.icons[1])}`;
    }
    return getTitle(icon);
  }
}
