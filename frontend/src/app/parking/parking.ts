import {Component, inject, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';

interface ParkingPlace {
  confirmed: boolean;
  equippedWithElectricCharging: boolean;
  id: number;
  number: string;
  reserved: boolean;
  row: string;
}

@Component({
  selector: 'app-parking',
  imports: [],
  templateUrl: './parking.html',
  styleUrl: './parking.scss',
})
export class Parking implements OnInit {
  parkingPlaces: ParkingPlace[][] = []

  httpClient = inject(HttpClient);

  ngOnInit() {
    this.httpClient.get<ParkingPlace[]>(`${import.meta.env.NG_APP_API_URL}/parking`).subscribe((places: ParkingPlace[]) => {
      // Grouper les places par rangée
      const grouped = places.reduce((acc, place) => {
        if (!acc[place.row]) {
          acc[place.row] = [];
        }
        acc[place.row].push(place);
        return acc;
      }, {} as Record<string, ParkingPlace[]>);

      // Convertir en tableau et trier par rangée (A, B, C, etc.)
      this.parkingPlaces = Object.keys(grouped)
        .sort()
        .map(row => grouped[row].sort((a, b) => a.number.localeCompare(b.number)));

      console.log(this.parkingPlaces);
    })
  }

  selectPlace(place: ParkingPlace) {
    if (place.reserved) return;

    // Logique de sélection/confirmation
    console.log('Place sélectionnée:', place);
    // Tu peux faire un appel HTTP pour confirmer la réservation
  }
}
