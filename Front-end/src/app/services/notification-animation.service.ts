import { Injectable } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Injectable({
  providedIn: 'root'
})
export class NotificationAnimationService {

  constructor() { }

  notificationAnimation = trigger('notificationAnimation', [
    state('slide-enter', style({ transform: 'translateX(0)', opacity: 1 })),
    state('slide-leave', style({ transform: 'translateX(100%)', opacity: 0 })),
    transition('void => slide-enter', [
      style({ transform: 'translateX(110%)', opacity: 0 }),
      animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)')
    ]),
    transition('slide-enter => slide-leave', [
      animate('200ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(110%)', opacity: 0 }))
    ]),
  ]);
}