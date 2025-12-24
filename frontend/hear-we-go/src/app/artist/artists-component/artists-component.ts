import { Component, ChangeDetectorRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-artists-component',
  imports: [CommonModule,RouterModule],
  templateUrl: './artists-component.html',
  styleUrl: './artists-component.css',
})



export class ArtistsComponent {
   constructor(
      private router: Router,
      private cdr:ChangeDetectorRef)

      {

      }
}
