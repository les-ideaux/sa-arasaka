import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ParkingService, ParkingPlace, ReservationResponse } from './parking.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-parking',
  imports: [CommonModule, FormsModule],
  templateUrl: './parking.html',
  styleUrl: './parking.scss',
})
export class Parking implements OnInit {

  private parkingService = inject(ParkingService);
  private authService = inject(AuthService);

  parkingPlaces: ParkingPlace[][] = [];
  availableSpaceIds = new Set<number>();

  startDate: string = '';
  endDate: string = '';
  needsElectric: boolean = false;
  selectedPlace: ParkingPlace | null = null;

  isLoading = false;
  isSearching = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  hasSearched = false;

  myReservations: ReservationResponse[] = [];
  readonly today = new Date();

  ngOnInit() {
    this.initDates();
    this.loadAllSpaces();
    this.loadMyReservations();
  }

  private initDates() {
    const today = new Date();
    this.startDate = this.formatDate(today);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.endDate = this.formatDate(tomorrow);
  }

  private loadAllSpaces() {
    this.isLoading = true;
    this.parkingService.getAllSpaces().subscribe({
      next: (places) => {
        this.parkingPlaces = this.groupByRow(places);
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les places de parking';
        this.isLoading = false;
      }
    });
  }

  loadMyReservations() {
    this.parkingService.getMyReservations().subscribe({
      next: (res) => this.myReservations = res,
      error: () => {}
    });
  }

  searchAvailableSpaces() {
    if (!this.startDate || !this.endDate) {
      this.errorMessage = 'Veuillez sélectionner des dates';
      return;
    }
    this.isSearching = true;
    this.selectedPlace = null;
    this.errorMessage = null;
    this.successMessage = null;

    this.parkingService.getAvailableSpaces(this.startDate, this.endDate, this.needsElectric).subscribe({
      next: (available) => {
        this.availableSpaceIds = new Set(available.map(p => p.id));
        this.hasSearched = true;
        this.isSearching = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la recherche';
        this.isSearching = false;
      }
    });
  }

  selectPlace(place: ParkingPlace) {
    if (!this.hasSearched || !this.availableSpaceIds.has(place.id)) return;
    this.selectedPlace = this.selectedPlace?.id === place.id ? null : place;
    this.errorMessage = null;
  }

  confirmReservation() {
    if (!this.selectedPlace) return;
    this.isLoading = true;
    this.errorMessage = null;

    this.parkingService.createReservation({
      startDate: this.startDate,
      endDate: this.endDate,
      parkingSpaceId: this.selectedPlace.id,
      needsElectricCharging: this.needsElectric
    }).subscribe({
      next: (res) => {
        this.successMessage = `✅ Réservation confirmée ! Place ${res.parkingSpaceLabel} du ${res.startDate} au ${res.endDate}`;
        this.selectedPlace = null;
        this.hasSearched = false;
        this.availableSpaceIds.clear();
        this.loadAllSpaces();
        this.loadMyReservations();
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = err.error || 'Erreur lors de la réservation';
        this.isLoading = false;
      }
    });
  }

  cancelReservation(reservationId: number) {
    this.parkingService.cancelReservation(reservationId).subscribe({
      next: () => {
        this.successMessage = 'Réservation annulée';
        this.loadMyReservations();
        this.loadAllSpaces();
      },
      error: (err) => this.errorMessage = err.error || 'Erreur lors de l\'annulation'
    });
  }

  getPlaceClass(place: ParkingPlace): string {
    if (!this.hasSearched) {
      if (place.confirmed) return 'confirmed';
      if (place.reserved) return 'reserved';
      return place.equippedWithElectricCharging ? 'electric' : 'available';
    }
    if (this.selectedPlace?.id === place.id) return 'selected';
    if (this.availableSpaceIds.has(place.id)) return place.equippedWithElectricCharging ? 'electric' : 'available';
    return 'unavailable';
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      'PENDING': '⏳ En attente',
      'CONFIRMED': '✅ Confirmée',
      'CANCELLED': '❌ Annulée',
      'EXPIRED': '⌛ Expirée'
    };
    return labels[status] || status;
  }

  get maxEndDate(): string {
    if (!this.startDate) return '';
    const role = this.authService.getRole();
    const maxDays = role === 'MANAGER' ? 29 : 4;
    const max = new Date(this.startDate);
    max.setDate(max.getDate() + maxDays);
    return this.formatDate(max);
  }

  formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private groupByRow(places: ParkingPlace[]): ParkingPlace[][] {
    const grouped = places.reduce((acc, place) => {
      if (!acc[place.row]) acc[place.row] = [];
      acc[place.row].push(place);
      return acc;
    }, {} as Record<string, ParkingPlace[]>);

    return Object.keys(grouped).sort()
      .map(row => grouped[row].sort((a, b) => a.number.localeCompare(b.number)));
  }
}
