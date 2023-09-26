import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '@services/auth/authentication.service';
import { UnSubscribable } from '@components/unsub';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})

export class UserProfileComponent extends UnSubscribable implements OnInit {
  user: any;

  constructor(
    private authService: AuthenticationService,
    private router: Router, private toastr: ToastrService) {
    super();
  }

  ngOnInit() {
    this.managed(this.authService.user$).subscribe(user => this.user = user);
  }
  public copyToken() {
    this.authService.generateAPIToken().subscribe(token => {
      const listener = (e: ClipboardEvent) => {
        e.clipboardData?.setData('text/plain', (token.id));
        e.preventDefault();
        this.toastr.success(`api-token copied!`);
      };
      document.addEventListener('copy', listener);
      document.execCommand('copy');
      document.removeEventListener('copy', listener);
    }, error => this.toastr.error('error generating api-token'));
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
}
function cut(value: string, len: number) {
  return (value || 'user').substr(0, len);
}
