import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '@services/auth/authentication.service';
import { Router } from '@angular/router';
import { UnSubscribable } from '@components/unsub';
import { UserConfigService } from '@services/user-config.service';
import { Theme } from '@model/types.model';

function cut(value: string, len: number) {
  return (value || 'user').substr(0, len);
}

@Component({
  selector: 'app-user-menu',
  templateUrl: './user-menu.component.html',
  styleUrls: ['./user-menu.component.css']
})
export class UserMenuComponent extends UnSubscribable implements OnInit {

  user: any;
  darkTheme = false;

  constructor(private authService: AuthenticationService, private router: Router, private config: UserConfigService) {
    super();
  }

  ngOnInit() {
    this.managed(this.authService.user$).subscribe(user => this.user = user);
    this.managed(this.config.userSettings$).subscribe((it: any) => this.darkTheme = (it.dashboard && it.dashboard.theme === Theme.dark));
  }

  toggleTheme() {
    this.config.setTheme(this.darkTheme ? Theme.default : Theme.dark);
  }
  isAdmin(user: any) {
    return user.account.roles.map(role => role.permissions)
      .flatMap(permissions => [...permissions]).find(permission => permission.id === 'leap.permission.admin');
  }
  getDisplayName(user: any) {
    return `${user.firstName}${user.lastName ? ' ' + user.lastName : ''}`;
  }
  getIcon(user: any): any {
    return { type: 'image', name: user.name, data: this.getImage(user), desc: user.email };
  }
  getImage(user: any) {
    return user.image || user.picture || user.photoUrl;
  }
  getImageText(user: any) {
    return `${user.lastName ? cut(user.firstName, 1) + cut(user.lastName, 1) : cut(user.firstName, 2)}`;
  }
  logout($event: MouseEvent) {
    this.authService.logout().subscribe(any => this.router.navigate(['/login']));
  }
}