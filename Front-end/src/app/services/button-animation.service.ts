import { Injectable } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Injectable({
  providedIn: 'root'
})
export class ButtonAnimationService {

  constructor() { }

  buttonState = trigger('buttonState', [
    state('normal', style({ transform: 'scale(1)' })),
    state('pressed', style({ transform: 'scale(0.95)' })),
    transition('* => pressed', animate('100ms ease-in')),
    transition('pressed => normal', animate('100ms ease-out'))
  ]);
}