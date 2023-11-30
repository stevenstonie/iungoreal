import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainPageComponent } from './components/main-page/main-page.component';
import { authGuard } from './guards/auth.guard';
import { AuthComponent } from './components/auth/auth.component';

const routes: Routes = [
  { path: 'main-page', component: MainPageComponent, canActivate: [authGuard] },
  { path: 'auth', component: AuthComponent },
  { path: '', redirectTo: '/main-page', pathMatch: 'full' },
  { path: '**', redirectTo: '/main-page', pathMatch: 'full' } // change this for a 404 page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
