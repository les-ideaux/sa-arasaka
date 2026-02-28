import { Routes } from '@angular/router';
import { authGuard } from './auth/auth.guard';
import { LoginComponent } from './login/login';
import { Parking } from './parking/parking';
import { CheckinComponent } from './checkin/checkin';
import { QrCodesDebugComponent } from './debug/qrcodes';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'parking', component: Parking, canActivate: [authGuard] },
  { path: 'checkin/:spaceLabel', component: CheckinComponent, canActivate: [authGuard] },
  { path: 'debug/qrcodes', component: QrCodesDebugComponent },
  { path: '', redirectTo: '/parking', pathMatch: 'full' },
];
