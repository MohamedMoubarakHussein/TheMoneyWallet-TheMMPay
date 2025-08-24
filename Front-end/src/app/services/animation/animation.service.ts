import { Injectable } from '@angular/core';
import { 
  trigger, 
  state, 
  style, 
  transition, 
  animate, 
  query, 
  stagger, 
  keyframes, 
  animation,
  AnimationReferenceMetadata
} from '@angular/animations';

@Injectable({
  providedIn: 'root'
})
export class AnimationService {

  constructor() { }

  // Base animations that can be reused
  fadeIn(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ opacity: 0 }),
      animate(`${duration} ease-out`, style({ opacity: 1 }))
    ]);
  }

  fadeOut(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ opacity: 1 }),
      animate(`${duration} ease-out`, style({ opacity: 0 }))
    ]);
  }

  slideInFromRight(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'translateX(30px)', opacity: 0 }),
      animate(`${duration} ease-out`, style({ transform: 'translateX(0)', opacity: 1 }))
    ]);
  }

  slideInFromLeft(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'translateX(-30px)', opacity: 0 }),
      animate(`${duration} ease-out`, style({ transform: 'translateX(0)', opacity: 1 }))
    ]);
  }

  slideInFromTop(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'translateY(-30px)', opacity: 0 }),
      animate(`${duration} ease-out`, style({ transform: 'translateY(0)', opacity: 1 }))
    ]);
  }

  slideInFromBottom(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'translateY(30px)', opacity: 0 }),
      animate(`${duration} ease-out`, style({ transform: 'translateY(0)', opacity: 1 }))
    ]);
  }

  zoomIn(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'scale(0.95)', opacity: 0 }),
      animate(`${duration} ease-out`, style({ transform: 'scale(1)', opacity: 1 }))
    ]);
  }

  zoomOut(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'scale(1)', opacity: 1 }),
      animate(`${duration} ease-out`, style({ transform: 'scale(0.95)', opacity: 0 }))
    ]);
  }

  bounceIn(duration: string = '0.6s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'scale(0.8)', opacity: 0 }),
      animate(`${duration} cubic-bezier(0.215, 0.610, 0.355, 1.000)`, keyframes([
        style({ transform: 'scale(1.1)', opacity: 0.6, offset: 0.5 }),
        style({ transform: 'scale(0.95)', opacity: 0.8, offset: 0.7 }),
        style({ transform: 'scale(1)', opacity: 1, offset: 1 })
      ]))
    ]);
  }

  pulse(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      style({ transform: 'scale(1)' }),
      animate(`${duration} ease-in-out`, keyframes([
        style({ transform: 'scale(1.05)', offset: 0.5 }),
        style({ transform: 'scale(1)', offset: 1 })
      ]))
    ]);
  }

  shake(duration: string = '0.5s'): AnimationReferenceMetadata {
    return animation([
      animate(`${duration} ease-in-out`, keyframes([
        style({ transform: 'translateX(-5px)', offset: 0.1 }),
        style({ transform: 'translateX(5px)', offset: 0.3 }),
        style({ transform: 'translateX(-3px)', offset: 0.5 }),
        style({ transform: 'translateX(3px)', offset: 0.7 }),
        style({ transform: 'translateX(-1px)', offset: 0.9 }),
        style({ transform: 'translateX(0)', offset: 1 })
      ]))
    ]);
  }

  // Ready-to-use animation triggers for common scenarios
  getEntranceAnimation(delay: number = 0) {
    return trigger('entrance', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('0.4s {{delay}}ms ease-out', 
          style({ opacity: 1, transform: 'translateY(0)' })
        )
      ], { params: { delay } })
    ]);
  }

  getListAnimation(delayBetweenItems: number = 50) {
    return trigger('listAnimation', [
      transition('* => *', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(delayBetweenItems, [
            animate('0.4s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ]);
  }

  getRouteAnimation() {
    return trigger('routeAnimation', [
      transition('* <=> *', [
        style({ position: 'relative' }),
        query(':enter, :leave', [
          style({
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%'
          })
        ], { optional: true }),
        query(':enter', [
          style({ opacity: 0 })
        ], { optional: true }),
        query(':leave', [
          animate('0.3s ease-out', style({ opacity: 0 }))
        ], { optional: true }),
        query(':enter', [
          animate('0.3s ease-out', style({ opacity: 1 }))
        ], { optional: true })
      ])
    ]);
  }

  getCardAnimation(delay: number = 0) {
    return trigger('cardAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(15px) scale(0.97)' }),
        animate('0.4s {{delay}}ms cubic-bezier(0.35, 0, 0.25, 1)', 
          style({ opacity: 1, transform: 'translateY(0) scale(1)' })
        )
      ], { params: { delay } }),
      transition(':leave', [
        animate('0.3s ease-out', style({ opacity: 0, transform: 'scale(0.97)' }))
      ])
    ]);
  }

  getHoverAnimation() {
    return trigger('hover', [
      state('inactive', style({
        transform: 'scale(1)',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
      })),
      state('active', style({
        transform: 'scale(1.03)',
        boxShadow: '0 10px 20px rgba(0, 0, 0, 0.15)'
      })),
      transition('inactive => active', animate('200ms ease-out')),
      transition('active => inactive', animate('150ms ease-in'))
    ]);
  }

  getButtonAnimation() {
    return trigger('buttonAnimation', [
      state('normal', style({
        transform: 'scale(1)'
      })),
      state('pressed', style({
        transform: 'scale(0.95)'
      })),
      transition('normal => pressed', animate('100ms ease-in')),
      transition('pressed => normal', animate('200ms ease-out'))
    ]);
  }

  getNotificationAnimation() {
    return trigger('notificationAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(30px)' }),
        animate('0.3s ease-out', style({ opacity: 1, transform: 'translateX(0)' }))
      ]),
      transition(':leave', [
        animate('0.3s ease-out', style({ opacity: 0, transform: 'translateX(30px)' }))
      ])
    ]);
  }

  getFormControlAnimation() {
    return trigger('formControlAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('0.3s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]);
  }

  getErrorAnimation() {
    return trigger('errorAnimation', [
      transition(':enter', [
        style({ opacity: 0, height: 0 }),
        animate('0.2s ease-out', style({ opacity: 1, height: '*' }))
      ]),
      transition(':leave', [
        animate('0.2s ease-out', style({ opacity: 0, height: 0 }))
      ])
    ]);
  }

  getChartAnimation() {
    return trigger('chartAnimation', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('0.8s 0.2s ease-out', style({ opacity: 1 }))
      ])
    ]);
  }

  getDataBarAnimation() {
    return trigger('dataBarAnimation', [
      transition(':enter', [
        style({ width: 0, opacity: 0 }),
        animate('0.8s 0.2s cubic-bezier(0.35, 0, 0.25, 1)', style({ width: '*', opacity: 1 }))
      ])
    ]);
  }

  // Animation for page loading states
  getLoadingAnimation() {
    return trigger('loadingAnimation', [
      transition('* => loading', [
        style({ opacity: 0 }),
        animate('0.2s ease-in', style({ opacity: 1 }))
      ]),
      transition('loading => *', [
        animate('0.3s ease-out', style({ opacity: 0 }))
      ])
    ]);
  }

  // Helper methods to apply animations with JavaScript
  applyPulseAnimation(element: HTMLElement, duration: number = 500): void {
    element.style.animation = `pulse ${duration}ms ease-in-out`;
    element.addEventListener('animationend', () => {
      element.style.animation = '';
    }, { once: true });
  }

  applyShakeAnimation(element: HTMLElement, duration: number = 500): void {
    element.style.animation = `shake ${duration}ms ease-in-out`;
    element.addEventListener('animationend', () => {
      element.style.animation = '';
    }, { once: true });
  }

  applySuccessAnimation(element: HTMLElement): void {
    element.style.animation = 'successPulse 0.8s ease-out';
    element.addEventListener('animationend', () => {
      element.style.animation = '';
    }, { once: true });
  }

  applyErrorAnimation(element: HTMLElement): void {
    element.style.animation = 'errorShake 0.6s ease-in-out';
    element.addEventListener('animationend', () => {
      element.style.animation = '';
    }, { once: true });
  }
}