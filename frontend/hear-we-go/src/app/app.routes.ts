import { Routes } from '@angular/router';
import {LoginComponent} from './login-component/login-component';
import {RegisterComponent} from './register-component/register-component';
import {HomePageComponent} from './home-page-component/home-page-component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
  { path: 'home', component: HomePageComponent},
];
