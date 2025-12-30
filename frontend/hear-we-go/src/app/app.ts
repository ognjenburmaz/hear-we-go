import { Component, signal } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavbarComponent} from './navbar/navbar';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    RouterOutlet,
    NavbarComponent
  ],
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('hear-we-go');
}
