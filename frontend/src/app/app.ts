import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Parking} from './parking/parking';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    Parking
  ],
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('Parking');
}
