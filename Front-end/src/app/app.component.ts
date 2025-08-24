import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { LoadingService } from './services/loading-service.service';
import { MobileNavigationComponent } from './components/mobile-navigation/mobile-navigation.component';
import { trigger, style, transition, animate, query } from '@angular/animations';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MobileNavigationComponent],
  templateUrl: './app.component.html',
  animations: [
    trigger('routeAnimations', [
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
        query(':leave', [
          style({ opacity: 1 }),
          animate('0.3s ease-out', style({ opacity: 0, transform: 'translateY(-20px)' }))
        ], { optional: true }),
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          animate('0.6s ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
        ], { optional: true })
      ])
    ]),
    trigger('slideInFromRight', [
      transition(':enter', [
        style({ transform: 'translateX(30px)', opacity: 0 }),
        animate('0.5s ease-out', style({ transform: 'translateX(0)', opacity: 1 }))
      ])
    ]),
    trigger('slideInFromLeft', [
      transition(':enter', [
        style({ transform: 'translateX(-30px)', opacity: 0 }),
        animate('0.5s ease-out', style({ transform: 'translateX(0)', opacity: 1 }))
      ])
    ]),
    trigger('slideInFromBottom', [
      transition(':enter', [
        style({ transform: 'translateY(30px)', opacity: 0 }),
        animate('0.5s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ])
    ]),
    trigger('slideInFromTop', [
      transition(':enter', [
        style({ transform: 'translateY(-30px)', opacity: 0 }),
        animate('0.5s ease-out', style({ transform: 'translateY(0)', opacity: 1 }))
      ])
    ])
  ]
})
export class AppComponent implements OnInit {
  isLoading = false;

  constructor(
    private loadingService: LoadingService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadingService.isLoading$.subscribe(loading => {
      this.isLoading = loading;
    });
    
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      window.scrollTo(0, 0);
    });
  }
  
  prepareRoute(outlet: RouterOutlet) {
    return outlet && outlet.activatedRouteData && 
           outlet.activatedRouteData['animation'];
  }
}