import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { AuthComponent } from './components/auth/auth.component';
import { MainPageComponent } from './components/main-page/main-page.component';
import { MapComponent } from './components/map/map.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ProfileComponent } from './components/profile/profile.component';
import { NotFoundPageComponent } from './components/not-found-page/not-found-page.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { CreatePostComponent } from './components/create-post/create-post.component';
import { UserSettingsComponent } from './components/user-settings/user-settings.component';
import { ChatComponent } from './components/chat/chat.component';
import { NotificationsComponent } from './components/notifications/notifications.component';
import { FriendRequestsComponent } from './components/friend-requests/friend-requests.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthComponent,
    MainPageComponent,
    MapComponent,
    ProfileComponent,
    NotFoundPageComponent,
    NavbarComponent,
    CreatePostComponent,
    UserSettingsComponent,
    ChatComponent,
    NotificationsComponent,
    FriendRequestsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
