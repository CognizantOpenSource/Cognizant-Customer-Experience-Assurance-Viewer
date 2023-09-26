import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './modules/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './modules/login/login.component';
import { ToastrModule } from 'ngx-toastr';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from '@services/auth/auth-interceptor.service';
import { LoaderComponent } from '@components/loader/loader.component';
import { RouterModule } from '@angular/router';
import { ClarityModule } from '@clr/angular';
import { TopNavComponent } from './modules/top-nav/top-nav.component';
import { AppLinksComponent } from './modules/top-nav/app-links/app-links.component';
import { UserGuideComponent } from './modules/top-nav/user-guide/user-guide.component';
import { SharedModule } from './modules/shared.module';
import { UserMenuComponent } from './modules/user/user-menu/user-menu.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    LoaderComponent,
    TopNavComponent,
    AppLinksComponent,
    UserMenuComponent,
    UserGuideComponent ,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    ToastrModule.forRoot(),
    HttpClientModule,
    RouterModule,
    ClarityModule,
    SharedModule,

  ],
  exports: [
    MaterialModule
  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }
