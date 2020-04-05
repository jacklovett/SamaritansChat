import { Routes, RouterModule } from '@angular/router';

import { AuthGuard } from './guards/auth.guard';

import { ChatComponent } from './components/chat/chat.component';
import { LoginComponent } from './components/login/login.component';

const routes: Routes = [
  { path: 'chat', component: ChatComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  // otherwise redirect to chat
  { path: '**', redirectTo: 'chat' },
];

export const AppRouting = RouterModule.forRoot(routes);
