import {Component, inject, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {HttpClient, provideHttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('Parking');

  httpClient = inject(HttpClient)

  onButtonClick(){
    console.log("I've been clicked!")
    console.log(import.meta.env)
    this.httpClient.get(`${import.meta.env.NG_APP_API_URL}/hello`).subscribe(data => console.log(data));
    this.httpClient.get(`${import.meta.env.NG_APP_API_URL}/sendTestEmail`).subscribe(data => console.log(data));
  }
}
