import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-checkin',
  imports: [CommonModule],
  templateUrl: './checkin.html',
  styleUrl: './checkin.scss'
})
export class CheckinComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

  private apiUrl = import.meta.env['NG_APP_API_URL'];

  spaceLabel = signal('');
  status = signal<'loading' | 'success' | 'error'>('loading');
  message = signal('');
  reservation = signal<any>(null);

  ngOnInit() {
    const label = this.route.snapshot.paramMap.get('spaceLabel') ?? '';
    this.spaceLabel.set(label.toUpperCase());
    this.doCheckIn(label);
  }

  private doCheckIn(label: string) {
    this.http.post<any>(`${this.apiUrl}/reservations/checkin-by-space/${label}`, {}).subscribe({
      next: (res) => {
        this.reservation.set(res);
        this.status.set('success');
        this.message.set(`Check-in confirmé pour la place ${res.parkingSpaceLabel}`);
      },
      error: (err) => {
        this.status.set('error');
        this.message.set(err.error || 'Aucune réservation en attente pour aujourd\'hui sur cette place');
      }
    });
  }
}
