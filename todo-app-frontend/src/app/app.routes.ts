import {RouterModule, Routes} from '@angular/router';
import { WelcomeComponent } from './welcome-component/welcome-component.component';
import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import {HomeComponent} from './home/home.component';
import {NgModule} from '@angular/core';

export const routes: Routes = [
  { path: '', component: WelcomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent },
];

