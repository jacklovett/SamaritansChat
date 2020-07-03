import { NgModule } from '@angular/core'
import { BrowserModule } from '@angular/platform-browser'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { ReactiveFormsModule } from '@angular/forms'
import { DatePipe } from '@angular/common'
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'

import { AppRouting } from './app-routing.module'
import { MaterialModule } from './material.module'
import { AppComponent } from './app.component'

import { ChatComponent } from './pages/chat/chat.component'
import { LoginComponent } from './pages/login/login.component'
import { UsersComponent } from './pages/users/users.component'
import { ChatLogsComponent } from './pages/chatlogs/chatlogs.component'
import { TranscriptComponent } from './pages/transcript/transcript.component'
import { ConfigComponent } from './pages/config/config.component'
import { ChatLogComponent } from './pages/chatlog/chatlog.component'
import { UserComponent } from './pages/user/user.component'

import { AlertComponent } from './components/alert/alert.component'
import { DialogComponent } from './components/dialog/dialog.component'
import { NotificationsComponent } from './components/notifications/notifications.component'
import { PasswordComponent } from './components/password/password.component'
import { MessagesComponent } from './components/messages/messages.component'
import { TableComponent } from './components/table/table.component'
import { ContactsComponent } from './components/contacts/contacts.component'

import { ErrorInterceptor } from './helpers/error.interceptor'
import { JwtInterceptor } from './helpers/jwt.interceptor'
import { PasswordDirective } from './helpers/password.directive'

import { UsernameAvailableValidator } from './validators/username-available-validator'
import { EmailAvailableValidator } from './validators/email-available-validator'
import { CheckPasswordValidator } from './validators/check-password-validator'

import {
  MAT_TOOLTIP_DEFAULT_OPTIONS,
  MatTooltipDefaultOptions,
} from '@angular/material/tooltip'

import {
  InjectableRxStompConfig,
  RxStompService,
  rxStompServiceFactory,
} from '@stomp/ng2-stompjs'
import { rxStompConfig } from './rx-stomp.config'

export const customTooltipDefaults: MatTooltipDefaultOptions = {
  showDelay: 1000,
  hideDelay: 1000,
  touchendHideDelay: 1000,
}

@NgModule({
  declarations: [
    AppComponent,
    AlertComponent,
    ChatComponent,
    DialogComponent,
    LoginComponent,
    UsersComponent,
    UsernameAvailableValidator,
    EmailAvailableValidator,
    CheckPasswordValidator,
    ChatLogsComponent,
    TranscriptComponent,
    NotificationsComponent,
    ConfigComponent,
    ChatLogComponent,
    UserComponent,
    PasswordDirective,
    PasswordComponent,
    MessagesComponent,
    TableComponent,
    ContactsComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MaterialModule,
    AppRouting,
    HttpClientModule,
    ReactiveFormsModule,
  ],
  entryComponents: [DialogComponent, PasswordComponent],
  providers: [
    DatePipe,
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: MAT_TOOLTIP_DEFAULT_OPTIONS, useValue: customTooltipDefaults },
    {
      provide: InjectableRxStompConfig,
      useValue: rxStompConfig,
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
