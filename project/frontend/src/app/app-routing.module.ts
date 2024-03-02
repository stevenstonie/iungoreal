import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainPageComponent } from './components/main-page/main-page.component';
import { authGuard } from './guards/auth.guard';
import { AuthComponent } from './components/auth/auth.component';
import { ProfileComponent } from './components/profile/profile.component';
import { NotFoundPageComponent } from './components/not-found-page/not-found-page.component';
import { CreatePostComponent } from './components/create-post/create-post.component';
import { UserSettingsComponent } from './components/user-settings/user-settings.component';
import { TestMessageComponent } from './components/test-message/test-message.component';

const routes: Routes = [
  {
    path: '', canActivate: [authGuard], children: [
      { path: '', component: MainPageComponent },
      { path: 'user/:username', component: ProfileComponent },
      { path: 'settings', component: UserSettingsComponent },
      { path: 'createPost', component: CreatePostComponent },
      {path: 'websockettest', component: TestMessageComponent},
      { path: '404', component: NotFoundPageComponent }
    ]
  },
  { path: 'auth', component: AuthComponent },
  { path: '**', redirectTo: '/404' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
