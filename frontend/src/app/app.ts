import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [RouterOutlet, CommonModule],
  styleUrl: './app.scss'
})
export class App {
  protected authService = inject(AuthService);
  protected readonly title = signal('Parking');
}
