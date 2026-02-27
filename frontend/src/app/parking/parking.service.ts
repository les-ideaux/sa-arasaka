import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ParkingPlace {
  confirmed: boolean;
  equippedWithElectricCharging: boolean;
  id: number;
  number: string;
  reserved: boolean;
  row: string;
}

export interface ReservationRequest {
  startDate: string;
  endDate: string;
  parkingSpaceId: number;
  needsElectricCharging: boolean;
}

export interface ReservationResponse {
  id: number;
  startDate: string;
  endDate: string;
  parkingSpaceId: number;
  parkingSpaceLabel: string;
  userId: number;
  userFullName: string;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED';
  checkinTime: string | null;
}

@Injectable({ providedIn: 'root' })
export class ParkingService {

  private http = inject(HttpClient);
  private apiUrl = import.meta.env['NG_APP_API_URL'];

  getAllSpaces(): Observable<ParkingPlace[]> {
    return this.http.get<ParkingPlace[]>(`${this.apiUrl}/parking`);
  }

  getAvailableSpaces(startDate: string, endDate: string, needsElectric: boolean): Observable<ParkingPlace[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate)
      .set('needsElectricCharging', needsElectric.toString());
    return this.http.get<ParkingPlace[]>(`${this.apiUrl}/reservations/available-spaces`, { params });
  }

  createReservation(request: ReservationRequest, userId: number): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(
      `${this.apiUrl}/reservations`,
      request,
      { headers: { 'X-User-Id': userId.toString() } }
    );
  }

  getMyReservations(userId: number): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(
      `${this.apiUrl}/reservations/my`,
      { headers: { 'X-User-Id': userId.toString() } }
    );
  }

  cancelReservation(reservationId: number, userId: number): Observable<ReservationResponse> {
    return this.http.post<ReservationResponse>(
      `${this.apiUrl}/reservations/${reservationId}/cancel`,
      {},
      { headers: { 'X-User-Id': userId.toString() } }
    );
  }
}
