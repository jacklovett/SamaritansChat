import { Routes, RouterModule } from '@angular/router';

import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

import { LoginComponent } from './pages/login/login.component';
import { ChatComponent } from './pages/chat/chat.component';
import { UserComponent } from './pages/user/user.component';
import { ChatLogComponent } from './pages/chatlog/chatlog.component';
import { UsersComponent } from './pages/users/users.component';
import { ConfigComponent } from './pages/config/config.component';
import { ChatLogsComponent } from './pages/chatlogs/chatlogs.component';
import { TranscriptComponent } from './pages/transcript/transcript.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'chat', component: ChatComponent, canActivate: [AuthGuard] },
  { path: 'user/:id', component: UserComponent, canActivate: [AuthGuard] },
  { path: 'chatlog', component: ChatLogComponent, canActivate: [AuthGuard] },
  { path: 'users', component: UsersComponent, canActivate: [AdminGuard] },
  { path: 'config', component: ConfigComponent, canActivate: [AdminGuard] },
  { path: 'register', component: UserComponent, canActivate: [AdminGuard] },
  { path: 'chatlogs', component: ChatLogsComponent, canActivate: [AdminGuard] },
  {
    path: 'transcript/:id',
    component: TranscriptComponent,
    canActivate: [AdminGuard],
  },

  // otherwise redirect to chat
  { path: '**', redirectTo: 'chat' },
];

export const AppRouting = RouterModule.forRoot(routes);
