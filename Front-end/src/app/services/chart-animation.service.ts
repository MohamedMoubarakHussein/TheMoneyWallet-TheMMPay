import { Injectable } from '@angular/core';
import { trigger, style, transition, animate } from '@angular/animations';

@Injectable({
  providedIn: 'root'
})
export class ChartAnimationService {

  constructor() { }

  fadeIn = trigger('fadeIn', [
    transition(':enter', [
      style({ opacity: 0 }),
      animate('0.4s ease-out', style({ opacity: 1 }))
    ])
  ]);

  chartAnimation = trigger('chartAnimation', [
    transition(':enter', [
      style({ opacity: 0, transform: 'scale(0.95)' }),
      animate('0.6s 0.2s cubic-bezier(0.25, 0.46, 0.45, 0.94)', style({ opacity: 1, transform: 'scale(1)' }))
    ])
  ]);
}