import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRouting } from './app-routing.module';
import { MaterialModule } from './material.module';

import { AppComponent } from './app.component';
import { ChatComponent } from './components/chat/chat.component';
import { LoginComponent } from './components/login/login.component';
import { AlertComponent } from './components/alert/alert.component';
import { DialogComponent } from './components/dialog/dialog.component';

import { JwtInterceptor } from './helpers/jwt.interceptor';
import { ErrorInterceptor } from './helpers/error.interceptor';

import { MAT_TOOLTIP_DEFAULT_OPTIONS, MatTooltipDefaultOptions } from '@angular/material/tooltip';

export const customTooltipDefaults: MatTooltipDefaultOptions = {
  showDelay: 1000,
  hideDelay: 1000,
  touchendHideDelay: 1000,
};

@NgModule({
  declarations: [
    AppComponent,
    AlertComponent,
    ChatComponent,
    LoginComponent,
    DialogComponent
  ],
  imports: [
    BrowserModule,
    MaterialModule,
    AppRouting,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule
  ],
  entryComponents: [
    DialogComponent
   ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: MAT_TOOLTIP_DEFAULT_OPTIONS, useValue: customTooltipDefaults }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
