import { Component, AfterViewInit, ElementRef, QueryList, ViewChildren } from '@angular/core';
import { CommonModule } from '@angular/common';
import QRCode from 'qrcode';

interface ParkingSpot {
  label: string;
  row: string;
  number: string;
  hasCharger: boolean;
}

@Component({
  selector: 'app-qrcodes',
  imports: [CommonModule],
  templateUrl: './qrcodes.html',
  styleUrl: './qrcodes.scss'
})
export class QrCodesDebugComponent implements AfterViewInit {

  @ViewChildren('qrCanvas') canvases!: QueryList<ElementRef<HTMLCanvasElement>>;

  readonly rows = ['A', 'B', 'C', 'D', 'E', 'F'];
  readonly numbers = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10'];

  spots: ParkingSpot[] = [];

  constructor() {
    for (const row of this.rows) {
      for (const number of this.numbers) {
        this.spots.push({
          label: `${row}${number}`,
          row,
          number,
          hasCharger: row === 'A' || row === 'F'
        });
      }
    }
  }

  ngAfterViewInit() {
    this.canvases.forEach((canvasRef, index) => {
      const spot = this.spots[index];
      const url = `${window.location.origin}/checkin/${spot.label}`;
      QRCode.toCanvas(canvasRef.nativeElement, url, {
        width: 120,
        margin: 1
      });
    });
  }

  spotsForRow(row: string): ParkingSpot[] {
    return this.spots.filter(s => s.row === row);
  }

  print() {
    window.print();
  }
}
